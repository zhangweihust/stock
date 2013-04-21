package com.zhangwei.stock.service;


import java.util.Iterator;

import com.zhangwei.stock.gson.DailyList;
import com.zhangwei.stock.gson.GoodStock;
import com.zhangwei.stock.gson.Stock;
import com.zhangwei.stock.net.TencentStockHelper;
import com.zhangwei.stock.net.WifiHelper;
import com.zhangwei.stock.receiver.DailyReceiver;
import com.zhangwei.stock.receiver.NetworkConnectChangedReceiver;
import com.zhangwei.stocklist.StockListHelper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import cn.zipper.framwork.service.ZService;

public class DailyStockScanService extends ZService {
	
	private  AlarmManager alarms;
	private  PendingIntent alarmIntent;
	private  final long alarm_interval = 24*60*60*1000;  //24 hour
	
	private DailyStockScanTask lastLookup;   
	private final int HANDLER_FLAG_TASK_COMPLETE =  0x12345678;
	private final int HANDLER_FLAG_WIFI_CONNECTED = 0x12345679;
	
	NetworkConnectChangedReceiver  myBroadcastReceiver;

	@Override
	public void onCreate() {
		super.onCreate();
	
	    alarms = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

	    String ALARM_ACTION = DailyReceiver.ACTION_REFRESH_DAILYSCAN_ALARM; 
	    Intent intentToFire = new Intent(ALARM_ACTION);
	    alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
	    
	    
	    myBroadcastReceiver = new NetworkConnectChangedReceiver();
	    IntentFilter filter = new IntentFilter();
	    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
	    filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
	    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
	    this.registerReceiver(myBroadcastReceiver, filter);
	    
	    LocalBroadcastManager.getInstance(this).registerReceiver(mWifiStatusReceiver,
	    	      new IntentFilter(NetworkConnectChangedReceiver.ACTION_WIFI_CONNECTED));

	}
	
	private BroadcastReceiver mWifiStatusReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
		    // Get extra data included in the Intent
		    //String message = intent.getStringExtra("message");
		    //Log.d("receiver", "Got message: " + message);
		    
		    //Message msg = handler.obtainMessage(HANDLER_FLAG_WIFI_CONNECTED);
		    handler.sendEmptyMessageDelayed(HANDLER_FLAG_WIFI_CONNECTED, 10000);
		  }
		};

		
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		this.unregisterReceiver(myBroadcastReceiver);
		
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mWifiStatusReceiver);
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) { 
		
	    int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
	    long timeToRefresh = SystemClock.elapsedRealtime() + alarm_interval;
	    alarms.setRepeating(alarmType, timeToRefresh, alarm_interval, alarmIntent);  
	    
	    //alarms.cancel(alarmIntent);
	    DailyList dailylist = StockListHelper.getInstance().getDailyList();
	    
		refreshVersionCheck(dailylist.getlastScanID());

		return Service.START_NOT_STICKY;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case HANDLER_FLAG_TASK_COMPLETE:
			this.stopSelf();
			break;
			
		case HANDLER_FLAG_WIFI_CONNECTED:
			//只有在service活着的时候才能接收局部广播并重启service
			//应用在一次异步任务已退出，但没有完成（没有发出HANDLER_FLAG_TASK_COMPLETE）
			//这时的service还没有结束，等待网络状态的改变
			Intent startIntent = new Intent(this, DailyStockScanService.class);
		    this.startService(startIntent);
		}
		return false;
	}
	
	public void refreshVersionCheck(String stockID) {
	    if (lastLookup==null ||
	    		lastLookup.getStatus().equals(AsyncTask.Status.FINISHED)) {
	      lastLookup = new DailyStockScanTask(handler);
	      lastLookup.execute(stockID);

	    }
	}

	/**
	 * 
	 *  @param 输入 上次记录的stock: sh600031(上次已完成)
	 *  @param 输出 这次完成的stock： sh600032
	 * 
	 *  @author zhangwei
	 * */
	private class DailyStockScanTask extends AsyncTask<String,Void,String>{

		private Handler handler;
		private boolean update;
		private boolean findIndex;
		private boolean isAbort;
		private String completeID;
		
		public DailyStockScanTask(Handler handler) {
			// TODO Auto-generated constructor stub
			this.handler = handler;
			update = false;
			findIndex = false;
			isAbort = false;
			completeID = null;
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String lastStockID = params[0];
			DailyList dailylist = StockListHelper.getInstance().getDailyList();

			//for(String key: dailylist.getDailyList().keySet()){
			for(Iterator<GoodStock> iterator = dailylist.getDailyMap().values().iterator(); iterator.hasNext(); ){
				GoodStock gs =  iterator.next(); 
				
				//check
				if(gs==null || gs.id==null){
					continue;
				}
				
				if(lastStockID!=null && !findIndex){
					//should find index first
					if(lastStockID.equals(gs.id)){
						findIndex = true;
					}
					continue;
				}

				//check net, only wifi can run
				if(!WifiHelper.VALUE_WIFI.equals(WifiHelper.getNetType())){
					isAbort = true;
					break;
				}
				
				if(isCancelled()){
					isAbort = true;
					break;
				}
				
				Stock stock = TencentStockHelper.getInstance().get_stock_from_doctor(gs.id);
				if(stock!=null){
					dailylist.updateStock(stock);
					
					//实时记录扫描的id到dailyList中
					dailylist.setlastScanID(stock.id);
					update = true;
					completeID = stock.id;
					
					//save stock into internal storage
					StockListHelper.getInstance().persistStock(stock);
				}

			}

			if(update){
				if(!isAbort){
					//完成这次扫描(中途被终止的不算)，记录时间
					dailylist.setlastScanTime(System.currentTimeMillis());
				}
				StockListHelper.getInstance().persistDailyList(dailylist);
			}

			
			return completeID;
		}
		
		protected void onPostExecute(String result) {  
			if(!isAbort){
				//Message msg = handler.obtainMessage(HANDLER_FLAG_TASK_COMPLETE);
				//msg.sendToTarget();
				handler.sendEmptyMessage(HANDLER_FLAG_TASK_COMPLETE);
			}
		}  
	}
}

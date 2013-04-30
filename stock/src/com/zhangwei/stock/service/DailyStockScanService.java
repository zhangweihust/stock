package com.zhangwei.stock.service;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zhangwei.stock.gson.DailyList;
import com.zhangwei.stock.gson.GoodStock;
import com.zhangwei.stock.gson.Stock;
import com.zhangwei.stock.gson.StockList;
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
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.service.ZService;

public class DailyStockScanService extends ZService {
	private final String TAG = "DailyStockScanService";
	
	private  AlarmManager alarms;
	private  PendingIntent alarmIntent;
	DailyList dailylist;
	StockList stocklist;
	private  final long alarm_interval = 24*60*60*1000;  //24 hour
	
	private DailyGoodStockScanTask lastLookup;   
	private KudnsRefreshTask lastRefresh;  
	private final int HANDLER_FLAG_TASK_COMPLETE =  0x12345678;
	private final int HANDLER_FLAG_WIFI_CONNECTED = 0x12345679;
	
	NetworkConnectChangedReceiver  myBroadcastReceiver;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "onCreate");
	
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
		    Log.d("mWifiStatusReceiver", "Got message HANDLER_FLAG_WIFI_CONNECTED" );
		    
		    //Message msg = handler.obtainMessage(HANDLER_FLAG_WIFI_CONNECTED);
		    handler.sendEmptyMessageDelayed(HANDLER_FLAG_WIFI_CONNECTED, 10000);
		  }
		};

		
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "onDestroy");
		
		this.unregisterReceiver(myBroadcastReceiver);
		
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mWifiStatusReceiver);
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) { 
		Log.e(TAG, "onStartCommand, flags:" + flags + " startId" + startId);
		
	    int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
	    long timeToRefresh = SystemClock.elapsedRealtime() + alarm_interval;
	    alarms.setRepeating(alarmType, timeToRefresh, alarm_interval, alarmIntent);  
	    
	    //alarms.cancel(alarmIntent);
	    dailylist = StockListHelper.getInstance().getDailyList();
	    
		DailyGoodStockScan(dailylist.getlastScanID());
		
		stocklist = StockListHelper.getInstance().getList();

		return Service.START_NOT_STICKY;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case HANDLER_FLAG_TASK_COMPLETE:
			Log.e(TAG, "handle task complete, stopSelf");
			this.stopSelf();
			break;
			
		case HANDLER_FLAG_WIFI_CONNECTED:
			//只有在service活着的时候才能接收局部广播并重启service
			//应用在一次异步任务已退出，但没有完成（没有发出HANDLER_FLAG_TASK_COMPLETE）
			//这时的service还没有结束，等待网络状态的改变
			Log.e(TAG, "handle wifi connected, refreshVersionCheck");
/*			Intent startIntent = new Intent(this, DailyStockScanService.class);
		    this.startService(startIntent);*/
			dailylist = StockListHelper.getInstance().getDailyList();
			DailyGoodStockScan(dailylist.getlastScanID());
			
			//refreshKudns_com();
		    break;
		}
		return false;
	}
	
	public void DailyGoodStockScan(String stockID) {
	    if (lastLookup==null ||
	    		lastLookup.getStatus().equals(AsyncTask.Status.FINISHED)) {
	      lastLookup = new DailyGoodStockScanTask(handler);
	      lastLookup.execute(stockID);

	    }
	}
	
	
	public void refreshKudns_com() {
	    if (lastRefresh==null ||
	    		lastRefresh.getStatus().equals(AsyncTask.Status.FINISHED)) {
	    	lastRefresh = new KudnsRefreshTask();
	    	lastRefresh.execute();

	    }
	}
	
	private class KudnsRefreshTask extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			//GET /islogin.php HTTP/1.1 (必须，得到PHPSESSID)
			HashMap<String, String> headers = new HashMap<String, String>();
			String cookie_str = "Hm_lvt_33ea14b096016df36e0a555e947b927e=1365233496,1365298626,1366705886;";
			headers.put("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
			headers.put("Cookie", cookie_str);
			ZHttp2 http2 = new ZHttp2();
			http2.setHeaders(headers);
			ZHttpResponse httpResponse = http2.get("http://www.kudns.com/islogin.php");
			Map<String, List<String>> ret = httpResponse.getHeaders();
			List<String> list = ret.get("Set-Cookie");
			Log.e(TAG, "set-cookie:" + list.get(0));
			
			//POST /user/ilogin.php HTTP/1.1  （必须，登陆， 让phpsession合法）
			headers.put("Cookie", cookie_str + " " + list.get(0) + ";");
			String post_data = "username=hustwei&pwd=lmx%401984&submit=%26%23160%3B%26%23160%3B%26%23160%3B%26%23160%3B";
			httpResponse = http2.post("http://www.kudns.com/user/ilogin.php", post_data.getBytes());
			
			ret = httpResponse.getHeaders();

			
			
			
			//GET /user/host/add_date.php?Tid=81343 HTTP/1.1
			headers.put("Cookie", cookie_str + " " + list.get(0) + ";");
			http2.get("http://www.kudns.com/user/host/add_date.php?Tid=81343");
			
			
			return null;
		}
		
	}

	/**
	 * 
	 *  @param 输入 上次记录的stock: sh600031(上次已完成)
	 *  @param 输出 这次完成的stock： sh600032
	 * 
	 *  @author zhangwei
	 * */
	private class DailyGoodStockScanTask extends AsyncTask<String,Void,String>{

		private Handler handler;
		private boolean update;
		private boolean findIndex;
		private boolean isAbort;
		private String completeID;
		
		public DailyGoodStockScanTask(Handler handler) {
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
				
				Log.e(TAG, "lastStockID:" + lastStockID);

				//check net, only wifi can run
				if(!WifiHelper.VALUE_WIFI.equals(WifiHelper.getNetType())){
					Log.e(TAG, "WifiHelper,  status:" + WifiHelper.getNetType() + " gs" + gs.id);
					isAbort = true;
					break;
				}
				
				if(isCancelled()){
					Log.e(TAG, "isCancelled, gs:" + gs.id);
					isAbort = true;
					break;
				}
				
				Stock stock = TencentStockHelper.getInstance().get_stock_from_tencent(gs.id);
				if(stock!=null){
					Log.e(TAG, "a stock done,  stock.id:" + stock.id);
					dailylist.updateStock(stock);
					
					//实时记录扫描的id到dailyList中
					dailylist.setlastScanID(stock.id);
					update = true;
					completeID = stock.id;
					
					//save stock into internal storage
					StockListHelper.getInstance().persistStock(stock);
				}

			}
			
			Log.e(TAG, "loop over, update:" + update + " isAbort:" + isAbort + " completeID:" + completeID);

			if(update){
				if(!isAbort){
					//完成这次扫描(中途被终止的不算)，记录时间
					dailylist.setlastScanTime(System.currentTimeMillis());
				}
				Log.e(TAG, "persistDailyList!");
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

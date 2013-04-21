package com.zhangwei.service;


import com.zhangwei.stock.receiver.DailyReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import cn.zipper.framwork.service.ZService;

public class DailyStockScanService extends ZService {
	
	private  AlarmManager alarms;
	private  PendingIntent alarmIntent;
	private  final long alarm_interval = 24*60*60*1000;  //24 hour
	
	private DailyStockScanTask lastLookup; 

	
	@Override
	public void onCreate() {
		super.onCreate();
	
	    alarms = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

	    String ALARM_ACTION = DailyReceiver.ACTION_REFRESH_VERSIONCHECK_ALARM; 
	    Intent intentToFire = new Intent(ALARM_ACTION);
	    alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
	    

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) { 
		
	    int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
	    long timeToRefresh = SystemClock.elapsedRealtime() + alarm_interval;
	    alarms.setRepeating(alarmType, timeToRefresh, alarm_interval, alarmIntent);  
	      
	    //refreshVersionCheck();
	    
	      //alarms.cancel(alarmIntent);
		return Service.START_NOT_STICKY;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void refreshVersionCheck() {
	    if (lastLookup==null ||
	    		lastLookup.getStatus().equals(AsyncTask.Status.FINISHED)) {
	      lastLookup = new DailyStockScanTask();
	      lastLookup.execute(1);

	    }
	}

	private class DailyStockScanTask extends AsyncTask<Integer,Void,Integer>{

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
		protected void onPostExecute(Integer result) {  


		}  
	}
}

package com.zhangwei.stock.receiver;

import com.zhangwei.stock.service.DailyStockScanService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeviceStartReceiver extends BroadcastReceiver {

	private static final String TAG = "DeviceStartReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive begin");
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
			Intent i = new Intent(context, DailyStockScanService.class);
			//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(i);
			Log.i(TAG,"DailyStockScanService begin to start");
			
			
		}

	}


	
}

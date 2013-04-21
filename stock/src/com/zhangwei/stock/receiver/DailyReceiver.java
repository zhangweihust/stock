package com.zhangwei.stock.receiver;

import com.zhangwei.service.DailyStockScanService;

import android.content.Context;
import android.content.Intent;
import cn.zipper.framwork.core.ZBroadcastReceiver;

public class DailyReceiver extends ZBroadcastReceiver {
	public static final String ACTION_REFRESH_DAILYSCAN_ALARM = "ACTION_REFRESH_DAILYSCAN_ALARM";
		
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
	    Intent startIntent = new Intent(context, DailyStockScanService.class);
	    context.startService(startIntent);
	}

}



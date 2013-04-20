package com.zhangwei.stocklist;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import cn.zipper.framwork.core.ZApplication;

import com.google.gson.Gson;
import com.zhangwei.common.storage.StorageManager;
import com.zhangwei.gson.DailyList;
import com.zhangwei.gson.StockList;

public class StockListHelper {

	private static  StockListHelper ins;
	private static String TAG = "StockList";
	private Context context;

	private StockListHelper(){
		context = ZApplication.getInstance();
	}
	
	public static StockListHelper getInstance(){
		if(ins==null){
			ins = new StockListHelper();
		}
		
		return ins;
	}
	
	public StockList getList(){
		StockList sl = (StockList) StorageManager.getInstance(null).getItem(StockList.ID, StockList.class);
	
	
		if(sl==null || sl.status!=1){
			//use default stock list in assets
			AssetManager manager = context.getAssets();
			try {
				InputStream mInput = manager.open("stock_list");
				// myData.txt can't be more than 2 gigs.
	            int size = mInput.available();
	            byte[] buffer = new byte[size];
	            mInput.read(buffer);
	            mInput.close();
	 
	            // byte buffer into a string
	            String text = new String(buffer);
	            Gson gson = new Gson();
	            sl = gson.fromJson(text, StockList.class);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			

		}
		
		return sl;
	}
	
	public DailyList getDailyList(){
		DailyList dl = (DailyList) StorageManager.getInstance(null).getItem(DailyList.ID, DailyList.class);
		
		return dl;
	}
	
}

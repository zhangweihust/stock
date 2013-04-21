package com.zhangwei.stocklist;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import cn.zipper.framwork.core.ZApplication;

import com.google.gson.Gson;
import com.zhangwei.common.storage.StorageManager;
import com.zhangwei.gson.DailyList;
import com.zhangwei.gson.Stock;
import com.zhangwei.gson.StockList;

public class StockListHelper {

	private static  StockListHelper ins;
	private static String TAG = "StockList";
	
	private Context context;
	private DailyList dailylist;
	private StockList stocklist;
	
	private StockListHelper(){
		context = ZApplication.getInstance();
		dailylist = null;
		stocklist = null;
	}
	
	public static StockListHelper getInstance(){
		if(ins==null){
			ins = new StockListHelper();
		}
		
		return ins;
	}
	
	public StockList getList(){
		//1. from memory
		if(stocklist!=null){
			return stocklist;
		}
		
		//2. from internal storage
		stocklist = (StockList) StorageManager.getInstance(null).getItem(StockList.ID, StockList.class);
		if(stocklist!=null){
			return stocklist;
		}
	
		//3. from assets last
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
            stocklist = gson.fromJson(text, StockList.class);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return stocklist;
	}
	
	public void persistStockList(StockList stocklist){

		//save to internal storage
		if(stocklist!=null){
			StorageManager.getInstance(null).putItem(StockList.ID, stocklist, StockList.class);
		}

	}
	
	public DailyList getDailyList(){

		//1. from memory
		if(dailylist!=null){
			return dailylist;
		}
		
		//2. from internal storage
		dailylist = (DailyList) StorageManager.getInstance(null).getItem(DailyList.ID, DailyList.class);
		if(dailylist!=null){
			return dailylist;
		}
		
		//3. from assets last
		//use default stock list in assets
		AssetManager manager = context.getAssets();
		try {
			InputStream mInput = manager.open("daily_list");
			// myData.txt can't be more than 2 gigs.
            int size = mInput.available();
            byte[] buffer = new byte[size];
            mInput.read(buffer);
            mInput.close();
 
            // byte buffer into a string
            String text = new String(buffer);
            Gson gson = new Gson();
            dailylist = gson.fromJson(text, DailyList.class);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return dailylist;
	}
	
	public void persistDailyList(DailyList dlist){

		//save to internal storage
		if(dlist!=null){
			StorageManager.getInstance(null).putItem(DailyList.ID, dlist, DailyList.class);
		}

	}
	
	
	public void persistStock(Stock stock){

		//save to internal storage
		if(stock!=null){
			StorageManager.getInstance(null).putItem(stock.id, stock, Stock.class);
		}

	}
}

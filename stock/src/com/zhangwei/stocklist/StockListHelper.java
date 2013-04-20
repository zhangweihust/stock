package com.zhangwei.stocklist;

import com.zhangwei.common.storage.StorageManager;
import com.zhangwei.gson.StockList;

public class StockListHelper {

	private static  StockListHelper ins;
	private static String TAG = "StockList";

	private void StockListHelper(){
		
	}
	
	public static StockListHelper getInstance(){
		if(ins==null){
			ins = new StockListHelper();
		}
		
		return ins;
	}
	
	public StockList getList(){
		StockList sl = (StockList) StorageManager.getInstance(null).getItem(StockList.ID, StockList.class);
	
	
		if(sl.status!=1){
			//use default stock list in assets
			
			return null;
		}
		
		return sl;
	}
	
}

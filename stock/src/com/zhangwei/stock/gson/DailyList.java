package com.zhangwei.stock.gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.util.Log;

/**
 *  每天都要扫描的列表，质地优秀的股票
 *  每月更新该列表本身
 * 
 *  @author zhangwei
 * 
 * */
public class DailyList {
	public transient static String ID = "_DailyList_";
	//public transient static DailyList ins;

	LinkedHashMap<String, GoodStock> map;
	
	/**
	 *  {@literal 上次扫描的stockid， sh600031}
	 * */
	String lastScanID;
	
	/**
	 *  {@literal 上次完成列表扫描的时间}
	 * */
	long lastScanTime;
	
	private DailyList(){
		map = new LinkedHashMap<String, GoodStock>();
	}
	
/*	public static DailyList getInstance(){
		if(ins==null){
			ins = new DailyList();
			ins.lastScanTime = 0;
			ins.map = new HashMap<String, GoodStock>();
		}
		
		return ins;
	}*/
	
	public String getlastScanID(){
		return lastScanID;
	}
	
	public void setlastScanID(String id){
		lastScanID = id;
	}
	
	public long getlastScanTime(){
		return lastScanTime;
	}
	
	public void setlastScanTime(long time){
		lastScanTime = time;
	}
	
	public LinkedHashMap<String, GoodStock> getDailyMap(){
		return map;
	}
	
	public void updateDailyList(DailyList obj){
		if(obj!=null){
			map.clear();
			map.putAll(obj.getDailyMap());
			lastScanTime = obj.getlastScanTime();
		}

	}
	
	/**
	 *  将Stock信息更新到GoodStock
	 *  <li>Stock侧重于股票详细信息
	 *  <li>GoodStock侧重于股票的变化
	 * 
	 * */
	public void updateStock(Stock stock){
		updateStock(stock.id, stock.rank, stock.trend, stock.quality);
	}
	
	public void updateStock(String id, String Rank, String Trend, String Quality){
		GoodStock obj;
		if(map.containsKey(id)){
			obj = map.get(id);
			obj.update(id, Rank, Trend, Quality);
		}else{
			obj = new GoodStock();
			obj.update(id, Rank, Trend, Quality);
			map.put(id, obj);
		}
	}
}

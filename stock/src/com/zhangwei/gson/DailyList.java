package com.zhangwei.gson;

import java.util.ArrayList;
import java.util.HashMap;

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
	public transient static DailyList ins;

	HashMap<String, GoodStock> map;
	
	/**
	 *  {@literal 上次完成列表扫描的时间}
	 * */
	long lastScanTime;
	
	private DailyList(){
		map = new HashMap<String, GoodStock>();
	}
	
	public static DailyList getInstance(){
		if(ins==null){
			ins = new DailyList();
			ins.lastScanTime = 0;
			ins.map = new HashMap<String, GoodStock>();
		}
		
		return ins;
	}
	
	public long getlastScanTime(){
		return lastScanTime;
	}
	
	public void setlastScanTime(long time){
		lastScanTime = time;
	}
	
	public HashMap<String, GoodStock> getDailyList(){
		return map;
	}
	
	public void updateDailyList(DailyList obj){
		if(obj!=null){
			ins.map.clear();
			ins.map.putAll(obj.getDailyList());
			ins.lastScanTime = obj.getlastScanTime();
		}

	}
	
	public void updateStock(String id, String Rank, String Trend, String Quality){
		GoodStock obj;
		if(map.containsKey(id)){
			obj = map.get(id);
			obj.update(id, Rank, Trend, Quality);
		}else{
			obj = new GoodStock();
			obj.update(id, Rank, Trend, Quality);
			Log.e("map-", "insert id" + id);
			map.put(id, obj);
		}
	}
}

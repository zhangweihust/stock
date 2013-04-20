package com.zhangwei.gson;

import java.util.ArrayList;

/**
 *  每天都要扫描的列表，质地优秀的股票
 *  每月更新该列表本身
 * 
 *  @author zhangwei
 * 
 * */
public class DailyList {
	public transient static String ID = "_DailyList_";

	ArrayList<GoodStock> list;
	
	/**
	 *  {@literal 上次完成列表扫描的时间}
	 * */
	long lastScanTime;
	
	private DailyList(){
		
	}
}

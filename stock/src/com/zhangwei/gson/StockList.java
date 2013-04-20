package com.zhangwei.gson;

public class StockList {

	public transient static String ID = "_StockList_";
	
	//
	public String[] shangzheng_list; //"sh600000", 上证
	public String[] shenzheng_list;  //"sz000858", 深证
	public String[] chuangye_list;   //"sz300005", 创业板
	
	public int status; //1 when ok
	public long last_modify;  //列表上次更新时间
}

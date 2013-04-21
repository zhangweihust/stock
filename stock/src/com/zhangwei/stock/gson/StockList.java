package com.zhangwei.stock.gson;

import java.util.ArrayList;

public class StockList {
	//////////////////////////////////////////
	public ArrayList<String> shangzheng_list; //"sh600000", 上证
	public ArrayList<String>  shenzheng_list;  //"sz000858", 深证
	public ArrayList<String>  chuangye_list;   //"sz300005", 创业板
	
	public int status; //1 when ok
	public long last_modify;  //列表上次更新时间
	////////////////////////////////////////////

	public transient static String ID = "_StockList_";
	private transient static int index;
	public StockList(){
		shangzheng_list = new ArrayList<String>();
		shenzheng_list = new ArrayList<String>();
		chuangye_list = new ArrayList<String>();
		index = 0;
	}
	
	public String generateStockID(boolean reset){
		String ret = null;
		int sh_size = shangzheng_list.size();
		int sz_size = shenzheng_list.size();
		int cyb_size = chuangye_list.size();
		
		if(reset){
			index=0;
		}
		
		if(index<sh_size){
			ret =  shangzheng_list.get(index);
			index++;
		}else if(index<sh_size+sz_size){			
			ret =  shenzheng_list.get(index-sh_size);
			index++;
		}else if(index<sh_size+sz_size+cyb_size){
			ret =  chuangye_list.get(index-sh_size-sz_size);
			index++;
		}else{
			//no-op
		}
		
		return ret;
	}
	

}

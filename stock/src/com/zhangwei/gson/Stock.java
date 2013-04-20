package com.zhangwei.gson;

public class Stock {

	//basic info
	public String fullname; //浦发银行（sh600000）
	public String name; //浦发银行
	public String id;   //sh600000
	
	//从腾讯医生获取的信息：http://stockapp.finance.qq.com/doctor/sz002605.html
	public String rank; //排名： A 
	public String info; //概况： 有大资金参与 筹码趋于集中
	public String trend; //股票走势: 较强
	public String trend_detail; //1.处于长期上升趋势，近期保持多头强势
	                            //2.强势涨停，保持上攻势头
	                            //3.成交换手活跃，延续强势状态
	                            //4.近期走势相对强于大盘和行业
	
	public String quality; //股票质地: 优秀
	public String quality_detail; //1.成本控制较好
	                              //2.现金流充裕
	
}

package com.zhangwei.gson;

public class Stock {

	//basic info
	public String fullname; //浦发银行（sh600000）
	public String name; //浦发银行
	public String id;   //sh600000
	
	//从腾讯医生获取的信息：http://stockapp.finance.qq.com/doctor/sz002605.html
	public String rank; //排名： A 
	public String info; //概况： 有大资金参与 筹码趋于集中
	public String descrp; //小盘股，市盈率在较合理区间，中价股。总体财务评级较好。主营业务集中，管理成本控制在较稳定水平，董事姚硕榆近期任职，资产稳步增长。近期走势相对强于大盘和行业。 当日资金净流入-2334.47万元，5日资金净流入25000.85万元。
	public String trend; //股票走势: 较强
	public String trend_detail; //1.处于长期上升趋势，近期保持多头强势
	                            //2.强势涨停，保持上攻势头
	                            //3.成交换手活跃，延续强势状态
	                            //4.近期走势相对强于大盘和行业
	
	public String quality; //股票质地: 优秀
	public String quality_detail; //1.成本控制较好
	                              //2.现金流充裕
	/**
	 *  @param id sh600031
	 *  @param name 三一重工
	 *  @param rank ap,am,ab,bm,bb,cm,cb..
	 *  @param info  有大资金参与 筹码趋于集中
	 *  @param decrp descrption
	 *  @param trend 股票走势
	 *  @param trend_detail
	 *  @param quality 股票质地
	 *  @param quality_detail
	 * */
	public Stock(String id, String name, String rank, String info, 
			String descrp, String trend, String trend_detail, String quality, String quality_detail){
		this.fullname = name + "(" + id + ")";
		this.name = name;
		this.id = id;
		this.rank = rank;
		this.info = info;
		this.descrp = descrp;
		this.trend = trend;
		this.trend_detail = trend_detail;
		this.quality = quality;
		this.quality_detail = quality_detail;
	}
	
}

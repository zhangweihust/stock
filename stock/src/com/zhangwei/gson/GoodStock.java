package com.zhangwei.gson;

/**
 *  质地优秀的股票条目
 *  支持记录rank、趋势和质地的变化，时间，方向
 *  支持关注和取消关注
 *  
 *  @author zhangwei
 * 
 * */
public class GoodStock {
	/**
	 *  {@literal 股票id， sh600031}
	 * */
	String id;

	/**
	 *  {@literal 用户是否关注}
	 * */
	boolean isCare; 
	
	/**
	 *  {@literal 上次变更时的rank}
	 * */
	String lastRank; 
	
	/**
	 *  {@literal 上次变更时的Trend}
	 * */
	String lastTrend; 
	
	/**
	 *  {@literal 上次变更时的质地}
	 * */
	String lastQuality; 
	
	/**
	 *  {@literal 发生变更的时间in ms, 若为0则表示从未变更过}
	 * */
	long lastChangeTime; 
	
	/**
	 *  {@literal 现在的rank}
	 * */
	String Rank; 
	
	/**
	 *  {@literal 现在的Trend}
	 * */
	String Trend; 
	
	/**
	 *  {@literal 现在的质地}
	 * */
	String Quality; 
	
}

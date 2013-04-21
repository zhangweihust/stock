package com.zhangwei.gson;

/**
 *  质地优秀的股票条目<==>每天要扫描的股票
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
	public String id;

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

	/**
	 *  如果Rank, Trend, Quality有任一值改变则更新时间，同时把现有的值插入到last值上,
	 *  否则维持不变
	 *  @param r rank
	 *  @param t trend
	 *  @param q quality
	 *  @author zhangwei
	 * */
	public void update(String id, String r, String t, String q) {
		// TODO Auto-generated method stub
		this.id = id;

		boolean hasChange = false;
		if(r!=null && r.equals(this.Rank)){
			this.lastRank = this.Rank;
			this.Rank = r;
			hasChange = true;
		}

		if(t!=null && t.equals(this.Trend)){
			this.lastTrend = this.Trend;
			this.Trend = t;
			hasChange = true;
		}
		
		if(q!=null && q.equals(this.Quality)){
			this.lastQuality = this.Quality;
			this.Quality = q;
			hasChange = true;
		}
		
		if(hasChange){
			lastChangeTime = System.currentTimeMillis();
		}

	}


	
}

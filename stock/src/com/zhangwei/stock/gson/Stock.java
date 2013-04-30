package com.zhangwei.stock.gson;

public class Stock {

	//basic info
	public String fullname; //浦发银行（sh600000）
	public String name; //浦发银行
	public String id;   //sh600000
	
	public String scan_date; //20130501
	
	//从腾讯医生获取的信息：http://stockapp.finance.qq.com/doctor/sz002605.html
	public String rank; //排名： A 
	public String info; //概况： 有大资金参与 筹码趋于集中
	public String descrp; //小盘股，市盈率在较合理区间，中价股。总体财务评级较好。主营业务集中，管理成本控制在较稳定水平，董事姚硕榆近期任职，资产稳步增长。近期走势相对强于大盘和行业。 当日资金净流入-2334.47万元，5日资金净流入25000.85万元。
	
	/**
	 *  {@literal 股票走势:强势 突破  较强  转强 震荡 平稳  一般  趋弱 较弱 低迷 超跌 危险}
	 * */
	public String trend; //股票走势: 较强
	
	/**
	 * {@literal 股票走势:}
	 *  <li>1.K线形态较弱，空方相对占优
	 *  <li>2.K线走势较强，多方相对占优
	 *  <li>3.[出水芙蓉]K线形态，突破整理形态...
	 *  <li>4.[升势尽头]K线形态，长期升势之后...
	 *  <li>5.[断头铡刀]K线形态，破位下跌,需...
	 *  <li>6.[旭日东升]K线形态，比较强烈的反...
	 *  <li>7.[看涨吞没]K线形态，在底部出现意...
	 *  <li>8.上升突破K线形态，多头占据相对优势
	 *  <li>9.下跌破位K线形态，空头占据相对优势
	 *  <li>10.主营利润率较高
	 *  <li>11.低位缩量盘整，关注走势变化
	 *  <li>12.净资产收益率较高
	 *  <li>13.出现缩量涨停，延续强势状态
	 *  <li>14.出现连续阳线，近期多头占据优势
	 *  <li>15.出现连续阴线，近期空头占据优势
	 *  <li>16.基金超配该类股票
	 *  <li>17.处于长期上升趋势
	 *  <li>18.处于长期上升趋势，近期保持多头...
	 *  <li>19.处于长期上升趋势，近期保持多头强势
	 *  <li>20.处于长期上升趋势，近期走势震荡回调
	 *  <li>21.处于长期上升趋势，，已处于长期...
	 *  <li>22.处于长期下降趋势
	 *  <li>23处于长期下降趋势，近期持续向下...
	 *  <li>24.处于长期下降趋势，近期持续向下回落
	 *  <li>25.处于长期下降趋势，近期走势震荡...
	 *  <li>26.处于长期下降趋势，近期走势震荡反弹
	 *  <li>27.多家基金持有该股
	 *  <li>28.多家基金进入
	 *  <li>29.存货量较合理
	 *  <li>30强势涨停，保持上攻势头
	 *  <li>31.成交开始放量，关注走势变化
	 *  <li>32.成交换手下降，延续弱势状态
	 *  <li>33.成交换手活跃，延续强势状态
	 *  <li>34.成交放大过度，可能出现变盘
	 *  <li>35.成本控制较好
	 *  <li>36.持仓量有显著增加
	 *  <li>37.换手率下降，关注走势持续度
	 *  <li>38.放量杀跌，风险凸现需要规避
	 *  <li>39.放量滞涨，走势可能出现变盘
	 *  <li>40.整体走势与大盘波动相近
	 *  <li>41.整体走势相对弱于大盘
	 *  <li>42.整体走势相对强于大盘
	 *  <li>43.有新机构进入该股
	 *  <li>44.极多家基金持有该股票
	 *  <li>45.毛利率较高
	 *  <li>46.波段低位出现止跌K线形态
	 *  <li>47.波段高位出现滞涨K线形态
	 *  <li>48.现金流充裕
	 *  <li>49.现金流较为充裕
	 *  <li>50.缩量回调整理，关注走势变化
	 *  <li>51.行业处于历史高速发展期,未来前景...
	 *  <li>52.资产周转快
	 *  <li>53.资产周转较快
	 *  <li>54.资产负债比例合理
	 *  <li>55.资金较充裕
	 *  <li>56.走势震荡加剧，多空双方分歧较大
	 *  <li>57.近期走势弱于大盘和行业，注意风险
	 *  <li>58.近期走势相对强于大盘和行业
	 *  <li>59.震荡整理K线形态，多空双方存在分歧
	 * */
	public String trend_detail; // 1.处于长期上升趋势，近期保持多头强势
								// 2.强势涨停，保持上攻势头
								// 3.成交换手活跃，延续强势状态
								// 4.近期走势相对强于大盘和行业
	
	/**
	 *  {@literal 股票质地:优秀 良好  一般 较差}
	 * */
	public String quality; //股票质地: 优秀
	public String quality_detail; //1.成本控制较好
	                              //2.现金流充裕
	
	/**
	 *  http://stockhtm.finance.qq.com/sstock/quotpage/q/300315.htm#ycgz
	 *  英策点评 - 接近当前股价估值区间 47.83元 ~ 52.60元
	 * */
	public String stockEstimateLowValue; //47.83
	/**
	 *  http://stockhtm.finance.qq.com/sstock/quotpage/q/300315.htm#ycgz
	 *  英策点评 - 接近当前股价估值区间 47.83元 ~ 52.60元
	 * */
	public String stockEstimateHighValue; //52.60
	
	/**
	 *  http://stockhtm.finance.qq.com/sstock/quotpage/q/300315.htm#ycgz
	 *  英策点评 - 公司品质: 优秀
	 * */
	public String stockQuality;
	/**
	 * 掌趣科技(300315)2011年营业收入为1.84亿元，同比上年增长56.55%，净利润为0.56亿元，同比上年增长34.72%，净资产为2.24亿元，同比上年增长33.11%。根据机构最新预测，公司2012年净利润为0.90亿元，同比上年增长61.64%，净资产为15.66亿元，同比上年增长599.46%，每股收益为0.55元，同比上年增长21.23%，每股净资产为9.57元，同比上年增长424.59%。综合以上数据分析，根据英策咨询的评价标准，掌趣科技的公司品质为优秀。
	 * */
	public String stockQuality_detail;
	
	/**
	 *  http://stockhtm.finance.qq.com/sstock/quotpage/q/300315.htm#ycgz
	 *  英策点评 - 增长速度: 高速增长
	 * */
	public String stockIncrement;
	/**
	 * 公司上年营业收入、净利润和今年预测净利润的增长率分别为56.55%,34.72%和61.64%,属于高速增长企业。
	 * */
	public String stockIncrement_detail;
	
	/**
	 *  http://stockhtm.finance.qq.com/sstock/quotpage/q/300315.htm#ycgz
	 *  英策点评 - 资本安全: 很安全
	 * */
	public String stockSafe;
	/**
	 * 数据显示，公司上年资产负债率为6%,资本安全度极高。
	 * */
	public String stockSafe_detail;
	
	/**
	 *  http://stockhtm.finance.qq.com/sstock/quotpage/q/300315.htm#ycgz
	 *  英策点评 - 市场价值: 低估不明显
	 * */
	public String stockMarketValue;
	/**
	 *  在按照5种估值方法计算出的每股价值中，3项高于当前价/收盘价，英策估值网认为公司股票低估不明显。
	 * */
	public String stockMarketValue_detail;
	
	/**
	 *  http://stockhtm.finance.qq.com/sstock/quotpage/q/300315.htm#ycgz
	 *  英策点评 - 资产价值: 一般高估
	 * */
	public String stockAssetValue;
	/**
	 *  公司股价比按平均市净率法计算出的每股价值高11.85%，表明公司资产价值被一般高估。
	 * */
	public String stockAssetValue_detail;
	
	/**
	 *  http://stockhtm.finance.qq.com/sstock/quotpage/q/300315.htm#ycgz
	 *  英策点评 - 收益价值: 严重高估
	 * */
	public String stockReturnValue;
	/**
	 *  公司股价比按平均市盈率法计算出的每股价值高93.88%，表明公司收益价值被严重高估。
	 * */
	public String stockReturnValue_detail;
	
	public String YC_Time; //更新时间：2012-12-14（交易日19:00-19:30更新）
	
	/**
	 *  http://stockhtm.finance.qq.com/sstock/quotpage/q/300315.htm#ycgz
	 *  英策点评 - 行业及市值规模: 公司属电子信息；本公司流通市值为 9亿元,属于小盘股范畴。
	 * */
	public String Category; //电子信息
	/**
	 *  本公司流通市值为 9亿元
	 * */
	public String MarketValue; // 9亿元
	public String Size;        // 小盘股范畴
	
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
	
	public Stock(String id){
		this.id = id;
	}
	
}

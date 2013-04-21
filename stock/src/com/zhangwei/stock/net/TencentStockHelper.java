package com.zhangwei.stock.net;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.zhangwei.stock.gson.Stock;

public class TencentStockHelper {
	private static TencentStockHelper ins;
	private final String TAG = "TencentStockHelper";
	//String doctor_url = "http://stockapp.finance.qq.com/doctor/sz002605.html";
	String doctor_url_prefix = "http://stockapp.finance.qq.com/doctor/";
	
	private TencentStockHelper(){
		
	}
	
	public static TencentStockHelper getInstance(){
		if(ins==null){
			ins = new TencentStockHelper();
		}
		
		return ins;
	}
	
	
	public Stock get_stock_from_doctor(String stockID){

		try {
			String result_stockname;
			String result_rank;
			String result_info;
			String result_descrp;
			String result_trend;
			String result_trend_detail;
			String result_quality;
			String result_quality_detail;
			
			Document doc = null;
			String connect_url = doctor_url_prefix + stockID + ".html";
			doc = Jsoup.connect(connect_url).timeout(30000).get();
			
			//rank
			Element lefttd = doc.body().getElementById("lefttd");
			Element rank = lefttd.select("div").first();
			result_rank = rank.attr("class");
			//Log.e("ok", "rank:" + rank.attr("class"));
			
			Element qt_ctn1 = doc.body().getElementById("qt-ctn1");
			Element stockname = qt_ctn1.select("span.dh_name").first();
			result_stockname = stockname.text();
			//Log.e("ok", "stockname : " + stockname.text()); //

			Element righttd = doc.body().getElementById("righttd");
			Element info = righttd.select("div.text-head").first();
			Element descrp = righttd.getElementById("doctor_h2desp");
			result_info = info.text();
			result_descrp = descrp.text();
/*			Log.e("ok", "info:" + info.text());
			Log.e("ok", "descrp : " + descrp.text());*/
			
			Element boxb = doc.body().getElementById("boxb");
			Element icon01 = boxb.select("div.icon01").first();
			Element trend = icon01.select("span").first();
			result_trend = trend.text();
			//Log.e("ok", "trend : " + trend.text()); // 较强 , trend
			
			Element trend_detail = boxb.select("div.data1").first();
			result_trend_detail = trend_detail.text();
			//Log.e("ok", "trend_detail:" + trend_detail.text());
			
			Element icon02 = boxb.select("div.icon02").first();
			Element quality = icon02.select("span").first();
			result_quality = quality.text();
			//Log.e("ok", "quality : " + quality.text()); // 较强 , trend
			
			Element quality_detail = boxb.select("div.data2").first();
			result_quality_detail = quality_detail.text();
			//Log.e("ok", "trend_detail:" + quality_detail.text());
			
			Stock stock = new Stock(stockID, result_stockname, result_rank, 
					result_info, result_descrp, result_trend,  result_trend_detail,
					result_quality, result_quality_detail);

			return stock;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
}

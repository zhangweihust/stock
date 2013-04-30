package com.zhangwei.stock.net;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import android.util.Log;

import com.zhangwei.stock.common.utils.Constants;
import com.zhangwei.stock.common.utils.InputCheck;
import com.zhangwei.stock.gson.Stock;

public class TencentStockHelper {
	private static TencentStockHelper ins;
	private final String TAG = "TencentStockHelper";
	//String doctor_url = "http://stockapp.finance.qq.com/doctor/sz002605.html";
	String doctor_url_prefix = "http://stockapp.finance.qq.com/doctor/";
	String ycgz_url_prefix = "http://img.gtimg.cn/copage/ycgz/htm/";//600315.htm";
	
	
	
	
	private TencentStockHelper(){
		
	}
	
	public static TencentStockHelper getInstance(){
		if(ins==null){
			ins = new TencentStockHelper();
		}
		
		return ins;
	}
	
	/**
	 * 
	 *  从腾讯doctor获取信息，再从腾讯英策估值中完善信息
	 * */
	public Stock get_stock_from_tencent(String stockID){

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
			
/*			if(fetch_ycgz(stock)){
				Date now = new Date();
				DateFormat df = DateFormat.getDateInstance();
				stock.scan_date = df.format(now);
				Log.e("test", "stock.scan_date:" + stock.scan_date);
			}*/
			

			return stock;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	public boolean fetch_ycgz(Stock  stock){
		//http://stockhtm.finance.qq.com/sstock/quotpage/q/600315.htm#ycgz
		//http://img.gtimg.cn/copage/ycgz/htm/600315.htm
		
		Document doc = null;
		String id = Pattern.compile("[a-zA-Z]").matcher(stock.id).replaceAll("");
		String connect_url = ycgz_url_prefix + id + ".htm";
		Log.e("test", "fetch_ycgz connect_url:" + connect_url);
		try {
			doc = Jsoup.connect(connect_url).timeout(30000).get();

			Element values = doc.body().select("div.zdig").first().select("div").first().child(0);//("div.title");
			
			Double max = 0.0;
			Double min = 999999999.0;
			for(TextNode tn : values.textNodes()){
				if(InputCheck.checkNum(tn.text().trim())){
					//Log.e("stock qujian", tn.text());
					Double temp = -1.0;
					try{
						temp = Double.valueOf(tn.text().trim());
					}catch(NumberFormatException ex){
						
					}
					
					if(temp>0 && max<temp){
						max = temp;
					}
					if(temp>0 && min>temp){
						min = temp;
					}
				}
				
			}
			DecimalFormat df = new DecimalFormat(".##");
			if(max>0.0){
				stock.stockEstimateHighValue = df.format(max);
				Log.e("stockEstimateHighValue", stock.stockEstimateHighValue );
			}
			
			if(min<999999999.0){
				stock.stockEstimateLowValue = df.format(min);;
				Log.e("stockEstimateLowValue", stock.stockEstimateLowValue );
			}
			
			
			for(Element elem:doc.body().select("td.gray_bottom_solid")){
				int type_state=0;
				for(Element sec_elem: elem.select("tbody").first().select("td")){
					Element strong_elem = sec_elem.select("strong").first();
					if(strong_elem!=null){
						String strong_str = strong_elem.text().replaceAll("[:： ]", "");
						if(Constants.stockQuality.equals(strong_str)){
							type_state = 1;
							stock.stockQuality = sec_elem.textNodes().get(0).text().replaceAll("[:： ]", "");
							Log.e("quality", strong_str + " what:" + stock.stockQuality);
						}else if(Constants.stockIncrement.equals(strong_str)){
							type_state = 2;
							stock.stockIncrement = sec_elem.textNodes().get(0).text().replaceAll("[:： ]", "");
							Log.e("stockIncrement", strong_str + " what:" + stock.stockIncrement);
						}else if(Constants.stockSafe.equals(strong_str)){
							type_state = 3;
							stock.stockSafe = sec_elem.textNodes().get(0).text().replaceAll("[:： ]", "");
							Log.e("stockSafe", strong_str + " what:" + stock.stockSafe);
						}else if(Constants.stockMarketValue.equals(strong_str)){
							type_state = 4;
							stock.stockMarketValue = sec_elem.textNodes().get(0).text().replaceAll("[:： ]", "");
							Log.e("stockMarketValue", strong_str + " what:" + stock.stockMarketValue);
						}else if(Constants.stockAssetValue.equals(strong_str)){
							type_state = 5;
							stock.stockAssetValue = sec_elem.textNodes().get(0).text().replaceAll("[:： ]", "");
							Log.e("stockAssetValue", strong_str + " what:" + stock.stockAssetValue);
						}else if(Constants.stockReturnValue.equals(strong_str)){
							type_state = 6;
							stock.stockReturnValue = sec_elem.textNodes().get(0).text().replaceAll("[:： ]", "");
							Log.e("stockReturnValue", strong_str + " what:" + stock.stockReturnValue);
						}
					}else{
						Log.e("detail", type_state + " " + sec_elem.text());
						if(type_state==1){
							stock.stockQuality_detail = sec_elem.text();
						}else if(type_state==2){
							stock.stockIncrement_detail = sec_elem.text();
						}else if(type_state==3){
							stock.stockSafe_detail = sec_elem.text();
						}else if(type_state==4){
							stock.stockMarketValue_detail = sec_elem.text();
						}else if(type_state==5){
							stock.stockAssetValue_detail = sec_elem.text();
						}else if(type_state==6){
							stock.stockReturnValue_detail = sec_elem.text();
						}
						type_state = 0;
					} 
					
				}
			}
			
			for( Element elem2 : doc.body().select("td")){
				Element strong_elem = elem2.select("strong").first();
				if(strong_elem!=null && elem2.select("td").size()==1){
					String strong_str = strong_elem.text().replaceAll("[:： ]", "");
					if(Constants.stockValueSize.equals(strong_str)){
						//Log.e("stockValueSize",elem2.parent().html());
						for(Element elem3:elem2.parent().select("span")){
							Log.e("elem3",elem3.html());
							if(elem3.text().contains("股")){
								stock.Size = elem3.text();
							}else if(elem3.text().contains("元")){
								stock.MarketValue = elem3.text();
							}else if(elem3.text().contains("行业")){
								stock.Category = elem3.text();
							}else {
								stock.Category = elem3.text();
							}
						}
						
					}
				}
				

			}
			
			for(Element elem : doc.body().select("span.logo")){
				
				if(elem.text().contains("更新")){
					Log.e("logo", elem.text());
					stock.YC_Time = elem.text();
				}
			}


			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		return true;
		
	}
	
	public boolean fetch_ycgz(String  stock){
		//http://stockhtm.finance.qq.com/sstock/quotpage/q/600315.htm#ycgz
		//http://img.gtimg.cn/copage/ycgz/htm/600315.htm
		
		Document doc = null;
		String id = Pattern.compile("[a-zA-Z]").matcher(stock).replaceAll("");
		String connect_url = ycgz_url_prefix + id + ".htm";
		Log.e("test", "fetch_ycgz connect_url:" + connect_url);
		try {
			doc = Jsoup.connect(connect_url).timeout(30000).get();

			Element values = doc.body().select("div.zdig").first().select("div").first().child(0);//("div.title");
			for(TextNode tn : values.textNodes()){
				if(InputCheck.checkNum(tn.text().trim())){
					//Log.e("stock qujian", tn.text());
				}
				
			}
			
			for(Element elem:doc.body().select("td.gray_bottom_solid")){
				int type_state=0;
				for(Element sec_elem: elem.select("tbody").first().select("td")){
					Element strong_elem = sec_elem.select("strong").first();
					if(strong_elem!=null){
						String strong_str = strong_elem.text().replaceAll("[:： ]", "");
						if(Constants.stockQuality.equals(strong_str)){
							type_state = 1;
							Log.e("quality", strong_str);
						}else if(Constants.stockIncrement.equals(strong_str)){
							type_state = 2;
							Log.e("stockIncrement", strong_str);
						}else if(Constants.stockSafe.equals(strong_str)){
							type_state = 3;
							Log.e("stockSafe", strong_str);
						}else if(Constants.stockMarketValue.equals(strong_str)){
							type_state = 4;
							Log.e("stockMarketValue", strong_str);
						}else if(Constants.stockAssetValue.equals(strong_str)){
							type_state = 5;
							Log.e("stockAssetValue", strong_str);
						}else if(Constants.stockReturnValue.equals(strong_str)){
							type_state = 6;
							Log.e("stockReturnValue", strong_str);
						}
					}else{
						Log.e("detail", type_state + " " + sec_elem.text());
						type_state = 0;
					} 
					
				}
			}
			
			for( Element elem2 : doc.body().select("td")){
				Element strong_elem = elem2.select("strong").first();
				if(strong_elem!=null && elem2.select("td").size()==1){
					String strong_str = strong_elem.text().replaceAll("[:： ]", "");
					if(Constants.stockValueSize.equals(strong_str)){
						//Log.e("stockValueSize",elem2.parent().html());
						for(Element elem3:elem2.parent().select("span")){
							Log.e("elem3",elem3.html());
						}
						
					}
				}
				

			}


			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		return true;
		
	}
}

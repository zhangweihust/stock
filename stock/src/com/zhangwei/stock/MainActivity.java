package com.zhangwei.stock;

import java.io.IOException;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import cn.zipper.framwork.core.ZActivity;

import com.zhangwei.stock.gson.DailyList;
import com.zhangwei.stock.gson.Stock;
import com.zhangwei.stock.gson.StockList;
import com.zhangwei.stock.net.JsoupHelper;
import com.zhangwei.stock.net.TencentStockHelper;
import com.zhangwei.stock.service.DailyStockScanService;
import com.zhangwei.stock.storage.SDCardStorageManager;
import com.zhangwei.stocklist.StockListHelper;



import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends ZActivity {
	private final String TAG = "MainActivity";
	private final int HANDLER_FLAG_STOCK_PROCESS_DONE = 1;
	
	String histroy_trade_url = "http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/600031.phtml";

	String doctor_url = "http://stockapp.finance.qq.com/doctor/sz002605.html";
	String doctor_url_prefix = "http://stockapp.finance.qq.com/doctor/";
	
	String stock_list_sh = "http://ustock.finance.ifeng.com/stock_list.php?type=sh";
	String stock_list_sz = "http://ustock.finance.ifeng.com/stock_list.php?type=sz";
	String stock_list_cyb = "http://ustock.finance.ifeng.com/stock_list.php?type=gem";
	StockList sl;
	StockTask stock_t;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent intent=new Intent(MainActivity.this, DailyStockScanService.class);  
		startService(intent);
		
		//new JsoupHelper().test();
		//Stock stock = new Stock("sh600315");
		//TencentStockHelper.getInstance().fetch_ycgz(stock);
		
		//TencentStockHelper.getInstance().get_stock_from_tencent("sh600315");
		
		Log.e("jsoup", "Ok done");
	}
	
	private void show_history_trade(){
		Document doc = null;
		try {
			doc = Jsoup.connect(histroy_trade_url).timeout(0).get();
			Element table = doc.body().getElementById("FundHoldSharesTable");
			//Log.e("debug", doc.body().text());

			for (Element row : table.select("tr")) {
				Elements tds = row.select("td");
				if (tds.size() > 1) {
					System.out.println(tds.get(0).text() + ":" + tds.get(1).text());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void show_stock_doctor(String stockID){
		Document doc = null;
		try {
			String connect_url = doctor_url_prefix + stockID + ".html";
			doc = Jsoup.connect(connect_url).timeout(0).get();
			Element lefttd = doc.body().getElementById("lefttd");
			Element rank = lefttd.select("div").first();
			Log.e("ok", "rank:" + rank.attr("class"));
			
			Element qt_ctn1 = doc.body().getElementById("qt-ctn1");
			Element stockname = qt_ctn1.select("span.dh_name").first();
			Log.e("ok", "stockname : " + stockname.text()); //

			Element righttd = doc.body().getElementById("righttd");
			Element info = righttd.select("div.text-head").first();
			Element descrp = righttd.getElementById("doctor_h2desp");
/*			Log.e("ok", "info:" + info.text());
			Log.e("ok", "descrp : " + descrp.text());*/
			
			Element boxb = doc.body().getElementById("boxb");
			Element icon01 = boxb.select("div.icon01").first();
			Element trend = icon01.select("span").first();
			//Log.e("ok", "icon01 : " + icon01.text()); 
			Log.e("ok", "trend : " + trend.text()); // 较强 , trend
			
			Element trend_detail = boxb.select("div.data1").first();
			Log.e("ok", "trend_detail:" + trend_detail.text());
			
			Element icon02 = boxb.select("div.icon02").first();
			Element quality = icon02.select("span").first();
			//Log.e("ok", "icon02 : " + icon02.text()); 
			Log.e("ok", "quality : " + quality.text()); // 较强 , trend
			
			Element quality_detail = boxb.select("div.data2").first();
			Log.e("ok", "trend_detail:" + quality_detail.text());
			
			Stock stock = new Stock(stockID, stockname.text(), rank.attr("class"), 
					                info.text(), descrp.text(), trend.text(),  trend_detail.text(),
					                quality.text(), quality_detail.text());
			SDCardStorageManager.getInstance().putItem(null, stockID, stock, Stock.class);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void save_stock_list(String stock_list, int type){
		Document doc = null;
		try {
			doc = Jsoup.connect(stock_list).timeout(0).get();
			Element stocklist = doc.body().getElementById("stocklist");
			for (Element row : stocklist.select("li")) {
				Elements links = row.select("a");
				String url = links.first().attr("href");
				Pattern pattern = Pattern.compile("[,=]+");
				String[] url_parts = pattern.split(url);
				Log.e("ok", "url:" + url_parts[url_parts.length-1] + " name:" + row.text());
				
				if(type==0){
				   sl.shangzheng_list.add(url_parts[url_parts.length-1]);
				}else if(type==1){
					sl.shenzheng_list.add(url_parts[url_parts.length-1]);
				}else if(type==2){
					sl.chuangye_list.add(url_parts[url_parts.length-1]);
				}
			}
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case HANDLER_FLAG_STOCK_PROCESS_DONE:
			stock_t = new StockTask();
			sl.next();
			String next = sl.getCurStockID();
			Log.e("handleMesage", "process:" + next);
			//stock_t.execute(next);
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	
	private class StockTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String stockid = params[0];
			show_stock_doctor(stockid);
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
    		if(handler!=null){
    			//handler.removeMessages(Constant.HANDLER_FLAG_LISTVIEW_UPDATE);
				Message msg = handler.obtainMessage(HANDLER_FLAG_STOCK_PROCESS_DONE);
				msg.sendToTarget();
    		}
		}
		
	}

}

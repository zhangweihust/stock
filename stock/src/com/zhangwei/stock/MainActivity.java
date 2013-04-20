package com.zhangwei.stock;

import java.io.IOException;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;



import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	String histroy_trade_url = "http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/600031.phtml";

	String doctor_url = "http://stockapp.finance.qq.com/doctor/sz002605.html";
	
	String stock_list = "http://ustock.finance.ifeng.com/stock_list.php";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//解析sina历史交易记录
		//show_history_trade();

		//解析腾讯股票医生
		//show_stock_doctor();

		//获取股票列表
		show_stock_list();

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
	
	private void show_stock_doctor(){
		Document doc = null;
		try {
			doc = Jsoup.connect(doctor_url).timeout(0).get();
			Element lefttd = doc.body().getElementById("lefttd");
			Element rank = lefttd.select("div").first();
			Log.e("ok", "lefttd - div - class:" + rank.attr("class"));

			Element righttd = doc.body().getElementById("righttd");
			Element head = righttd.select("div.text-head").first();
			Element descrp = righttd.getElementById("doctor_h2desp");
			Log.e("ok", "lefttd - div - class:" + head.text());
			Log.e("ok", "descrp : " + descrp.text());
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void show_stock_list(){
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
			}
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

package com.example.qianlong_crack;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
/*		String d = "ddddddddd";
		byte[] b = d.getBytes();
		int len = d.length();
		Dump.Print(b, len);
		Dump.Print(b, 0, len);
		Dump.Print(d);*/
		
		Document doc = null;
		try {
			doc = Jsoup.connect("http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/600031.phtml").timeout(0).get();
			//doc = Jsoup.connect("http://espn.go.com/mens-college-basketball/conferences/standings/_/id/2/year/2012/acc-conference").get();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Element table = doc.body().getElementById("FundHoldSharesTable");
/*		for (Element table : doc.select("table.FundHoldSharesTable")) {
	        for (Element row : table.select("tr")) {
	            Elements tds = row.select("td");
	            if (tds.size() > 1) {
	                System.out.println(tds.get(0).text() + ":" + tds.get(1).text());
	            }
	        }
	    }*/
		
		Log.e("debug", doc.body().text());
		
		   for (Element row : table.select("tr")) {
	            Elements tds = row.select("td");
	            if (tds.size() > 1) {
	                System.out.println(tds.get(0).text() + ":" + tds.get(1).text());
	            }
	        }
		
		

/*	    for (Element table : doc.select("table.tablehead")) {
	        for (Element row : table.select("tr")) {
	            Elements tds = row.select("td");
	            if (tds.size() > 6) {
	                System.out.println(tds.get(0).text() + ":" + tds.get(1).text());
	            }
	        }
	    }*/
		
		Log.e("jsoup", "Ok done");
	}

}

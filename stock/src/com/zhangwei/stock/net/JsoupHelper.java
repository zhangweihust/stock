package com.zhangwei.stock.net;


import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import android.util.Log;

public class JsoupHelper {
	private final String TAG = "JsoupHelper";
	
	int[] path;
	
	public JsoupHelper(){
		path = new int[100];
	}
	
	/**
	 *  @param input 在给定的Element中搜索cssQuerys
	 *  @param cssQuerys cssQuerys的格式eg： "div.zdig_div_span" 中间用_分割
	 *  @param indexs 每个cssQuery查询的元素位置 "0_0_1"  中间用_分割
	 * */
	public   Element search(Element input, String cssQuerys, String indexs){
		String[] cssQuery_str_array = cssQuerys.split("_");
		String[] index_str_array = indexs.split("_");
		Element e = search(input, cssQuery_str_array, index_str_array, 0);
		
		return e;
		
	}
	
	/**
	 *  @param input 在给定的Element中搜索cssQuerys,默认每一级取第一个
	 *  @param cssQuerys cssQuerys的格式eg： "div.zdig_div_span" 中间用_分割
	 * */
	public   Element search(Element input, String cssQuerys){
		String[] cssQuery_str_array = cssQuerys.split("_");
		String[] indexs_str_array = new String[cssQuery_str_array.length];
		for(int i=0; i<indexs_str_array.length; i++){
			indexs_str_array[i] = "0";
		}
		Element e = search(input, cssQuery_str_array, indexs_str_array, 0);
		
		return e;
		
	}

	/**
	 *  @param input 在给定的Element中搜索cssQuerys
	 *  @param cssQuerys cssQuerys数组
	 *  @param index 对应cssQuerys每个cssQuery查询的元素位置 
	 * */
	public   Element search(Element input,  String[] cssQuery_str_array, String[] indexs_str_array){

		Element e = search(input, cssQuery_str_array, indexs_str_array, 0 );
		
		return e;
		
	}
	
	

	
	private   Element search(Element input,  String[] cssQuerys, String[] indexs, int level){
		if(level>=cssQuerys.length){
			//found
			return input;
		}
		
		int index = 0;
		if(indexs.length>level){
			index = Integer.valueOf(indexs[level]);
		}

		
		if(input!=null){
			Elements es = input.select(cssQuerys[level]);
			
			if(es!=null && es.size()>index){
				Element result = null;
				Element e = es.get(index);
				if(e!=null){
					result = search(e, cssQuerys, indexs, level+1);
				}
				
				return result;
			}else{
				return null;
			}

		}else{
			return null;
		}
	}
	
	public String[] getTextFromElement(Element input){
		String[] result = null;
		if(input!=null && input.textNodes().size()>0){
			result = new String[input.textNodes().size()];
			for(int i=0; i<input.textNodes().size(); i++ ){
				result[i] = input.textNodes().get(i).text();
			}
		}
		
		return result;
		

	}
	
	public void dump(Element input, String cssQuerys){
		String[] cssQuery_str_array = cssQuerys.split("_");
		dump(input, cssQuery_str_array, 0);
	}
	
	private   void dump(Element input, String[] cssQuerys, int level){
		if(level>=cssQuerys.length){
			//found
			Log.e(TAG, "Path:" + getPath(level));
			Log.i(TAG,  "Element:" + input.html());
			return;
		}
		
		if(input!=null){
			Elements es = input.select(cssQuerys[level]);
			
			for(int i=0; i< es.size(); i++){
				path[level] = i;
				dump(es.get(i), cssQuerys, level+1);
			}
			

		}
	}
	
	private String getPath(int level){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<level; i++){
			sb.append(path[i]).append("-");
		}
		return sb.toString();
	}
	
	
	public   void test(){
		String connect_url = "http://img.gtimg.cn/copage/ycgz/htm/600315.htm";
		try {
			String cssQuery = "div.zdig_div";
			Document doc = Jsoup.connect(connect_url).timeout(30000).get();
			
			Element e =  search(doc.body(), cssQuery, "0_0");
			if(e!=null){
				Log.e("JsoupHelper", e.html());
			}else{
				Log.e("JsoupHelper", "null");
			}
			
			String[] result = getTextFromElement(e);
			for(String r:result){
				Log.w(TAG, "r:" + r);
			}
			
			dump(doc.body(), cssQuery);

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

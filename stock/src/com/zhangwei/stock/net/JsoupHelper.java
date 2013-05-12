package com.zhangwei.stock.net;


import java.io.IOException;
import java.util.ArrayList;

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
	 *  @param cssQuerys cssQuerys的格式eg： "div.zdig/div/span" 中间用|分割
	 *  @param indexs 每个cssQuery查询的元素位置 "0/0/1"  中间用_分割
	 * */
	public   Element search(Element input, String cssQuerys, String indexs){
		String[] cssQuery_str_array = cssQuerys.split("/");
		String[] index_str_array = indexs.split("/");
		Element e = search(input, cssQuery_str_array, index_str_array, 0);
		
		return e;
		
	}
	
	/**
	 *  @param input 在给定的Element中搜索cssQuerys,默认每一级取第一个
	 *  @param cssQuerys cssQuerys的格式eg： "div.zdig_div_span" 中间用_分割
	 * */
	public   Element search(Element input, String cssQuerys){
		String[] cssQuery_str_array = cssQuerys.split("/");
		String[] indexs_str_array = new String[cssQuery_str_array.length];
		for(int i=0; i<indexs_str_array.length; i++){
			indexs_str_array[i] = "0";
		}
		Element e = search(input, cssQuery_str_array, indexs_str_array, 0);
		
		return e;
		
	}

	/**
	 *  @param input 在给定的Element中搜索cssQuerys
	 *  @param cssQuery_str_array cssQuerys数组
	 *  @param indexs_str_array 对应cssQuerys每个cssQuery查询的元素位置 
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
	
	/**
	 *  @param input 在给定的Element中搜索cssQuerys
	 *  @param cssQuerys cssQuerys数组
	 *  @param indexs 对应cssQuerys每个cssQuery查询的元素位置 
	 *  @return pipeiElements
	 * */
	public Elements match(Element input,  String cssQuerys, String indexs){
		String[] cssQuery_str_array = cssQuerys.split("/");
		String[] index_str_array = indexs.split("/");
		
		
		return match(input, cssQuery_str_array, index_str_array, 0);
	}
	
	private Elements match(Element input, String[] cssQuerys, String[] indexs, int level){
		
		int index = 0;
		if(indexs.length>level){
			index = Integer.valueOf(indexs[level]);
		}		

		if(input!=null){	
			if(level+1<cssQuerys.length){
				Elements es = input.select(cssQuerys[level]);
				
				if(es!=null && es.size()>index){					
					return match(es.get(index), cssQuerys, indexs, level+1);
				}
			}else{
				Elements es2 = input.select(cssQuerys[level]);
				return es2;
			}
		}
		
		return null;
		
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
	
	public  ArrayList<Element> dump(Element input, String cssQuerys){
		String[] cssQuery_str_array = cssQuerys.split("/");
		return  dump(input, cssQuery_str_array, 0);
	}
	
	private  ArrayList<Element> dump(Element input, String[] cssQuerys, int level){
		ArrayList<Element> r =  new ArrayList<Element>();
		if(level>=cssQuerys.length){
			//found
			Log.e(TAG, "Path:" + getPath(level));
			Log.i(TAG,  "Element:" + input.html());
			r.add(input);
			return r;
		}
		
		if(input!=null){
			Elements es = input.select(cssQuerys[level]);
			
			for(int i=0; i< es.size(); i++){
				path[level] = i;
				 ArrayList<Element> tmp = dump(es.get(i), cssQuerys, level+1);
				if(tmp!=null){
					r.addAll(tmp);
				}
			}
			

		}
		
		return r;
	}
	
	private String getPath(int level){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<level; i++){
			sb.append(path[i]).append("/");
		}
		return sb.toString();
	}
	
	
	public   void test(){
		String connect_url = "http://img.gtimg.cn/copage/ycgz/htm/600315.htm";
		try {
			String cssQuery = "div/div/span";
			Document doc = Jsoup.connect(connect_url).timeout(30000).get();
			
			Element e =  search(doc.body(), cssQuery, "3/1/2");
			if(e!=null){
				Log.e("JsoupHelper", e.html());
			}else{
				Log.e("JsoupHelper", "null");
			}
			
			String[] result = getTextFromElement(e);
			if(result!=null){
				for(String r:result){
					Log.w(TAG, "r:" + r);
				}
			}

			
			dump(doc.body(), cssQuery);

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

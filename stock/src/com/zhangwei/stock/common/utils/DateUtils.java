/**
 * 
 */
package com.zhangwei.stock.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wuxiang
 *
 * @create 2013-4-10
 */
public class DateUtils {
	
	public static final String DATE_FORMAT_NORMAL="yyyy-MM-dd HH:mm";
	public static final String DATE_FORMAT_TODAY="HH:mm";

	public static Date stringToDate(String date,String format){
		try {
			SimpleDateFormat dateFormat=new SimpleDateFormat(format);
			Date dDate=dateFormat.parse(date);
			return dDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//根据给定日期返回制定
	public static String dateFormat(String date,String inFormat,String outFormat){
		SimpleDateFormat dateFormat=new SimpleDateFormat(outFormat);
		return dateFormat.format(stringToDate(date,inFormat));
	}
	
	public static String dateToString(Date date,String format){
		SimpleDateFormat dateFormat=new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	public static boolean isToday(String date,String format){
		Date dDate=stringToDate(date,format);
		Date today=new Date();
		Date start=new Date(today.getYear(), today.getMonth(), today.getDate(), 0, 0, 0);
		Date end=new Date(today.getYear(), today.getMonth(), today.getDate(), 23, 59, 59);
		if(dDate.getTime()>=start.getTime()&&dDate.getTime()<=end.getTime())
			return true;
		return false;
	}
	
}

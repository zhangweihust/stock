package com.zhangwei.stock.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputCheck{
	/**
	 * 验证有1-3位小数的正实数
	 * */
	public static boolean checkNum(String num) 
    {  
        try{            
            Pattern pattern = Pattern.compile("^[0-9]+(.[0-9]{1,3})?$");
            Matcher matcher = pattern.matcher(num);
            return matcher.matches();

        } 
        catch(Exception e){
            e.printStackTrace();
        }
        return false; 
    }
}
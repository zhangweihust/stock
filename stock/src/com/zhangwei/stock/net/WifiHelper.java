package com.zhangwei.stock.net;

import android.telephony.TelephonyManager;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

public class WifiHelper {
	public static final String VALUE_EMPTY = "";
	public static final String VALUE_WIFI = "WIFI";
	public static final String VALUE_GPRS = "GPRS";
	public static final String VALUE_3G = "3G";
	
	public static String getNetType() {
		String result = VALUE_EMPTY;
		if (ZNetworkStateDetector.isWifi()) {
			result = VALUE_WIFI;
		} else {
			int type = ZNetworkStateDetector.getMobileType();
			switch (type) { // 没验证过这种2G/3G的检测方式是否正确, 暂时这样实现;
			case TelephonyManager.NETWORK_TYPE_UMTS:
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				result = VALUE_3G;
				break;

			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_EDGE:
			case TelephonyManager.NETWORK_TYPE_GPRS:
				result = VALUE_GPRS;
				break;
			}
		}
		return result;
	}
}

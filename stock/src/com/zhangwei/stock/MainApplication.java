package com.zhangwei.stock;

import cn.zipper.framwork.core.ZApplication;

public class MainApplication extends ZApplication {
    private static MainApplication mInstance = null;
	
	@Override
	public void onCreate() {
		super.onCreate();

		mInstance = this;
	}
	
	public static MainApplication getAppInstance() {
		return mInstance;
	}
	



}

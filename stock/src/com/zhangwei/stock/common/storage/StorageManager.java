package com.zhangwei.stock.common.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;



import cn.zipper.framwork.core.ZApplication;

import com.google.gson.Gson;
import com.zhangwei.stock.common.utils.MD5;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * @author zhangwei
 */
public class StorageManager {
	public static final String CHACHE_PREFIX = "storage_";  

	private static final String TAG = "StorageManager";
	private static StorageManager instance;
	private HashMap<String, StorageValue> cache;
	private Context context;
	private static Gson gson;

	private class CacheFilter implements FilenameFilter {

		public boolean isCache(String file) {
			if (file.toLowerCase(Locale.ENGLISH).startsWith(CHACHE_PREFIX)) {
				return true;
			} else {
				return false;
			}
		}

		public boolean accept(File dir, String fname) {
			return (isCache(fname));

		}

	}

	private StorageManager(Context c) {
		context = c;
		cache = new HashMap<String, StorageValue>();
		gson = new Gson();
		load();
	}

	private void load() {
		Log.e(TAG, "CacheManager load");
		FilenameFilter filter = new CacheFilter();
		File[] filelist = context.getFilesDir().listFiles(filter);
		for (File f : filelist) {
			if(f.isFile()){
				cache.put(f.getName(), new StorageValue(f.getName(), (int) f.length()));
			}else if(f.isDirectory()){
				load(f) ;
			}
		}
	}
	
	private void load(File fileDir) {
		FilenameFilter filter = new CacheFilter();
		File[] filelist = fileDir.listFiles(filter);
		for (File f : filelist) {
			if(f.isFile()){
				cache.put(f.getName(), new StorageValue(f.getName(), (int) f.length()));
			}else if(f.isDirectory()){
				load(f) ;
			}
			
		}
	}

	public static StorageManager getInstance(Context c) {
		Context context = null;
		if (c != null) {
			context = c;
		} else {
			context = ZApplication.getInstance();
		}

		if (instance == null) {
			instance = new StorageManager(context);
		}

		return instance;
	}

	/**
	 * @return 被Cache的元数据信息
	 */
	public String getItem(String uri) {
		//String key = MD5.encode(CHACHE_PREFIX, uri.getBytes());
		//String key = MD5.encode(CHACHE_PREFIX, uri.getBytes());
		String key = CHACHE_PREFIX + uri;
		if (cache.containsKey(key)) {
			// Read the created file and display to the screen
			try {
				FileInputStream mInput = context.openFileInput(key);
				int len = 0;
				byte[] data = new byte[1024];
				StringBuilder sb = new StringBuilder();

				while ((len = mInput.read(data)) != -1) {
					sb.append(new String(data, 0, len));
				}

				mInput.close();
				return sb.toString();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		} else {
			return null;
		}
	}

	/**
	 * @return 被Cache的元数据信息
	 */
	public Object getItem(String uri, Class<?> cls) {
		String jsonStr = getItem(uri);
		Object object = null;
		try{
			if (jsonStr != null) {
				object = gson.fromJson(jsonStr, cls);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}

		return object;
	}
	
	public Object getItem(String uri, Type type) {
		String jsonStr = getItem(uri);
		Object object = null;
		try{
			if (jsonStr != null) {
				object = gson.fromJson(jsonStr, type);
			}
		}catch(Exception e){
			e.printStackTrace();
		}


		return object;
	}

	/**
	 * 
	 * @param uri 要查找的资源uri
	 * @param objStr 将json String作为内容写入内存储(overwrite)
	 */
	public StorageValue putItem(String dir, String uri, String objStr) {
		//String key = MD5.encode(CHACHE_PREFIX, uri.getBytes());

		String key = CHACHE_PREFIX + uri;
		FileOutputStream mOutput = null;
		StorageValue result = null;
		String path;
		
		if(dir!=null){
			File fileDir= new File(dir);
			if(!fileDir.exists()) {
				fileDir.mkdirs();
			}
			
			path = dir + "/" + key;
		}else{
			path = key;
		}
		
		try {
			// overwrite
			mOutput = context.openFileOutput(path, Activity.MODE_PRIVATE);
			mOutput.write(objStr.getBytes());
			mOutput.close();
			result = cache.put(key, new StorageValue(key,
					objStr.getBytes().length));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param uri 要插入的资源uri
	 * @param object json Object
	 * @param cls json对象类型
	 */
	public StorageValue putItem(String dir, String file, Object object, Class<?> cls) {

		String jsonStr = null;
		jsonStr = gson.toJson(object, cls);
		if (jsonStr != null) {
			return putItem(dir, file, jsonStr);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param uri
	 *            要删除的资源uri
	 */
	public void deleteItem(String uri) {
		String key = CHACHE_PREFIX + uri;
		//String key = MD5.encode(CHACHE_PREFIX, uri.getBytes());
		if (cache.containsKey(key)) {
			cache.remove(key);
			context.deleteFile(key);
		}
	}

	public void cleanAll() {
		Iterator<Entry<String, StorageValue>> iter = cache.entrySet()
				.iterator();

		while (iter.hasNext()) {
			Map.Entry<String, StorageValue> entry = (Map.Entry<String, StorageValue>) iter
					.next();
			String key = entry.getKey();
			context.deleteFile(key);
		}

		cache.clear();

	}

}
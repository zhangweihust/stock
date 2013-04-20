package com.zhangwei.stock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class Dump {
	private static String TAG = "DumpString";
	private static String FILENAME = "DumpString.txt";

	private static Dump instance;
	private int Seq;

	private Dump() {
		Seq = 0;

	}

	public static Dump getInstance() {
		if (instance != null) {
			return instance;
		} else {
			instance = new Dump();
			return instance;
		}
	}

	private synchronized void incSeq() {
		Seq++;
	}

	private void print(byte[] b,int offset, int len){
		if(len<=0){
			return;
		}
		
		incSeq();
		

		byte[] c = new byte[len];
		System.arraycopy(b, offset, c, 0, len);
		// Create the file reference
		File dataFile = new File(Environment.getExternalStorageDirectory(),
				FILENAME);

		// Check if external storage is usable
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.e(TAG, "find no external storage");
			return;
		}
		


		// Create a new file and write some data
		try {
			String head = "\nSeq:" + Seq + ":\n";
			FileOutputStream mOutput = new FileOutputStream(dataFile, true);
			mOutput.write(head.getBytes());

			//printStack(mOutput);
        	Date now = new Date();
        	DateFormat df = DateFormat.getTimeInstance(); 
        	//DateFormat df = DateFormat.getDateTimeInstance();
        	StringBuilder sb = new StringBuilder();
        	sb.append(df.format(now) + "\n");
        	mOutput.write(sb.toString().getBytes());
        	
			mOutput.write(c);

			mOutput.close();
			Log.d(TAG, c.toString());

			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void printStack(FileOutputStream mOutput){/*

		Throwable ex = new Throwable();  
        StackTraceElement[] stackElements = ex.getStackTrace();
        StringBuilder sb = new StringBuilder();
        if (stackElements != null) {  
        	sb.append("---------------\n");
        	//Date now = new Date();
        	//DateFormat df = DateFormat.getTimeInstance(); 
        	//DateFormat df = DateFormat.getDateTimeInstance();
        	
        	//sb.append(df.format(now) + "\n");
            for (int i = 0; i < stackElements.length; i++) {  
                sb.append(stackElements[i].getClassName()+"\t"); 
                sb.append(stackElements[i].getFileName()+"\t"); 
                sb.append(stackElements[i].getLineNumber()+"\t"); 
                sb.append(stackElements[i].getMethodName() + "\n"); 

            }  
        	sb.append("---------------\n");
            try {
				mOutput.write(sb.toString().getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        } 
	*/}
	
	public static void Print(byte[] b, int len) {
		Dump inc = getInstance();
		inc.print(b, 0, len);

	}
	
	public static void Print(byte[] b, int offset, int len) {
		Dump inc = getInstance();
		inc.print(b,offset,len);

	}
	
	public static void Print(String s) {
		if(s==null){
			return;
		}
		Dump inc = getInstance();
		inc.print(s.getBytes(), 0, s.length());

	}

}

package com.emzac.networking;

import android.util.Log;

public class ManagerError {
	
	public static String env = "PRODUCTION";

	public ManagerError() {
		
	}
	
	public static void newError(Exception e) {
		
		try {
			String env = ManagerError.env;
			
			if ( env == "DEVELOPMENT" ) {
				e.printStackTrace();
			}
			
		} catch (Exception e2) {
			// TODO: handle exception
		}
	}
	
	public static void newLogMessage(String tag, String msg) {
		
		try {
			String env = ManagerError.env;
			
			if ( env == "DEVELOPMENT" ) {
				Log.d(tag,msg);
			}
			
		} catch (Exception e2) {
			// TODO: handle exception
		}
	}
	
	public static void newLogMessage(String tag, String msg, Exception e) {
		
		try {
			String env = ManagerError.env;
			
			if ( env == "DEVELOPMENT" ) {
				Log.d(tag,msg);
				e.printStackTrace();
			}
			
		} catch (Exception e2) {
			// TODO: handle exception
		}
	}
	
	public static void newLogMessage(String tag, String msg, Throwable t) {
		
		try {
			String env = ManagerError.env;
			
			if ( env == "DEVELOPMENT" ) {
				Log.d(tag,msg);
				t.printStackTrace();
			}
			
		} catch (Exception e2) {
			// TODO: handle exception
		}
	}
}

package com.emzac.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Queue extends CordovaPlugin {
	
	public static JSONArray notisQueue = new JSONArray();

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
	    if ("notifications".equals(action)) {
	        this.notifications(callbackContext);
	        callbackContext.success();
	        return true;
	    }
	    return false;  // Returning false results in a "MethodNotFound" error.
	}
	
	private void notifications(CallbackContext callbackContext) {
		
		try {
			JSONObject response = new JSONObject();
			
			response.put("cant", notisQueue.length());
			response.put("notis", notisQueue);
			
			notisQueue = new JSONArray();
			
			callbackContext.success(response);
			
		} catch (Exception e) {
			callbackContext.error(e.getMessage());
		}
		
	}
}

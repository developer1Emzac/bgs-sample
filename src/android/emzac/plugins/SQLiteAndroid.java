package com.emzac.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.emzac.db.DatabaseHelper;
import com.emzac.networking.ManagerError;

import android.content.Context;

public class SQLiteAndroid extends CordovaPlugin {

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
	    if ("update".equals(action)) {
	    	int user = args.getInt(0);
	        this.update(user, callbackContext);
	        callbackContext.success();
	        return true;
	    } else if ("delete".equals(action)) {
	    	this.delete(args, callbackContext);
	        callbackContext.success();
	        return true;
	    }
	    return false;  // Returning false results in a "MethodNotFound" error.
	}
	
	private void update(int user, CallbackContext callbackContext) {
		
		try {
			Context context = this.cordova.getActivity().getApplicationContext();
			
			DatabaseHelper dbHelper = new DatabaseHelper(context);
			boolean foo = dbHelper.update(user);
			
			if ( foo ) {
				callbackContext.success();
			} else {
				ManagerError.newLogMessage(
					"SQLiteAndroid.update","No guardo");
				callbackContext.error("Ocurrio un error.");
			}

		} catch ( Exception e) {
			ManagerError.newLogMessage(
				"SQLiteAndroid.update",e.getMessage());
			callbackContext.error("Ocurrio un error.");
		}
	}
	
	private void delete(JSONArray dataCalendar, CallbackContext callbackContext) {
		
		/*try {
			JSONObject obj = dataCalendar.getJSONObject(0);
	  	  	callbackContext.success();
		} catch ( Exception e) {
			callbackContext.error("Error al abrir el calendario.");
		}*/
	}
}

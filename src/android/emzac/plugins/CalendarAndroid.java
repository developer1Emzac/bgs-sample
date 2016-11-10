package com.emzac.plugins;

import java.util.Calendar;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CalendarAndroid extends CordovaPlugin {
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
	    if ("create".equals(action)) {
	        this.insert(args, callbackContext);
	        callbackContext.success();
	        return true;
	    }
	    return false;  // Returning false results in a "MethodNotFound" error.
	}
	
	private void insert(JSONArray dataCalendar, CallbackContext callbackContext) {
		
		try {
			JSONObject obj = dataCalendar.getJSONObject(0);
			String title = obj.getString("title");
			String description = obj.getString("description");
			String location = obj.getString("location");
			String sBeginTime = obj.getString("beginTime");
			String sEndTime = obj.getString("endTime");
			
			DateTimeFormatter formatter = DateTimeFormat.forPattern("M/d/yyyy HH:mm:ss");
			DateTime dtBegin = formatter.parseDateTime(sBeginTime);
			DateTime dtEnd = formatter.parseDateTime(sEndTime);
			
	  	  	Calendar beginTime = Calendar.getInstance();
	  	  	beginTime.set(
	  	  			dtBegin.getYear(), 
	  	  			dtBegin.getMonthOfYear()-1,
	  	  			dtBegin.getDayOfMonth(),
	  	  			dtBegin.getHourOfDay(),
	  	  			dtBegin.getMinuteOfHour());
	  	  	
	  	  	Calendar endTime = Calendar.getInstance();
	  	  	endTime.set(
	  	  			dtEnd.getYear(), 
  	  				dtEnd.getMonthOfYear()-1,
	  	  			dtEnd.getDayOfMonth(),
	  	  			dtEnd.getHourOfDay(),
	  				dtEnd.getMinuteOfHour());
	  	  	
	  	  	/*Intent intent = new Intent(Intent.ACTION_INSERT)
	          .setData(Events.CONTENT_URI)
	          .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
	          .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
	          .putExtra(Events.TITLE, title)
	          .putExtra(Events.DESCRIPTION, description)
	          .putExtra(Events.EVENT_LOCATION, location);
	  	  	cordova.getActivity().startActivity(intent);*/
	  	  	
	  	  	callbackContext.success();
		} catch ( Exception e) {
			callbackContext.error("Error al abrir el calendario.");
		}
	}

}

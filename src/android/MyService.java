package com.red_folder.phonegap.plugin.backgroundservice.sample;

import java.text.DateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Vibrator;

import com.emzac.networking.ManagerError;
import com.red_folder.phonegap.plugin.backgroundservice.BackgroundService;

public class MyService extends BackgroundService {
	
	private final static String TAG = MyService.class.getSimpleName();
	
	private String mHelloTo = "World";
	private int user = -1;

	@Override
	protected JSONObject doWork() {
		JSONObject result = new JSONObject();
		
		try {
			DateFormat df = DateFormat.getDateTimeInstance();
			String now = df.format( new Date( System.currentTimeMillis() ) );

			String msg = "Hello " + this.mHelloTo + " - its currently " + now;
			result.put("Message", msg);
			result.put("user_id", this.user);
			
			NotificationManager mNotificationManager =
				    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			Vibrator vibrar = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			
			ConnectivityManager cm =
			        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			
			MyServiceControl sc = new MyServiceControl();
			
			sc.setUser(this.user);
			sc.setMyService(this);
			sc.setContext(getBaseContext());
			sc.setNotificationManager(mNotificationManager);
			sc.setVibrar(vibrar);
			sc.setConnectivity(cm);
			
			JSONObject jsonResponse = sc.iniciarVerificacion();
			result.put("response", jsonResponse);
			
			ManagerError.newLogMessage(TAG,msg);
		} catch (JSONException e) {
		}
		
		return result;	
	}

	@Override
	protected JSONObject getConfig() {
		JSONObject result = new JSONObject();
		
		try {
			result.put("HelloTo", this.mHelloTo);
			result.put("user", this.user);
		} catch (JSONException e) {
		}
		
		return result;
	}

	@Override
	protected void setConfig(JSONObject config) {
		try {
			if (config.has("HelloTo"))
				this.mHelloTo = config.getString("HelloTo");

			if (config.has("user"))
				this.user = config.getInt("user");
		} catch (JSONException e) {
		}
		
	}     

	@Override
	protected JSONObject initialiseLatestResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onTimerEnabled() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onTimerDisabled() {
		// TODO Auto-generated method stub
		
	}


}

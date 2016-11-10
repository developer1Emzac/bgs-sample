package com.red_folder.phonegap.plugin.backgroundservice.sample;

import org.apache.cordova.*;
import org.json.JSONObject;

import com.emzac.plugins.Queue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MyServiceActivity5 extends CordovaActivity {
	
	public static String evento = "1";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        Context context     = getApplicationContext();
        String packageName  = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);

        launchIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        JSONObject noti = new JSONObject();
        JSONObject dataNoti = new JSONObject();
        
        try {
        	noti.put("name", "detalle-evento");
        	dataNoti.put("id_evento", MyServiceActivity5.evento);
        	noti.put("data", dataNoti);
        	Queue.notisQueue.put(noti);
        } catch (Exception e) {
			//callbackContext.error(e.getMessage());
		}

        context.startActivity(launchIntent);
        //super.onCreate(savedInstanceState);
        //super.loadUrl("file:///android_asset/www/index.html#detalle-evento?ev=" + MyServiceActivity5.evento);
    }
}

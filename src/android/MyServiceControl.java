package com.red_folder.phonegap.plugin.backgroundservice.sample;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.emzac.db.DatabaseHelper;
import com.emzac.networking.ManagerError;
import com.emzac.networking.R;

public class MyServiceControl {
	
	private int user = -1; 
	private Context context;
	private MyService obj;
	private NotificationManager nm;
	private ConnectivityManager cm;
	private Vibrator vibrar;
	private String defaultError = "{\"error\":true,\"msj\":\"Ocurrio un error\"}";

	public MyServiceControl() {
	}
	
	public JSONObject iniciarVerificacion() {
		
		if ( this.user == -1 ) {
			this.user = this.getIdFromDB();
		}
		
		JSONObject defaultResponse = new JSONObject();
		JSONObject jsonResponse = new JSONObject();
		
		try {
			defaultResponse.put("error", "true");
			
			if ( this.user == -1 ) {
				ManagerError.newLogMessage(
					"Control.iniciarVerificacion","id de la db -1");
				return defaultResponse;
			} else {
				ManagerError.newLogMessage(
					"Control.iniciarVerificacion","El id de la db es: " + this.user);
			}
			
			String HttpResponse = this.request();
			ManagerError.newLogMessage("Http Response:", HttpResponse);

			JSONObject data = new JSONObject(HttpResponse);
			jsonResponse = data;
			
			boolean error = data.getBoolean("error");
			
			if ( error ) {
				return jsonResponse;
			}
			
			this.verificarInvi(data);
			this.verificarAcep(data);
			this.verificarChat(data);
			this.verificarBoletin(data);

		} catch (JSONException e) {
			jsonResponse = defaultResponse;
		}
		
		return jsonResponse;
		
	}
	
	public void verificarInvi(JSONObject data) {
		try {
			int cant = data.getInt("cant_invi");
			
			DatabaseHelper dbHelper = new DatabaseHelper(this.context);
			
			if ( cant == 0 ) {
				ManagerError.newLogMessage(
					"Control.verificarInvi","cero notificaciones");
				dbHelper.zeroNotis(1);
			} else if ( cant > 0 ) {
				
				String pendientes = cant + " invitaciones pendientes.";
				
				if ( cant == 1 ) {
					pendientes = "1 invitación pendiente.";
				}
				
				int cantNotis = dbHelper.cantNotis(1);
				
				DateTime dt = new DateTime();
				DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
				String fecha = fmt.print(dt);
				
				if ( cant == cantNotis ) {
					ManagerError.newLogMessage(
						"Control.verificarInvi","igual cantidad de notis");
					String fechaNoti1 = dbHelper.fechaNotis(1);
					if ( fechaNoti1 == "-1" ) {
						ManagerError.newLogMessage(
							"Control.verificarInvi","fechaNoti1 = -1");
						dbHelper.updateNotis(1,cant, fecha);
						ManagerError.newLogMessage(
							"Control.verificarInvi","lanzar notificacion de invitaciones");
						this.showNotification("Networking", pendientes, 1, MyServiceActivity.class);
						this.vibrate(500);
						this.beep(1);
					} else {
						ManagerError.newLogMessage(
							"Control.verificarInvi","fechaNoti1 != -1");
						DateTime dtNoti = new DateTime(fechaNoti1);
						Long milisNoti = dtNoti.getMillis();
						Long milis = dt.getMillis();
						Long diference = milis-milisNoti;
						Long difHours = diference/3600000;
						
						ManagerError.newLogMessage(
							"Control.verificarInvi","milis notificacion "+milisNoti);
						ManagerError.newLogMessage(
							"Control.verificarInvi","milis "+milis);
						ManagerError.newLogMessage(
							"Control.verificarInvi","diference "+diference);
						ManagerError.newLogMessage(
							"Control.verificarInvi","diferencia horas "+difHours);
						
						if ( difHours > 3 ) {
							ManagerError.newLogMessage(
								"Control.verificarInvi","mas de 3 horas desde la ultima noti");
							dbHelper.updateNotis(1,cant, fecha);
							ManagerError.newLogMessage(
								"Control.verificarInvi","lanzar notificacion de invitaciones");
							this.showNotification("Networking", pendientes, 1, MyServiceActivity.class);
							this.vibrate(500);
							this.beep(1);
						}
					}
				} else {
					ManagerError.newLogMessage(
						"Control.verificarInvi","!= cantidad de notis");
					dbHelper.updateNotis(1,cant, fecha);
					ManagerError.newLogMessage(
						"Control.verificarInvi","lanzar notificacion de invitaciones");
					this.showNotification("Networking", pendientes, 1, MyServiceActivity.class);
					this.vibrate(500);
					this.beep(1);
				}

			}
			
		} catch (JSONException e) {
			ManagerError.newLogMessage(
				"Control.verificarInvi","error verificar invitaciones");
		}
	}
	
	public void verificarAcep(JSONObject data) {
		
		try {
			int cant = data.getInt("cant_acep");
			
			DatabaseHelper dbHelper = new DatabaseHelper(this.context);
			
			if ( cant == 0 ) {
				ManagerError.newLogMessage(
					"Control.verificarAcep","cero notificaciones");
				dbHelper.zeroNotis(2);
			} else if ( cant > 0 ) {
				
				String acept = cant + " nuevos contactos.";
				
				if ( cant == 1 ) {
					acept = "1 nuevo contacto.";
				}
				
				int cantNotis = dbHelper.cantNotis(2);
				
				DateTime dt = new DateTime();
				DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
				String fecha = fmt.print(dt);
				
				if ( cant == cantNotis ) {
					ManagerError.newLogMessage(
						"Control.verificarAcep","igual cantidad de notis");
					String fechaNoti2 = dbHelper.fechaNotis(2);
					if ( fechaNoti2 == "-1" ) {
						ManagerError.newLogMessage(
							"Control.verificarAcep","fechaNoti2 = -1");
						dbHelper.updateNotis(2,cant, fecha);
						ManagerError.newLogMessage(
							"Control.verificarAcep","lanzar notificacion de aceptaciones");
						this.showNotification("Networking", acept, 2, MyServiceActivity2.class);
						this.vibrate(500);
						this.beep(1);
					} else {
						ManagerError.newLogMessage(
							"Control.verificarAcep","fechaNoti2 != -1");
						DateTime dtNoti = new DateTime(fechaNoti2);
						Long milisNoti = dtNoti.getMillis();
						Long milis = dt.getMillis();
						Long diference = milis-milisNoti;
						Long difHours = diference/3600000;
						
						ManagerError.newLogMessage(
							"Control.verificarAcep","milis notificacion "+milisNoti);
						ManagerError.newLogMessage(
							"Control.verificarAcep","milis "+milis);
						ManagerError.newLogMessage(
							"Control.verificarAcep","diference "+diference);
						ManagerError.newLogMessage(
							"Control.verificarAcep","diferencia horas "+difHours);
						
						if ( difHours > 3 ) {
							ManagerError.newLogMessage(
								"Control.verificarAcep","mas de 3 horas desde la ultima noti");
							dbHelper.updateNotis(2,cant, fecha);
							ManagerError.newLogMessage(
								"Control.verificarAcep","lanzar notificacion de aceptaciones");
							this.showNotification("Networking", acept, 2, MyServiceActivity2.class);
							this.vibrate(500);
							this.beep(1);
						}
					}
				} else {
					ManagerError.newLogMessage(
						"Control.verificarAcep","!= cantidad de notis");
					dbHelper.updateNotis(2,cant, fecha);
					ManagerError.newLogMessage(
						"Control.verificarAcep","lanzar notificacion de aceptaciones");
					this.showNotification("Networking", acept, 2, MyServiceActivity2.class);
					this.vibrate(500);
					this.beep(1);
				}

			}
			
		} catch (JSONException e) {
			ManagerError.newLogMessage(
				"Control.verificarAcep","error verificar aceptaciones");
		}
	}
	
	public void verificarChat(JSONObject data) {
		
		try {
			DatabaseHelper dbHelper = new DatabaseHelper(this.context);
			int cantChatsNoRead = data.getInt("cant_no_read");
			int cantConverNoRead = data.getInt("conver_no_read");
			
			if ( cantChatsNoRead == 0 ) {
				ManagerError.newLogMessage(
					"Control.verificarChat","cero notificaciones");
				dbHelper.zeroNotis(3);
				dbHelper.zeroNotis(4);
				cancelNotification(3);
			} else {
				String strMsj = " mensajes";
				if ( cantChatsNoRead == 1 ) {
					strMsj = " mensaje";
				}
				
				String strConv = "";
				if ( cantConverNoRead > 1 ) {
					strConv = " de " + cantConverNoRead + " conversaciones";
				}
				
				String chatMsj = cantChatsNoRead + strMsj + strConv;
				
				DateTime dt = new DateTime();
				DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
				String fecha = fmt.print(dt);
				
				int cantNotis3 = dbHelper.cantNotis(3);
				int cantNotis4 = dbHelper.cantNotis(4);
				
				if ( cantNotis3 != cantChatsNoRead 
					|| cantNotis4 != cantConverNoRead ) {
					ManagerError.newLogMessage(
						"Control.verificarChat","!= cantidad de notis");
					dbHelper.updateNotis(3,cantChatsNoRead, fecha);
					dbHelper.updateNotis(4,cantConverNoRead, fecha);
					ManagerError.newLogMessage(
						"Control.verificarChat","lanzar notificacion de chat");
					this.showNotification("Networking", chatMsj, 3, MyServiceActivity3.class);
					this.vibrate(500);
					this.beep(1);
				} else {
					String fechaNoti3 = dbHelper.fechaNotis(3);
					if ( fechaNoti3 == "-1" ) {
						ManagerError.newLogMessage(
							"Control.verificarChat","fechaNoti3 = -1");
						dbHelper.updateNotis(3,cantChatsNoRead, fecha);
						dbHelper.updateNotis(4,cantConverNoRead, fecha);
						ManagerError.newLogMessage(
							"Control.verificarChat","lanzar notificacion de chat");
						this.showNotification("Networking", chatMsj, 3, MyServiceActivity3.class);
						this.vibrate(500);
						this.beep(1);
					} else {
						ManagerError.newLogMessage(
							"Control.verificarChat","fechaNoti3 != -1");
						DateTime dtNoti = new DateTime(fechaNoti3);
						Long milisNoti = dtNoti.getMillis();
						Long milis = dt.getMillis();
						Long diference = milis-milisNoti;
						Long difHours = diference/3600000;
						
						ManagerError.newLogMessage(
							"Control.verificarChat","milis notificacion "+milisNoti);
						ManagerError.newLogMessage(
							"Control.verificarChat","milis "+milis);
						ManagerError.newLogMessage(
							"Control.verificarChat","diference "+diference);
						ManagerError.newLogMessage(
							"Control.verificarChat","diferencia horas "+difHours);
						
						if ( difHours > 3 ) {
							ManagerError.newLogMessage(
								"Control.verificarChat","mas de 3 horas desde la ultima noti");
							dbHelper.updateNotis(3,cantChatsNoRead, fecha);
							dbHelper.updateNotis(4,cantConverNoRead, fecha);
							ManagerError.newLogMessage(
								"Control.verificarChat","lanzar notificacion de chat");
							this.showNotification("Networking", chatMsj, 3, MyServiceActivity3.class);
							this.vibrate(500);
							this.beep(1);
						}
					}
				}

			}
			
			
		} catch (JSONException e) {
			ManagerError.newLogMessage(
				"Control.verificarChat","excepcion al verificar chat");
		}
	}
	
	public void verificarBoletin(JSONObject data) {
		
		try {
			int cant = data.getInt("cant_boletines");
			
			if ( cant == 0 ) {
				ManagerError.newLogMessage(
					"Control.verificarBoletin","cero boletines");
			} else if ( cant > 0 ) {
				
				JSONArray boletines = data.getJSONArray("boletines");
				JSONObject boletin = boletines.getJSONObject(0);
				
				String titulo = boletin.getString("titulo");
				String text = boletin.getString("contenido");
				String tipo = boletin.getString("tipo");
				
				if ( tipo.equals("1") ) {
					String evento = boletin.getString("evento");
					MyServiceActivity5.evento = evento;
					ManagerError.newLogMessage(
							"Control.verificarBoletin","lanzar notificacion de boletin");
					this.showNotification2(titulo, text, 5, MyServiceActivity5.class);
					this.vibrate(500);
					this.beep(1);
				} else if ( tipo.equals("2") ) {
					String link = boletin.getString("link");
					ManagerError.newLogMessage(
							"Control.verificarBoletin","lanzar notificacion de boletin-link-externo");
					this.LinkNotification(titulo, text, 5, link);
					this.vibrate(500);
					this.beep(1);
				} else {
					ManagerError.newLogMessage(
							"Control.verificarBoletin","tipo no identificado");
				}

			}
			
		} catch (JSONException e) {
			ManagerError.newLogMessage(
				"Control.verificarBoletin","error verificar boletin");
		}
	}
	
	public boolean isOnline() {
	    NetworkInfo netInfo = this.cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	private String request() {
		
		if ( !this.isOnline() ) {
			return this.defaultError;
		}
		
		String user = Integer.toString(this.user);
		
		try {
			String uri = this.context.getString(R.string.networking_app_url);
			URL url = new URL(uri + "/request.php");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			
			String urlParameters = "request=status&userId=" + user;
			
			urlConnection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			
			int responseCode = urlConnection.getResponseCode();
			
			if ( responseCode != 200 ) {
				ManagerError.newLogMessage(
					"Control.request","Failed to get JSON object");
				urlConnection.disconnect();
    			return this.defaultError;
			}

			BufferedReader in = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			urlConnection.disconnect();
			return response.toString();
			
		} catch(Exception e) {
			ManagerError.newError(e);
            return this.defaultError;
		} finally {
			//urlConnection.disconnect();
		}
        
        //return "{\"error\":true,\"msj\":\"Debe ser un usuario valido para realizar esta acci\u00f3n.\",\"msj_code\":\"rp_usuario_no_existe\"}";
	}
	
	/**
	 * 
	 * @param title
	 * @param text
	 * @param idNot (1 => Invitacion,2 => Aceptacion,3,4 => Chat,5 => Boletin)
	 * @param nameActi
	 */
	public void showNotification(String title, String text, int idNot,Class nameActi) {

		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this.obj)
		        .setSmallIcon(R.drawable.ic_stat_rf)
		        .setContentTitle(title)
		        .setContentText(text)
		        .setAutoCancel(true);
			
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this.obj, nameActi);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		//resultIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		
		//Back to forground
		//resultIntent.setAction("android.intent.action.MAIN");
		//resultIntent.addCategory("android.intent.category.LAUNCHER");
		
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.obj);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(nameActi);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = this.nm;
		// mId allows you to update the notification later on.
		mNotificationManager.notify(idNot, mBuilder.build());
		
	}
	
	/**
	 * 
	 * @param title
	 * @param text
	 * @param idNot (1 => Invitacion,2 => Aceptacion,3,4 => Chat,5 => Boletin)
	 * @param nameActi
	 */
	public void showNotification2(String title, String text, int idNot,Class nameActi) {

		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this.obj)
		        .setSmallIcon(R.drawable.ic_stat_rf)
		        .setContentTitle(title)
		        .setContentText(text)
		        .setAutoCancel(true);
			
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this.obj, nameActi);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.obj);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(nameActi);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = this.nm;
		// mId allows you to update the notification later on.
		mNotificationManager.notify(String.valueOf(System.currentTimeMillis()), idNot, mBuilder.build());
		
	}
	
	private void LinkNotification(String title, String text, int idNot, String link){

		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this.obj)
		        .setSmallIcon(R.drawable.ic_stat_rf)
		        .setContentTitle(title)
		        .setContentText(text)
		        .setAutoCancel(true);

	    NotificationManager mNotificationManager = this.nm;

	    // pending intent is redirection using the deep-link
	    Intent resultIntent = new Intent(Intent.ACTION_VIEW);
	    resultIntent.setData(Uri.parse(link));

	    PendingIntent pending = PendingIntent.getActivity(this.context, 0, resultIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
	    mBuilder.setContentIntent(pending);

	    // using the same tag and Id causes the new notification to replace an existing one
	    mNotificationManager.notify(String.valueOf(System.currentTimeMillis()), idNot, mBuilder.build());
	    
	}
	
	public void cancelNotification(int idNot) {
		NotificationManager mNotificationManager = this.nm;
		mNotificationManager.cancel(idNot);
	}
	
	public int getIdFromDB() {
		ManagerError.newLogMessage(
			"Control.getIdFromDB","Traer id de bd");
		DatabaseHelper dbHelper = new DatabaseHelper(this.context);
		return dbHelper.getIdFirstUser();
	}
	
	public void setUser(int id) {
		this.user = id;
	}
	
	public void setMyService(MyService obj) {
		this.obj = obj;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public void setNotificationManager(NotificationManager nm) {
		this.nm = nm;
	}
	
	public void setVibrar(Vibrator vibrar) {
		this.vibrar = vibrar;
	}
	
	public void setConnectivity(ConnectivityManager cm) {
		this.cm = cm;
	}
	
	public void vibrate(long milliseconds) {
		
		try {
			ManagerError.newLogMessage(
				"Control.vibrate","Vibrar");
			vibrar.vibrate(milliseconds);
		} catch (Exception e) {
			ManagerError.newLogMessage(
				"Control.vibrate","Error al vibrar");
			ManagerError.newError(e);
		}
		
	}
	
	/**
     * Beep plays the default notification ringtone.
     *
     * @param count     Number of times to play notification
     */
	public void beep(long count) {
		
		ManagerError.newLogMessage(
			"Control.beep","Beep");
		Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone notification = RingtoneManager.getRingtone(this.context, ringtone);

        // If phone is not set to silent mode
        if (notification != null) {
            for (long i = 0; i < count; ++i) {
                notification.play();
                long timeout = 5000;
                while (notification.isPlaying() && (timeout > 0)) {
                    timeout = timeout - 100;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
		
	}
}

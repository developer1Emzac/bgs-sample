package com.emzac.db;

import com.emzac.networking.ManagerError;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context) {
		super(context, "networking", null, 2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS user_sqlite (_id unique primary key)");
		db.execSQL("CREATE TABLE IF NOT EXISTS notis_sqlite (_id unique primary key,cant Integer,fecha TEXT)");
		this.insertIfNoExist(db);
		this.insertIfNoExist2(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		if ( oldVersion == 1 && newVersion == 2 ) {
			db.execSQL("CREATE TABLE IF NOT EXISTS notis_sqlite (_id unique primary key,cant Integer,fecha TEXT)");
			this.insertIfNoExist2(db);
		}
	}
	
	public void insertIfNoExist(SQLiteDatabase db) {
		try {

			int cant = this.cantUser(db);
			
			if ( cant == 0 ) {
				ContentValues cv = new ContentValues();
				cv.put("_id", -1);
				db.insert("user_sqlite", "_id", cv);
			}
		} catch (SQLiteException e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.insertIfNoExist","Insert if no exist error");
		} catch (Exception e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.insertIfNoExist",e.getMessage());
		}
	}
	
	public void insertIfNoExist2(SQLiteDatabase db) {
		try {

			int cant = this.cantNotis(db);
			
			if ( cant == 0 ) {
				ContentValues cv = new ContentValues();
				cv.put("_id", 1);
				cv.put("cant", 0);
				cv.put("fecha", "-1");
				db.insert("notis_sqlite", "_id", cv);
				cv.put("_id", 2);
				cv.put("cant", 0);
				cv.put("fecha", "-1");
				db.insert("notis_sqlite", "_id", cv);
				cv.put("_id", 3);
				cv.put("cant", 0);
				cv.put("fecha", "-1");
				db.insert("notis_sqlite", "_id", cv);
				cv.put("_id", 4);
				cv.put("cant", 0);
				cv.put("fecha", "-1");
				db.insert("notis_sqlite", "_id", cv);
			}
		} catch (SQLiteException e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.insertIfNoExist2","Insert if no exist error");
		} catch (Exception e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.insertIfNoExist2",e.getMessage());
		}
	}
	
	public boolean update(int id) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			db.execSQL("UPDATE user_sqlite SET _id = "+id);
			return true;
		} catch (SQLiteException e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.update","update error");
			return false;
		} catch (Exception e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.update",e.getMessage());
			return false;
		}
	}
	
	public int cantUser(SQLiteDatabase db) {
		try {
			Cursor cur = db.rawQuery("SELECT _id as cant FROM user_sqlite LIMIT 1",new String [] {});

			return cur.getCount();
		} catch (SQLiteException e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.cantUser","cant User error");
			return -1;
		} catch (Exception e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.cantUser",e.getMessage());
			return -1;
		}
	}
	
	public int cantNotis(SQLiteDatabase db) {
		try {
			Cursor cur = db.rawQuery("SELECT _id FROM notis_sqlite",new String [] {});

			return cur.getCount();
		} catch (SQLiteException e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.cantNotis","cant notis error");
			return -1;
		} catch (Exception e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.cantNotis",e.getMessage());
			return -1;
		}
	}
	
	public int getIdFirstUser() {
		
		try {
			SQLiteDatabase db=this.getReadableDatabase();
			
			Cursor cur=db.rawQuery("SELECT _id FROM user_sqlite LIMIT 1",new String [] {});
			
			if ( cur.getCount() == 0 ) {
				return -1;
			}
			
			cur.moveToFirst();
			
			int userId = cur.getInt(0);

			return userId;
		} catch (SQLiteException e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.getIdFirstUser","get id error");
			return -1;
		} catch (Exception e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.getIdFirstUser",e.getMessage());
			return -1;
		}
		
	}
	
	public int cantNotis(int id) {
		
		try {
			SQLiteDatabase db=this.getReadableDatabase();
			
			Cursor cur=db.rawQuery("SELECT cant FROM notis_sqlite WHERE _id="+id,new String [] {});
			
			if ( cur.getCount() == 0 ) {
				return -1;
			}
			
			cur.moveToFirst();
			
			int cant = cur.getInt(0);

			return cant;
		} catch (SQLiteException e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.cantNotis","cant notis"+id+" error");
			return -1;
		} catch (Exception e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.cantNotis",e.getMessage());
			return -1;
		}
		
	}
	
	public void zeroNotis(int id) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			db.execSQL("UPDATE notis_sqlite SET cant = 0,fecha=\"-1\" WHERE _id = "+id);
		} catch (SQLiteException e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.zeroNotis","update error"+id);
		} catch (Exception e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.zeroNotis",e.getMessage());
		}
	}
	
	public void updateNotis(int id, int cant, String fecha) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			db.execSQL("UPDATE notis_sqlite SET cant = "+cant+", fecha = \""+fecha+"\" WHERE _id = "+id);
		} catch (SQLiteException e) {
			Log.d("DatabaseHelper.updateNotis","update error"+id);
		} catch (Exception e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.updateNotis",e.getMessage());
		}
	}
	
	public String fechaNotis(int id) {
		
		try {
			SQLiteDatabase db=this.getReadableDatabase();
			
			Cursor cur=db.rawQuery("SELECT fecha FROM notis_sqlite WHERE _id="+id,new String [] {});
			
			if ( cur.getCount() == 0 ) {
				return "-1";
			}
			
			cur.moveToFirst();
			
			String fecha = cur.getString(0);

			return fecha;
		} catch (SQLiteException e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.fechaNotis","cant notis"+id+" error");
			return "-1";
		} catch (Exception e) {
			ManagerError.newLogMessage(
				"DatabaseHelper.fechaNotis",e.getMessage());
			return "-1";
		}
		
	}

}

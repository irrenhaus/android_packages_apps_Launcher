package com.android.launcher.extended.data;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class ExtendedSettings {

	public static final String preferenceName = "extendedlauncher";
	public static final String homeTag = "home_";
	public static final String sensorTag = "sensor_";
	
	public static int Home_HomeScreens(Context context)
	{
		/*SQLiteDatabase mDatabase;
		ExtendedHomeDBHelper hlp = new ExtendedHomeDBHelper(context);
		mDatabase = hlp.getWritableDatabase();
		
        Cursor eCursor = mDatabase.query(false, "extendedhome", new String[] { "screens" }, "name='home'", null, null, null, null, null);
        eCursor.moveToFirst();
        
        int screens = eCursor.getInt(0);
        
        eCursor.close();
        mDatabase.close();*/
        
        SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);
        
        int screens = prefs.getInt(homeTag+"screens", 3);
        
        return screens;
	}
	public static int Home_DefaultScreen(Context context)
	{
		/*SQLiteDatabase mDatabase;
		ExtendedHomeDBHelper hlp = new ExtendedHomeDBHelper(context);
		mDatabase = hlp.getWritableDatabase();
		
        Cursor eCursor = mDatabase.query(false, "extendedhome", new String[] { "defaultscreen" }, "name='home'", null, null, null, null, null);
        eCursor.moveToFirst();
        
        int screen = eCursor.getInt(0);
        
        eCursor.close();
        mDatabase.close();*/
        
        SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);
        
        int screen = prefs.getInt(homeTag+"defaultscreen", 2);
        
        return screen;
	}
	// irrenhaus
	public static boolean Home_CloseFolders(Context context)
	{
		/*SQLiteDatabase mDatabase;
		ExtendedHomeDBHelper hlp = new ExtendedHomeDBHelper(context);
		mDatabase = hlp.getWritableDatabase();
		
        Cursor eCursor = mDatabase.query(false, "extendedhome", new String[] { "closefolders" }, "name='home'", null, null, null, null, null);
        eCursor.moveToFirst();
        
        int close = eCursor.getInt(0);
        
        eCursor.close();
        mDatabase.close();*/
        
        SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);
        
        int close = prefs.getInt(homeTag+"closefolders", 1);
        
        if(close != 0)
        	return true;
        return false;
	}
	public static void Set_Home_HomeScreens(Context context, int Screens)
	{
		/*SQLiteDatabase mDatabase;
		ExtendedHomeDBHelper hlp = new ExtendedHomeDBHelper(context);
		mDatabase = hlp.getWritableDatabase();
		
        ContentValues updateValues = new ContentValues();
        updateValues.put("screens", Screens);
        
        mDatabase.update("extendedhome", updateValues, "name='home'", null);
        mDatabase.close();*/
		
		SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor edit = prefs.edit();
		
		edit.putInt(homeTag+"screens", Screens);
		
		edit.commit();
        
        Log.d(homeTag, "Number of homescreens set to "+Screens);
	}
	public static void Set_Home_DefaultScreen(Context context, int Screen)
	{
		/*SQLiteDatabase mDatabase;
		ExtendedHomeDBHelper hlp = new ExtendedHomeDBHelper(context);
		mDatabase = hlp.getWritableDatabase();
		
        ContentValues updateValues = new ContentValues();
        updateValues.put("defaultscreen", Screen);
        
        mDatabase.update("extendedhome", updateValues, "name='home'", null);
        mDatabase.close();*/
		
		SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor edit = prefs.edit();
		
		edit.putInt(homeTag+"defaultscreen", Screen);
		
		edit.commit();
        
        Log.d(homeTag, "Default homescreen set to "+Screen);
	}
	
	// irrenhaus
	public static void Set_Home_CloseFolders(Context context, boolean close)
	{
		/*SQLiteDatabase mDatabase;
		ExtendedHomeDBHelper hlp = new ExtendedHomeDBHelper(context);
		mDatabase = hlp.getWritableDatabase();
		
        ContentValues updateValues = new ContentValues();
        
        int value = 0;
        if(close)
        	value = 1;
        
        updateValues.put("closefolders", value);
        
        mDatabase.update("extendedhome", updateValues, "name='home'", null);
        mDatabase.close();*/
		
		SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor edit = prefs.edit();
		
		if(close)
			edit.putInt(homeTag+"closefolders", 1);
		else
			edit.putInt(homeTag+"closefolders", 0);
		
		edit.commit();
        
        Log.d(homeTag, "Default homescreen set to "+close);
	}

    public static int Sensor_Enabled(Context context)
    {
        /*SQLiteDatabase mDatabase;
        ExtendedSensorDBHelper hlp = new ExtendedSensorDBHelper(context);
        mDatabase = hlp.getWritableDatabase();

        Cursor eCursor = mDatabase.query(false, "extendedsensor", new String[] { "sensorenabled" }, "name='rotation'", null, null, null, null, null);
        eCursor.moveToFirst();

        int enabled = eCursor.getInt(0);

        eCursor.close();
        mDatabase.close();*/
        
        SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);
        
        int enabled = prefs.getInt(sensorTag+"sensorenabled", 1);

        return enabled;
    }

    public static void Set_Sensor_Enabled(Context context, int Enabled)
    {
        /*SQLiteDatabase mDatabase;
        ExtendedSensorDBHelper hlp = new ExtendedSensorDBHelper(context);
        mDatabase = hlp.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put("sensorenabled", Enabled);

        mDatabase.update("extendedsensor", updateValues, "name='rotation'", null);
        mDatabase.close();*/
		
		SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor edit = prefs.edit();
		
		edit.putInt(sensorTag+"sensorenabled", Enabled);
		
		edit.commit();

        Log.d(sensorTag, "Sensor-based rotation set to " + Enabled);
    }

	/** Database */
	protected static class ExtendedHomeDBHelper extends SQLiteOpenHelper {
		public ExtendedHomeDBHelper(Context context) {
			super(context, "extendedhome.sqlite", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String ddlScripts = "create table extendedhome (name text, screens integer, defaultscreen integer, closefolders integer);";
			db.execSQL(ddlScripts);
			
			ddlScripts = "insert into extendedhome (name, screens, defaultscreen, closefolders) values ('home', 3, 1, 1);";
			db.execSQL(ddlScripts);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}	

    /** Database */
    protected static class ExtendedSensorDBHelper extends SQLiteOpenHelper {
        public ExtendedSensorDBHelper(Context context) {
            super(context, "extendedsensor.sqlite", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String ddlScripts = "create table extendedsensor (name text, sensorenabled integer);";
            db.execSQL(ddlScripts);

            ddlScripts = "insert into extendedsensor (name, sensorenabled) values ('rotation', 1);";
            db.execSQL(ddlScripts);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
	
}

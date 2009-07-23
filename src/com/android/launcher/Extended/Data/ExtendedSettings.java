package com.android.launcher.Extended.Data;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class ExtendedSettings {

	public static final String PreferenceName = "LauncherExtended";
	public static final String Tag = "ExtendedHome";
	
	public static int Home_HomeScreens(Context context)
	{
		SQLiteDatabase mDatabase;
		ExtendedHomeDBHelper hlp = new ExtendedHomeDBHelper(context);
		mDatabase = hlp.getWritableDatabase();
		
        Cursor eCursor = mDatabase.query(false, "extendedhome", new String[] { "screens" }, "name='home'", null, null, null, null, null);
        eCursor.moveToFirst();
        
        int screens = eCursor.getInt(0);
        
        eCursor.close();
        mDatabase.close();
        
        return screens;
	}
	public static int Home_DefaultScreen(Context context)
	{
		SQLiteDatabase mDatabase;
		ExtendedHomeDBHelper hlp = new ExtendedHomeDBHelper(context);
		mDatabase = hlp.getWritableDatabase();
		
        Cursor eCursor = mDatabase.query(false, "extendedhome", new String[] { "defaultscreen" }, "name='home'", null, null, null, null, null);
        eCursor.moveToFirst();
        
        int screen = eCursor.getInt(0);
        
        eCursor.close();
        mDatabase.close();
        
        return screen;
	}
	public static void Set_Home_HomeScreens(Context context, int Screens)
	{
		SQLiteDatabase mDatabase;
		ExtendedHomeDBHelper hlp = new ExtendedHomeDBHelper(context);
		mDatabase = hlp.getWritableDatabase();
		
        ContentValues updateValues = new ContentValues();
        updateValues.put("screens", Screens);
        
        mDatabase.update("extendedhome", updateValues, "name='home'", null);
        mDatabase.close();
        
        Log.d(Tag, "Number of homescreens set to "+Screens);
	}
	public static void Set_Home_DefaultScreen(Context context, int Screen)
	{
		SQLiteDatabase mDatabase;
		ExtendedHomeDBHelper hlp = new ExtendedHomeDBHelper(context);
		mDatabase = hlp.getWritableDatabase();
		
        ContentValues updateValues = new ContentValues();
        updateValues.put("defaultscreen", Screen);
        
        mDatabase.update("extendedhome", updateValues, "name='home'", null);
        mDatabase.close();
        
        Log.d(Tag, "Default homescreen set to "+Screen);
	}

    public static int Sensor_Enabled(Context context)
    {
        SQLiteDatabase mDatabase;
        ExtendedSensorDBHelper hlp = new ExtendedSensorDBHelper(context);
        mDatabase = hlp.getWritableDatabase();

        Cursor eCursor = mDatabase.query(false, "extendedsensor", new String[] { "sensorenabled" }, "name='rotation'", null, null, null, null, null);
        eCursor.moveToFirst();

        int enabled = eCursor.getInt(0);

        eCursor.close();
        mDatabase.close();

        return enabled;
    }

    public static void Set_Sensor_Enabled(Context context, int Enabled)
    {
        SQLiteDatabase mDatabase;
        ExtendedSensorDBHelper hlp = new ExtendedSensorDBHelper(context);
        mDatabase = hlp.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put("sensorenabled", Enabled);

        mDatabase.update("extendedsensor", updateValues, "name='rotation'", null);
        mDatabase.close();

        Log.d(Tag, "Sensor-based rotation set to " + Enabled);
    }

	/** Database */
	protected static class ExtendedHomeDBHelper extends SQLiteOpenHelper {
		public ExtendedHomeDBHelper(Context context) {
			super(context, "extendedhome.sqlite", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String ddlScripts = "create table extendedhome (name text, screens integer, defaultscreen integer);";
			db.execSQL(ddlScripts);
			
			ddlScripts = "insert into extendedhome (name, screens, defaultscreen) values ('home', 3, 1);";
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

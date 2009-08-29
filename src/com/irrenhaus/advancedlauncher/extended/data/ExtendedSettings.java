package com.irrenhaus.advancedlauncher.extended.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public final class ExtendedSettings {

	public static final String preferenceName = "extendedlauncher";
	public static final String homeTag = "home_";
	public static final String sensorTag = "sensor_";

	public static int Home_HomeScreens(Context context) {

		SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);

		int screens = prefs.getInt(homeTag + "screens", 3);

		return screens;
	}

	public static int Home_DefaultScreen(Context context) {

		SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);

		int screen = prefs.getInt(homeTag + "defaultscreen", 1);

		return screen;
	}

	// irrenhaus
	public static boolean Home_CloseFolders(Context context) {

		SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);

		int close = prefs.getInt(homeTag + "closefolders", 1);

		if (close != 0)
			return true;
		return false;
	}

	public static void Set_Home_HomeScreens(Context context, int Screens) {

		SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor edit = prefs.edit();

		edit.putInt(homeTag + "screens", Screens);

		edit.commit();

		Log.d(homeTag, "Number of homescreens set to " + Screens);
	}

	public static void Set_Home_DefaultScreen(Context context, int Screen) {

		SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor edit = prefs.edit();

		edit.putInt(homeTag + "defaultscreen", Screen);

		edit.commit();

		Log.d(homeTag, "Default homescreen set to " + Screen);
	}

	public static void Set_Home_CloseFolders(Context context, boolean close) {

		SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor edit = prefs.edit();

		if (close)
			edit.putInt(homeTag + "closefolders", 1);
		else
			edit.putInt(homeTag + "closefolders", 0);

		edit.commit();

		Log.d(homeTag, "Default homescreen set to " + close);
	}

	public static int Sensor_Enabled(Context context) {

		SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);

		int enabled = prefs.getInt(sensorTag + "sensorenabled", 1);

		return enabled;
	}

	public static void Set_Sensor_Enabled(Context context, int Enabled) {

		SharedPreferences prefs = context.getSharedPreferences(preferenceName, 0);
		SharedPreferences.Editor edit = prefs.edit();

		edit.putInt(sensorTag + "sensorenabled", Enabled);

		edit.commit();

		Log.d(sensorTag, "Sensor-based rotation set to " + Enabled);
	}

}

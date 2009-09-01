package com.irrenhaus.advancedlauncher;

import com.irrenhaus.advancedlauncher.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ExtendedHomeSettings extends Activity {

	@Override
	protected void onPause() {

		ActivityManager am = (ActivityManager)getSystemService(
                Context.ACTIVITY_SERVICE);
        am.restartPackage("com.irrenhaus.advancedlauncher");
		
		super.onPause();
	}

	public static String TAG = "Launcher - Extended";
	Context context;
	
	SeekBar SeekBarHomeScreens;
	SeekBar SeekBarDefaultHomeScreen;
	
	TextView TextViewDefaultHomeScreenNr;
	TextView TextViewHomeScreensNr;
	
	//irrenhaus
	CheckBox CheckBoxCloseFolders;
	
	int MIN_SCREENS = 2;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        context = this;
        
        setContentView(R.layout.extended_homesettings);
    
        TextViewDefaultHomeScreenNr = (TextView)findViewById(R.id.TextViewDefaultHomeScreenNr);
        TextViewHomeScreensNr = (TextView)findViewById(R.id.TextViewHomeScreensNr);
        
        SeekBarHomeScreens = (SeekBar)findViewById(R.id.SeekBarHomeScreens);
        SeekBarHomeScreens.setProgress(com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Home_HomeScreens(context)-MIN_SCREENS);

        SeekBarDefaultHomeScreen = (SeekBar)findViewById(R.id.SeekBarDefaultHomeScreen);
        
        SeekBarDefaultHomeScreen.setMax(com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Home_HomeScreens(context)-1);
        SeekBarDefaultHomeScreen.setProgress(com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Home_DefaultScreen(context));

        TextViewDefaultHomeScreenNr.setText(String.valueOf(com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Home_DefaultScreen(context)+1));
        TextViewHomeScreensNr.setText(String.valueOf(com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Home_HomeScreens(context)));
        
        // irrenhaus
        CheckBoxCloseFolders = (CheckBox)findViewById(R.id.CheckBoxCloseFolders);
        CheckBoxCloseFolders.setChecked(com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Home_CloseFolders(context));
        
        SeekBarHomeScreens.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
				com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Set_Home_HomeScreens(context, progress + MIN_SCREENS);
				
				TextViewHomeScreensNr.setText(String.valueOf(progress+MIN_SCREENS));
				SeekBarDefaultHomeScreen.setMax(com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Home_HomeScreens(context)-1);
				
				if(com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Home_DefaultScreen(context) > (com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Home_HomeScreens(context)-1))
				{
					com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Set_Home_DefaultScreen(context, (com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Home_HomeScreens(context)-1));
					TextViewDefaultHomeScreenNr.setText(String.valueOf(com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Home_HomeScreens(context)));

				}
				
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}});
        
	    SeekBarDefaultHomeScreen.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
	    {
	
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
				com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Set_Home_DefaultScreen(context, progress);
				TextViewDefaultHomeScreenNr.setText(String.valueOf(progress+1));
			}
	
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
	
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}});
	    
	    // irrenhaus
	    CheckBoxCloseFolders.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Set_Home_CloseFolders(context, isChecked);
			}
	    	
	    });
    
    }    
}
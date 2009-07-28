package com.android.launcher;

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
        am.restartPackage("com.android.launcher");
		
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
        
        context = this.getApplicationContext();
        
        setContentView(R.layout.extended_homesettings);
    
        TextViewDefaultHomeScreenNr = (TextView)findViewById(R.id.TextViewDefaultHomeScreenNr);
        TextViewHomeScreensNr = (TextView)findViewById(R.id.TextViewHomeScreensNr);
        
        SeekBarHomeScreens = (SeekBar)findViewById(R.id.SeekBarHomeScreens);
        SeekBarHomeScreens.setProgress(com.android.launcher.extended.data.ExtendedSettings.Home_HomeScreens(context.getApplicationContext())-MIN_SCREENS);

        SeekBarDefaultHomeScreen = (SeekBar)findViewById(R.id.SeekBarDefaultHomeScreen);
        
        SeekBarDefaultHomeScreen.setMax(com.android.launcher.extended.data.ExtendedSettings.Home_HomeScreens(context.getApplicationContext())-1);
        SeekBarDefaultHomeScreen.setProgress(com.android.launcher.extended.data.ExtendedSettings.Home_DefaultScreen(context.getApplicationContext()));

        TextViewDefaultHomeScreenNr.setText(String.valueOf(com.android.launcher.extended.data.ExtendedSettings.Home_DefaultScreen(context.getApplicationContext())+1));
        TextViewHomeScreensNr.setText(String.valueOf(com.android.launcher.extended.data.ExtendedSettings.Home_HomeScreens(context.getApplicationContext())));
        
        // irrenhaus
        CheckBoxCloseFolders = (CheckBox)findViewById(R.id.CheckBoxCloseFolders);
        CheckBoxCloseFolders.setChecked(com.android.launcher.extended.data.ExtendedSettings.Home_CloseFolders(context.getApplicationContext()));
        
        SeekBarHomeScreens.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
				com.android.launcher.extended.data.ExtendedSettings.Set_Home_HomeScreens(context.getApplicationContext(), progress + MIN_SCREENS);
				
				TextViewHomeScreensNr.setText(String.valueOf(progress+MIN_SCREENS));
				SeekBarDefaultHomeScreen.setMax(com.android.launcher.extended.data.ExtendedSettings.Home_HomeScreens(context.getApplicationContext())-1);
				
				if(com.android.launcher.extended.data.ExtendedSettings.Home_DefaultScreen(context.getApplicationContext()) > (com.android.launcher.extended.data.ExtendedSettings.Home_HomeScreens(context.getApplicationContext())-1))
				{
					com.android.launcher.extended.data.ExtendedSettings.Set_Home_DefaultScreen(context.getApplicationContext(), (com.android.launcher.extended.data.ExtendedSettings.Home_HomeScreens(context.getApplicationContext())-1));
					TextViewDefaultHomeScreenNr.setText(String.valueOf(com.android.launcher.extended.data.ExtendedSettings.Home_HomeScreens(context.getApplicationContext())));

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
				
				com.android.launcher.extended.data.ExtendedSettings.Set_Home_DefaultScreen(context.getApplicationContext(), progress);
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
				com.android.launcher.extended.data.ExtendedSettings.Set_Home_CloseFolders(context.getApplicationContext(), isChecked);
			}
	    	
	    });
    
    }    
}
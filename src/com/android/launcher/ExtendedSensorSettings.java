package com.android.launcher;

import com.android.launcher.*;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ExtendedSensorSettings extends Activity {

	public static String TAG = "Launcher - Extended";
	Context context;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        context = this.getApplicationContext();        
        
        setContentView(R.layout.extended_sensorsettings);
    
        CheckBox CheckBoxSensorRotation = (CheckBox)findViewById(R.id.CheckBoxSensorRotation);
        
        if(com.android.launcher.extended.data.ExtendedSettings.Sensor_Enabled(context)==1)
        {
        	CheckBoxSensorRotation.setChecked(true);
        } else {
        	CheckBoxSensorRotation.setChecked(false);
        }
        
        CheckBoxSensorRotation.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if(isChecked)
				{
					com.android.launcher.extended.data.ExtendedSettings.Set_Sensor_Enabled(context, 1);
				} else {
					com.android.launcher.extended.data.ExtendedSettings.Set_Sensor_Enabled(context, 0);
				}

				
			}
        });
        
   
    }    
}
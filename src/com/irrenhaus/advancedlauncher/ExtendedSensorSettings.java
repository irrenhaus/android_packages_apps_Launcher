package com.irrenhaus.advancedlauncher;

import com.irrenhaus.advancedlauncher.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ExtendedSensorSettings extends Activity {

	public static String TAG = "Launcher - Extended";
	Context context;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        context = this.getApplicationContext();        
        
        setContentView(R.layout.extended_sensorsettings);
    
        CheckBox CheckBoxSensorRotation = (CheckBox)findViewById(R.id.CheckBoxSensorRotation);
        
        if(com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Sensor_Enabled(context)==1)
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
					com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Set_Sensor_Enabled(context, 1);
				} else {
					com.irrenhaus.advancedlauncher.extended.data.ExtendedSettings.Set_Sensor_Enabled(context, 0);
				}

				
			}
        });
        
   
    }    
}
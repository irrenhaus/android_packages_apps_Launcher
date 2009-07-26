package com.android.launcher;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.launcher.extended.display.MenuAdapter;
import com.android.launcher.extended.display.MenuItem;

public class ExtendedSettings extends Activity {

	public static String TAG = "Launcher - Extended";
	
	public ArrayList<MenuItem> MenuItems = new ArrayList<MenuItem>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.extended_settings);
        
        UpdateMenu();
        final MenuAdapter menuAdapter = new MenuAdapter(this, MenuItems);
        
        ListView ListViewMenu = (ListView)findViewById(R.id.ListViewExtendedMenu);
        ListViewMenu.setAdapter(menuAdapter);
        
        ListViewMenu.setOnItemClickListener(new OnItemClickListener()
        {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				MenuItem selected = (MenuItem)menuAdapter.getItem(arg2);

				if(selected.Tag=="Drawer")
				{
					final Intent extendeddrawersettings = new Intent(ExtendedSettings.this, ExtendedDrawerSettings.class);
					startActivity(extendeddrawersettings);
				}
				if(selected.Tag=="Home")
				{
					final Intent extendedhomesettings = new Intent(ExtendedSettings.this, ExtendedHomeSettings.class);
					startActivity(extendedhomesettings);
				}
			    
                if(selected.Tag=="Sensor")
                {
                    final Intent extendedsensorsettings = new Intent(ExtendedSettings.this, ExtendedSensorSettings.class);
                    startActivity(extendedsensorsettings);
                }
     
			}        	
        }
        );
        
    }
    
    void UpdateMenu()
    {
    	MenuItems.clear();

        MenuItem MenuDrawer = new MenuItem();
        MenuDrawer.Title = "Application drawer";
        MenuDrawer.Description = "Restore hidden application drawer shortcuts";
        MenuDrawer.Tag = "Drawer";
        MenuDrawer.Icon = android.R.drawable.ic_menu_more;
        
        MenuItems.add(MenuDrawer);
        
        MenuItem MenuHome = new MenuItem();
        MenuHome.Title = "Home screen";
        MenuHome.Description = "Setup the number and default of homescreens ( In development )";
        MenuHome.Tag = "Home";
        MenuHome.Icon = android.R.drawable.ic_menu_more;
        
        MenuItems.add(MenuHome);        
        
        MenuItem MenuSensor = new MenuItem();
        MenuSensor.Title = "Sensor based orientation";
        MenuSensor.Description = "Enable / disable sensor based orientation ( In development )";
        MenuSensor.Tag = "Sensor";
        MenuSensor.Icon = android.R.drawable.ic_menu_more;
        
        MenuItems.add(MenuSensor);        

        
    }

}

package com.android.launcher;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.launcher.SubMenuSettings.SubMenuDBHelper;

public class SubMenuAdapter extends ArrayAdapter<ApplicationInfo> {
	private final LayoutInflater mInflater;
	private final Context context;
	
	public SubMenuAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		mInflater = LayoutInflater.from(context);
		this.context = context;
	}
	
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ApplicationInfo info = getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.application_boxed, parent, false);
        }

        if (!info.filtered) {
            info.icon = Utilities.createIconThumbnail(info.icon, getContext());
            info.filtered = true;
        }

        final TextView textView = (TextView) convertView;
        textView.setCompoundDrawablesWithIntrinsicBounds(null, info.icon, null, null);
        textView.setText(info.title);

        return convertView;
    }
	
	public void generateAppsList(String title)
	{
		SubMenuDBHelper hlp = new SubMenuDBHelper(context);
		SQLiteDatabase db = hlp.getReadableDatabase();
		
		Cursor data = db.query("submenus_entries", new String[] {"_id", "name", "intent", "submenu"}, "submenu = '"+title+"'", null, "Upper(name)", null, null);
		
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		PackageManager manager = context.getPackageManager();
        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
		
		while(data.moveToNext())
		{
	        for(int i = 0; i < apps.size(); i++)
	        {
	        	ResolveInfo info = apps.get(i);
	        	
	        	String apptitle = info.loadLabel(manager).toString();
	        	if (apptitle == null) {
	                apptitle = info.activityInfo.name;
	            }
	        	
	        	if(!apptitle.equals(data.getString(data.getColumnIndex("name"))))
	        		continue;
	        	
	        	ComponentName componentName = new ComponentName(
	                    info.activityInfo.applicationInfo.packageName,
	                    info.activityInfo.name);
	        	
	        	ApplicationInfo application = new ApplicationInfo();
	            application.container = ItemInfo.NO_ID;

	            updateApplicationInfoTitleAndIcon(manager, info, application, context);

	            application.setActivity(componentName,
	                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	        	
	            if(application.title.equals(data.getString(data.getColumnIndex("name"))))
	            {
	            	this.add(application);
	            	break;
	            }
	        }
		}
		
		data.close();
		db.close();
	}
	
	private static void updateApplicationInfoTitleAndIcon(PackageManager manager, ResolveInfo info,
            ApplicationInfo application, Context context) {

        application.title = info.loadLabel(manager);
        if (application.title == null) {
            application.title = info.activityInfo.name;
        }

        application.icon =
                Utilities.createIconThumbnail(info.activityInfo.loadIcon(manager), context);
        application.filtered = false;
    }

}

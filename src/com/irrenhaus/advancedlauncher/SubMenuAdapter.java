package com.irrenhaus.advancedlauncher;

import java.net.URISyntaxException;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.launcher.R;
import com.irrenhaus.advancedlauncher.SubMenuSettings.SubMenuDBHelper;

public class SubMenuAdapter extends ArrayAdapter<ApplicationInfo> {
	private final LayoutInflater mInflater;
	private final Context context;
	
	private static class ApplicationInfoComparator implements Comparator<ApplicationInfo> {
        public final int compare(ApplicationInfo a, ApplicationInfo b) {
        	if(a.isSubMenu && ! b.isSubMenu)
        		return -1;
        	if(!a.isSubMenu && b.isSubMenu)
        		return 1;
        	
            return Collator.getInstance().compare(a.title.toString(), b.title.toString());
        }
    }
	
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
		SubMenuDBHelper hlp = new SubMenuDBHelper(context, false);
		SQLiteDatabase db = hlp.getReadableDatabase();
		
		Cursor data = db.query("submenus_entries", new String[] {"_id", "name", "intent", "submenu"}, "submenu = '"+title+"'", null, null, null, null);

		PackageManager manager = context.getPackageManager();
		
		while(data.moveToNext())
		{
			Intent intent;
			try {
				intent = Intent.getIntent(data.getString(2));
				if(intent.resolveActivity(manager) == null)
				{
					continue;
				}
			} catch (URISyntaxException e) {
				Log.d("SubMenuAdapter", "Could not get intent from uri!");
				continue;
			}
			
	        final List<ResolveInfo> apps = manager.queryIntentActivities(intent, 0);
			
	        if(apps.size() <= 0)
	        	continue;
	        
	        ResolveInfo info = apps.get(0);
	        	
	        	String apptitle = info.loadLabel(manager).toString();
	        	if (apptitle == null) {
	                apptitle = info.activityInfo.name;
	            }
	        	
	        	ComponentName componentName = new ComponentName(
	                    info.activityInfo.applicationInfo.packageName,
	                    info.activityInfo.name);
	        	
	        	ApplicationInfo application = new ApplicationInfo();
	            application.container = ItemInfo.NO_ID;

	            updateApplicationInfoTitleAndIcon(manager, info, application, context);

	            application.setActivity(componentName,
	                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	        	
	            this.add(application);
		}
		
		this.sort(new ApplicationInfoComparator());
		
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

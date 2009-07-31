package com.android.launcher;

import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SubMenuSettings extends ListActivity {
	
	LayoutInflater mInflater;
	Cursor mCursor;
	SQLiteDatabase mDatabase;
	ApplicationsAdapter mAdapter;
	ListView mListView;
	private static Cursor mCursorSubMenus;
	public static Launcher activeLauncher;

	public static final int mnuMoveItem = Menu.FIRST + 1;
	
	private ProgressDialog dlg;
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add("Manage submenus");
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getTitle().equals("Manage submenus"))
		{
			Intent intent = new Intent(this, SubMenuManageMenusSettings.class);
			startActivity(intent);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override 
	public boolean onContextItemSelected(MenuItem item) 
    {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int itemId = Integer.valueOf(String.valueOf(info.position));

		mCursor.moveToPosition(itemId);
		int ApplicationId = mCursor.getInt(0);
		String name = mCursor.getString(mCursor.getColumnIndex("name"));
		
		refreshMenuList(mDatabase);
		
		int i = 1;
		
		while(mCursorSubMenus.moveToNext())
		{
			if(item.getItemId() == mnuMoveItem)
			{
				MoveApplication("MainMenu", name, null, false);
				break;
			}
			else if(item.getItemId() == mnuMoveItem+i)
			{
				MoveApplication(mCursorSubMenus.getString(mCursorSubMenus.getColumnIndex("name")), name, null, false);
				break;
			}
			i++;
		}
		
    	return super.onContextItemSelected(item);
    }
	
	protected static class SubMenuDBHelper extends SQLiteOpenHelper {
		private Context context;
		
		public SubMenuDBHelper(Context context) {
			super(context, "submenu.sqlite", null, 2);
			
			this.context = context;
			
			Log.d("SubMenuDBHelper", "Created");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String ddlScripts = "create table submenus_entries (_id integer primary key, name text, intent text, submenu text);";
			db.execSQL(ddlScripts);
			
			ddlScripts = "create table submenus (_id integer primary key, name text);";
			db.execSQL(ddlScripts);
			
			ContentValues values = new ContentValues();
			values.put("name", "MainMenu");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Cursor data = db.query("submenus_entries", new String[] {"_id", "name", "intent", "submenu"}, null, null, "Upper(name)", null, null);
			
			final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			PackageManager manager = context.getPackageManager();
	        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
			
	        Log.d("SubMenuSettings", "Upgrading db to version 2...");
	        
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
		            	int submenu = data.getColumnIndex("submenu");
		            	int name = data.getColumnIndex("name");
		            	
		            	ContentValues values = new ContentValues();
		    			values.put("name", data.getString(name));
		    			values.put("intent", application.intent.toURI());
		    			values.put("submenu", data.getString(submenu));
		    			
		    			if(db.update("submenus_entries", values, "name = '"+data.getString(name)+"'", null) <= 0)
		    			{
		    				Log.d("MoveApplication", "update <= 0");
		    			}
		    			else
		    				Log.d("MoveApplication", "App "+data.getString(name)+" updated");
		            	
		            	break;
		            }
		        }
			}
			
			Log.d("SubMenuSettings", "Upgrading successfull");
			
			data.close();
		}
		
		@Override
		public void onOpen(SQLiteDatabase db) {
			Log.d("SubMenuDBHelper", "onOpen()");
		}
	}

	public class ApplicationsAdapter extends CursorAdapter {

		public ApplicationsAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			TextView ApplicationNameText = (TextView) view.findViewById(R.id.txtTitle);
			String applicationName = cursor.getString(1)+": "+cursor.getString(3);

			ApplicationNameText.setText(applicationName);

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.extended_drawersettings_row, null);
			bindView(view, context, cursor);
			return view;
		}
	}

	private int mScrollX;
	private int mScrollY;
	
	private void refreshCursor() {
		mScrollX = 0;
		mScrollY = 0;
		
		try {
			if(mCursor != null)
				mCursor.close();
			mCursor = mDatabase.query(false, "submenus_entries", new String[] { "_id", "name", "intent", "submenu" }, null, null, null, null, "Upper(name)", null);
			mAdapter = new ApplicationsAdapter(this, mCursor);

			mScrollX = getListView().getScrollX();
			mScrollY = getListView().getScrollY();
			setListAdapter(mAdapter);
			
			getListView().setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
				public void onChildViewAdded(View parent, View child) {
					getListView().scrollTo(mScrollX, mScrollY);
				}
				
				public void onChildViewRemoved(View parent, View child) {
				}
			});
		} catch(SQLiteException e) {
			Log.d("SubMenuSettings", "Error: "+e.getMessage());
			
		}
	}

	@Override
	protected void onResume()
	{
		refreshCursor();
		super.onResume();
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
                
        setContentView(R.layout.submenu_settings);
        
        mListView = (ListView) findViewById(android.R.id.list);
        mInflater = getLayoutInflater();
        
        SubMenuDBHelper hlp = new SubMenuDBHelper(this); 
        mDatabase = hlp.getWritableDatabase();

    	Log.d("SubMenuSettings", "Loaded db "+mDatabase.getPath());

		InsertAllApps();

        refreshCursor();
        
        refreshMenuList(mDatabase);

        Log.d("SubMenuSettings", "Count: "+mCursorSubMenus.getCount());
        
        mListView.setOnCreateContextMenuListener(
        		new OnCreateContextMenuListener()
        		{
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						
						refreshMenuList(mDatabase);

						menu.add(0, mnuMoveItem, 0, "Add to main menu");

						int i = 1;
						while(mCursorSubMenus.moveToNext())
						{
							menu.add(0, mnuMoveItem+i, 0, "Add to submenu "+mCursorSubMenus.getString(mCursorSubMenus.getColumnIndex("name")));
							i++;
						}
					}
        		}
        );
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		mCursorSubMenus.close();
		mDatabase.close();
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		
        final LauncherModel model = Launcher.getModel();

        model.loadApplications(false, activeLauncher, false);
	}
	
	public static void refreshMenuList(SQLiteDatabase db)
	{
		if(mCursorSubMenus != null)
			mCursorSubMenus.close();
		if(db != null)
			mCursorSubMenus = db.query(false, "submenus", new String[] { "_id", "name" }, null, null, null, null, "Upper(name)", null);
	}
		
	void MoveApplication(String menu, String name, String intent, boolean insert)
	{
		Cursor fix = mDatabase.query(false, "submenus_entries", new String[] { "_id", "name", "intent", "submenu" }, "name = '"+name+"'", null, null, null, null, null);
			
		try
		{
			fix.moveToFirst();
			int field = fix.getColumnIndex("intent");
			if(fix.getString(field) == null || fix.getString(field).equals("null") || fix.getString(field).equals(""))
			{
				menu = fix.getString(3);
				insert = false;
			}
		} catch (Exception e)
		{
			Log.d("SubMenuSettings", "Error at "+name);
		}
		
		fix.close();
		
		if(insert)
		{
			Cursor tmp = mDatabase.query(false, "submenus_entries", new String[] { "_id", "name", "intent" }, "intent = '"+intent+"'", null, null, null, null, null);
	        
			while(tmp.moveToNext())
			{
				tmp.close();
				return;
			}
			
			tmp.close();
		}
		
		Log.d("MoveApplication", "Starting move of "+name);
		
		try {
			ContentValues values = new ContentValues();
			
			if(name != null)
				values.put("name", name);
			if(intent != null)
				values.put("intent", intent);
			if(menu != null)
				values.put("submenu", menu);
			
			if(insert)
			{
				mDatabase.insert("submenus_entries", null, values);
			}
			else if(mDatabase.update("submenus_entries", values, "name = '"+name+"'", null) <= 0)
			{
				Log.d("MoveApplication", "update <= 0");
			}
			else
				Log.d("MoveApplication", "Submenu of app "+name+" updated");
			
			refreshCursor();
		}catch(SQLiteException e) {
			Log.d("SubMenuSettings", "Error: "+e.getMessage());
		}
		
	}
	
	void AddMenu(String title)
	{
		ContentValues values = new ContentValues();
		values.put("name", title);
		
		mDatabase.insert("submenus", null, values);
		
		refreshCursor();
	}
	
	void InsertAllApps()
	{
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		PackageManager manager = this.getPackageManager();
        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        
        for(int i = 0; i < apps.size(); i++)
        {
        	ResolveInfo info = apps.get(i);
        	
        	ComponentName componentName = new ComponentName(
                    info.activityInfo.applicationInfo.packageName,
                    info.activityInfo.name);
        	
        	ApplicationInfo application = new ApplicationInfo();
            application.container = ItemInfo.NO_ID;

            updateApplicationInfoTitleAndIcon(manager, info, application, this);

            application.setActivity(componentName,
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            
        	MoveApplication("MainMenu", application.title.toString(), application.intent.toURI(), true);
        }
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
package com.android.launcher;

import java.util.List;

import android.app.ListActivity;
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
	Cursor mCursorSubMenus;
	public static Launcher activeLauncher;

	public static final int mnuMoveItem = Menu.FIRST + 1;
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add("Add submenu");
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getTitle().equals("Add submenu"))
		{
			AddMenu("test");
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
		
		if(mCursorSubMenus != null)
			mCursorSubMenus.close();
		mCursorSubMenus = mDatabase.query(false, "submenus", new String[] { "_id", "name" }, null, null, null, null, null, null);
        
		int i = 1;
		
		while(mCursorSubMenus.moveToNext())
		{
			if(item.getItemId() == mnuMoveItem+i)
			{
				MoveApplication(ApplicationId, mCursorSubMenus.getString(mCursorSubMenus.getColumnIndex("name")), name, null, false);
				break;
			}
			i++;
		}
		
		refreshCursor();
		
    	return super.onContextItemSelected(item);
    }
	
	protected static class SubMenuDBHelper extends SQLiteOpenHelper {
		public SubMenuDBHelper(Context context) {
			super(context, "submenu.sqlite", null, 1);
			
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

	private void refreshCursor() {
		try {
			if(mCursor != null)
				mCursor.close();
			mCursor = mDatabase.query(false, "submenus_entries", new String[] { "_id", "name", "intent", "submenu" }, null, null, null, null, null, null);
			mAdapter = new ApplicationsAdapter(this, mCursor);
			setListAdapter(mAdapter);
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
        
        mCursorSubMenus = mDatabase.query(false, "submenus", new String[] { "_id", "name" }, null, null, null, null, null, null);
        
        Log.d("SubMenuSettings", "Count: "+mCursorSubMenus.getCount());
        
        mListView.setOnCreateContextMenuListener(
        		new OnCreateContextMenuListener()
        		{
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {

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
		
	void MoveApplication(int ApplicationId, String menu, String name, String intent, boolean insert)
	{
		if(insert)
		{
			Cursor tmp = mDatabase.query(false, "submenus_entries", new String[] { "_id", "name", "intent" }, null, null, null, null, null, null);
	        
			while(tmp.moveToNext())
			{
				String n = tmp.getString(1);
				String i = tmp.getString(2);
				if((n != null && n.equals(name)) || (i != null && i.equals(intent)))
				{
					tmp.close();
					return;
				}
			}
			
			tmp.close();
		}
		
		Log.d("MoveApplication", "Starting move of "+name);
		
		try {
			ContentValues values = new ContentValues();
			values.put("name", name);
			values.put("intent", intent);
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
			
	        final LauncherModel model = Launcher.getModel();
	
	        model.dropApplications();
	        model.loadApplications(false, activeLauncher, false);
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
		
        final LauncherModel model = Launcher.getModel();

        model.dropApplications();
        model.loadApplications(false, activeLauncher, false);
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
            
        	MoveApplication(i, "MainMenu", application.title.toString(), application.intent.toString(), true);
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
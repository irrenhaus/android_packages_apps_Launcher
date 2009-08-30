package com.irrenhaus.advancedlauncher;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import android.database.DatabaseUtils;
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

import com.android.launcher.R;
import com.irrenhaus.advancedlauncher.ExtendedDrawerSettings.ExtendedDrawerDBHelper;

public class SubMenuSettings extends ListActivity {
	
	LayoutInflater mInflater;
	Cursor mCursor;
	SQLiteDatabase mDatabase;
	ApplicationsAdapter mAdapter;
	ListView mListView;
	private static Cursor mCursorSubMenus;
	public static AdvancedLauncher activeLauncher;

	public static final int mnuMoveItem = Menu.FIRST + 1;
	
	private static ProgressDialog dlg;
		
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
		String intent = mCursor.getString(mCursor.getColumnIndex("intent"));
		
		refreshMenuList(mDatabase);
		
		int i = 1;
		
		while(mCursorSubMenus.moveToNext())
		{
			if(item.getItemId() == mnuMoveItem)
			{
				MoveApplication("MainMenu", name, intent, false);
				break;
			}
			else if(item.getItemId() == mnuMoveItem+i)
			{
				MoveApplication(mCursorSubMenus.getString(mCursorSubMenus.getColumnIndex("name")), name, intent, false);
				break;
			}
			i++;
		}
		
    	return super.onContextItemSelected(item);
    }
	
	protected static class SubMenuDBHelper extends SQLiteOpenHelper {
		private Context context;
		
		public SubMenuDBHelper(Context context, boolean check) {
			super(context, "submenu.sqlite", null, 2);
			
			this.context = context;
			
	        if(check)
	        {
				SQLiteDatabase db = this.getWritableDatabase();
				
				Cursor data = db.query("submenus_entries", new String[] {"_id", "name", "intent", "submenu"}, null, null, "Upper(name)", null, null);
				
				final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				PackageManager manager = context.getPackageManager();
		        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
			
		        Log.d("SubMenuSettings", "Checking db...");
		        
				while(data.moveToNext())
				{
					boolean isInstalled = false;
					
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
			            	isInstalled = true;
			            	
			            	break;
			            }
			        }
			        
			        if(!isInstalled)
			        {
			        	db.delete("submenus_entries", "intent='"+data.getString(data.getColumnIndex("intent"))+"'", null);
			        }
				}
				
				Log.d("SubMenuSettings", "DB check successfull");
				
				data.close();
				db.close();
	        }
			
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
			String applicationName = cursor.getString(1)+"\n"+cursor.getString(3);

			PackageManager manager = SubMenuSettings.this.getPackageManager();
	        List<ResolveInfo> apps = null;
			try {
				apps = manager.queryIntentActivities(Intent.getIntent(cursor.getString(cursor.getColumnIndex("intent"))), 0);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        	ApplicationInfo application = new ApplicationInfo();
        	
	        for(int i = 0; i < apps.size(); i++)
	        {
	        	ResolveInfo info = apps.get(i);
	        	
	        	ComponentName componentName = new ComponentName(
	                    info.activityInfo.applicationInfo.packageName,
	                    info.activityInfo.name);
	        	
	        	application = new ApplicationInfo();
	            application.container = ItemInfo.NO_ID;

	            SubMenuSettings.updateApplicationInfoTitleAndIcon(manager, info, application, SubMenuSettings.this);
	        }
			
			ApplicationNameText.setText(applicationName);
			ApplicationNameText.setCompoundDrawablesWithIntrinsicBounds(application.icon, null, null, null);
			ApplicationNameText.setTextSize(14);
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
		dlg.setTitle("Refreshing database cursor...");
		dlg.setMessage("Please wait...");
		
		mScrollX = 0;
		mScrollY = 0;
		
		try {
			if(mCursor != null)
				mCursor.close();
			mCursor = mDatabase.query(false, "submenus_entries", new String[] { "_id", "name", "intent", "submenu" }, null, null, null, null, "Upper(submenu)", null);
			
			if(mAdapter == null)
			{
				mAdapter = new ApplicationsAdapter(this, mCursor);
				setListAdapter(mAdapter);
			}
			else
				((ApplicationsAdapter)this.getListAdapter()).changeCursor(mCursor);
			
		} catch(SQLiteException e) {
			Log.d("SubMenuSettings", "Error: "+e.getMessage());
			
		}
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
                
        setContentView(R.layout.submenu_settings);
        
        mListView = (ListView) findViewById(android.R.id.list);
        mInflater = getLayoutInflater();
        
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
	public void onResume()
	{
		super.onResume();
		
		dlg = ProgressDialog.show(this, "Loading apps data", "");
		
		SubMenuDBHelper hlp = new SubMenuDBHelper(this, true); 
        mDatabase = hlp.getWritableDatabase();

    	Log.d("SubMenuSettings", "Loaded db "+mDatabase.getPath());

		InsertAllApps();

        refreshCursor();
        
        refreshMenuList(mDatabase);
        
        dlg.cancel();
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
		
        final LauncherModel model = AdvancedLauncher.getModel();

        model.loadApplications(false, activeLauncher, false);
	}
	
	public static void refreshMenuList(SQLiteDatabase db)
	{
		dlg.setTitle("Refreshing menu list...");
		dlg.setMessage("Please wait...");
		
		if(mCursorSubMenus != null)
			mCursorSubMenus.close();
		if(db != null)
			mCursorSubMenus = db.query(false, "submenus", new String[] { "_id", "name" }, null, null, null, null, "Upper(name)", null);
	}
		
	void MoveApplication(String menu, String name, String intent, boolean insert)
	{
		SubMenuSettings.MoveApplication(mDatabase, menu, name, intent, insert);

		refreshCursor();
	}
	
	public static void MoveApplication(SQLiteDatabase db, String menu, String name, String intent, boolean insert)
	{
		if(insert)
		{
			Cursor tmp = db.query(false, "submenus_entries", new String[] { "_id", "name", "intent" }, "intent = '"+intent+"'", null, null, null, null, null);
	        
			while(tmp.moveToNext())
			{
				tmp.close();
				return;
			}
			
			tmp.close();
		}
		else
		{
			Cursor tmp = db.query(false, "submenus_entries", new String[] { "_id", "name", "intent" }, "intent = '"+intent+"'", null, null, null, null, null);
	        
			if(tmp.getCount() <= 0)
				insert = true;
			
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
				db.insert("submenus_entries", null, values);
			}
			else
			{
				if(intent != null)
				{
					if(db.update("submenus_entries", values, "intent = '"+intent+"' AND name = "+DatabaseUtils.sqlEscapeString(name), null) <= 0)
					{
						Log.d("MoveApplication", "update <= 0");
					}
					else
						Log.d("MoveApplication", "Submenu of app "+name+" updated");
				}
				else if(name != null)
				{
					if(db.update("submenus_entries", values, "name = '"+name+"'", null) <= 0)
					{
						Log.d("MoveApplication", "update <= 0");
					}
					else
						Log.d("MoveApplication", "Submenu of app "+name+" updated");
				}
			}
			
		}catch(SQLiteException e) {
			Log.d("SubMenuSettings", "Error: "+e.getMessage());
		}
		
	}
	
	void AddMenu(String title)
	{
		AddMenu(mDatabase, title);
		
		refreshCursor();
	}
	
	void InsertAllApps()
	{
		dlg.setTitle("Checking for unknown apps...");
		dlg.setMessage("Please wait...");
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
            
    		dlg.setMessage(application.title.toString());
            
        	MoveApplication("MainMenu", application.title.toString(), application.intent.toURI(), true);
        }
	}
	
	public static ArrayList<ApplicationInfo> getSubmenuContents(Context context, String submenu)
	{
		ArrayList<ApplicationInfo> ret = new ArrayList<ApplicationInfo>();
		SubMenuDBHelper hlp = new SubMenuDBHelper(context, false);
		SQLiteDatabase db = hlp.getReadableDatabase();
		
		Cursor data = db.query("submenus_entries", new String[] {"_id", "name", "intent", "submenu"}, "submenu = '"+submenu+"'", null, null, null, null);

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
	        	
	            ret.add(application);
		}
		
		data.close();
		db.close();
		
		return ret;
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
	
	public static void AddMenu(SQLiteDatabase db, String title)
	{
		ContentValues values = new ContentValues();
		values.put("name", title);
		
		db.insert("submenus", null, values);
	}
	
	public static void RenameMenu(SQLiteDatabase db, String which, String title)
	{
		ContentValues content_values = new ContentValues();
		content_values.put("submenu", title);
		
		ContentValues menu_values = new ContentValues();
		menu_values.put("name", title);
		
		db.update("submenus_entries", content_values, "submenu = '"+which+"'", null);
		
		db.update("submenus", menu_values, "name = '"+which+"'", null);
	}
	
	public static void DeleteMenu(SQLiteDatabase db, String title)
	{
		ContentValues values = new ContentValues();
		values.put("submenu", "MainMenu");
		
		db.update("submenus_entries", values, "submenu = '"+title+"'", null);
		db.delete("submenus", "name = '"+title+"'", null);
	}
	
	private static FileWriter openSDCardFileW(String fileName)
	{
		try {
			File root = new File("sdcard/" + "AdvancedLauncher");
		    
			if(root.exists() || root.mkdir())
		    {
		        File file = new File(root, fileName);
		        
		        if(!file.exists())
		        	file.createNewFile();
		        
		        FileWriter writer;
				writer = new FileWriter(file);
				
		        return writer;
		    }
		} catch (IOException e) {
			Log.d("BackupCreation", e.getMessage());
			return null;
		}
	    
	    return null;
	}
	
	private static FileReader openSDCardFileR(String fileName)
	{
		try {
			File root = new File("sdcard/" + "AdvancedLauncher");
		    
			if(root.exists() || root.mkdir())
		    {
		        File file = new File(root, fileName);
		        
		        if(!file.exists())
		        	file.createNewFile();
		        
		        FileReader reader;
				reader = new FileReader(file);
				
		        return reader;
		    }
		} catch (IOException e) {
			Log.d("BackupCreation", e.getMessage());
			return null;
		}
	    
	    return null;
	}
	
	public static boolean createConfigBackup(Context context)
	{
		String dbPath = "/data/data/com.irrenhaus.advancedlauncher/databases/";
		String prefPath = "/data/data/com.irrenhaus.advancedlauncher/shared_prefs/";
		String outPath = "/sdcard/AdvancedLauncher/";

		String submenuFile = dbPath+"/submenu.sqlite";
		String extendedFile = dbPath+"/extendeddrawer.sqlite";
		String prefsFile = prefPath+"/extendedlauncher.xml";
		
		String submenuOutFile = "submenu.sql";
		String extendedOutFile = "extendeddrawer.sql";
		String prefsOutFile = "extendedlauncher.xml";
		
		CommandHandler bkupSubmenu = new CommandHandler(true, "sqlite3 "+submenuFile+" .dump");
		CommandHandler bkupExtended = new CommandHandler(true, "sqlite3 "+extendedFile+" .dump");
		CommandHandler bkupPrefs = new CommandHandler(true, "cp "+prefsFile+" "+outPath+prefsOutFile);

		bkupSubmenu.start();
		bkupExtended.start();
		bkupPrefs.start();
		try {
			bkupSubmenu.join();
			bkupExtended.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			return false;
		}
		
		try {
			FileWriter fw = openSDCardFileW(submenuOutFile);

			if(fw == null)
				return false;
			
			ArrayList<String> lines = bkupSubmenu.getStdOutLines();
			
			if(lines.size() > 0)
			{
				for(String line: lines)
				{
					fw.write(line);
					fw.write("\n");
				}
			}
			
			fw.flush();
			fw.close();
			
			fw = openSDCardFileW(extendedOutFile);

			if(fw == null)
				return false;
			
			lines = bkupExtended.getStdOutLines();
			
			if(lines.size() > 0)
			{
				for(String line: lines)
				{
					fw.write(line);
					fw.write("\n");
				}
			}
			
			fw.flush();
			fw.close();
		} catch (FileNotFoundException e) {
			Log.d("BackupCreation", e.getMessage());
			return false;
		} catch (IOException e) {
			Log.d("BackupCreation", e.getMessage());
			return false;
			
		}
		
		return true;
	}
	
	public static boolean restoreConfigBackup(Context context)
	{
		String dbPath = "/data/data/com.irrenhaus.advancedlauncher/databases/";
		String prefPath = "/data/data/com.irrenhaus.advancedlauncher/shared_prefs/";
		String outPath = "/sdcard/AdvancedLauncher/";

		String submenuFile = dbPath+"/submenu.sqlite";
		String extendedFile = dbPath+"/extendeddrawer.sqlite";
		String prefsFile = prefPath+"/extendedlauncher.xml";
		
		String submenuOutFile = "submenu.sql";
		String extendedOutFile = "extendeddrawer.sql";
		String prefsOutFile = "extendedlauncher.xml";
		
		CommandHandler bkupPrefs = new CommandHandler(true, "cp "+outPath+prefsOutFile+" "+prefsFile);

		bkupPrefs.start();

		CommandHandler rmSubmenuDB = new CommandHandler(true, "rm "+submenuFile);
		CommandHandler rmExtendedDB = new CommandHandler(true, "rm "+extendedFile);
		
		CommandHandler readSubmenuDB = new CommandHandler(true, "sqlite3 "+submenuFile+" \".read "+outPath+submenuOutFile+"\"");
		CommandHandler readExtendedDB = new CommandHandler(true, "sqlite3 "+extendedFile+" \".read "+outPath+extendedOutFile+"\"");
		
		rmSubmenuDB.start();
		rmExtendedDB.start();

		try {
			rmSubmenuDB.join();
			rmExtendedDB.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		
		SubMenuDBHelper hlp = new SubMenuDBHelper(context, false);
		hlp.getWritableDatabase();
		hlp.close();
		ExtendedDrawerDBHelper extendedHlp = new ExtendedDrawerDBHelper(context);
		extendedHlp.getWritableDatabase();
		extendedHlp.close();

		readSubmenuDB.start();
		readExtendedDB.start();
		
		return true;
	}
	
	//From wifi teether for root users
	
	static class CommandHandler extends Thread {
        
        public static final String MSG_TAG = "SubMenu -> ExecuteProcess";
        
        private Process process = null;
        private String command;
        private Runtime runtime;
        private ArrayList<String> stdOutLines;
        private int exitCode = -1;
        private boolean runAsRoot = false;

        CommandHandler(String command) {
                this.command = command;
                this.runAsRoot = false;
                this.runtime = Runtime.getRuntime();
        }

        CommandHandler(boolean runAsRoot, String command) {
                this.command = command;
                this.runAsRoot = runAsRoot;
                this.runtime = Runtime.getRuntime();
        }
        
        public int getExitCode() {
                return this.exitCode;
        }
        
        public ArrayList<String> getStdOutLines() {
                return this.stdOutLines;
        }
        
        public void destroy() {
                try {
                        if (this.process != null) {
                                this.process.destroy();
                        }
                        this.interrupt();
                }
                catch (Exception ex) {
                        // nothing
                }
        }
        
        public void run() {
                DataOutputStream os = null;
        InputStream stderr = null;
        InputStream stdout = null;
        String line;
        
        this.stdOutLines = new ArrayList<String>();
        Log.d(MSG_TAG, "Executing command (root:"+this.runAsRoot+"): " + command);
        try {
                this.runtime.gc();
                if (this.runAsRoot) {
                        this.process = this.runtime.exec("su");
                }
                else {
                        this.process = this.runtime.exec(command);
                }
                stderr = this.process.getErrorStream();
                stdout = this.process.getInputStream();
                BufferedReader errBr = new BufferedReader(new InputStreamReader(stderr), 8192);
                BufferedReader inputBr = new BufferedReader(new InputStreamReader(stdout), 8192);
                if (this.runAsRoot) {
                        os = new DataOutputStream(process.getOutputStream());
                        os.writeBytes(command+"\n");
                        os.flush();
                        os.writeBytes("exit\n");
                        os.flush();
                }
                while ((line = inputBr.readLine()) != null) {
                        stdOutLines.add(line.trim());
                        Log.d(MSG_TAG, "STDOUT: "+line.trim());
                }
                while ((line = errBr.readLine()) != null);
                this.exitCode = this.process.waitFor();
        } catch (Exception e) {
                Log.d(MSG_TAG, "Unexpected error - Here is what I know: "+e.getMessage());
        }
        finally {
                // Closing streams
                        try {
                                if (os != null)
                                        os.close();
                                if (stderr != null)
                                        stderr.close();
                                if (stdout != null)
                                        stdout.close();
                        } catch (Exception ex) {;}
                        // Destroy process
                        try {
                                this.process.destroy();
                        } catch (Exception e) {;}
        }
        }
	}

}
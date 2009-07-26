package com.android.launcher;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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

import com.android.launcher.SubMenuSettings.SubMenuDBHelper;

public class SubMenuManageMenusSettings extends ListActivity {
	private SQLiteDatabase		mDatabase;
	private Cursor		  		mCursor;
	private ListView			mListView;
	private LayoutInflater		mInflater;
	
	private final static int	mFirstEntry = Menu.FIRST+1;
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		if(item.getItemId() == mFirstEntry) //add menu
		{
			Intent intent = new Intent(this, SubMenuAddMenu.class);
			this.startActivityForResult(intent, 0);
		}
		else if(item.getItemId() == mFirstEntry+1) //delete menu
		{
			
		}
		
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		AddMenu(data.getStringExtra("com.android.launcher.AddSubMenu"));
		refreshCursor();
	}
	
	void AddMenu(String title)
	{
		ContentValues values = new ContentValues();
		values.put("name", title);
		
		mDatabase.insert("submenus", null, values);
		
		refreshCursor();
		
        final LauncherModel model = Launcher.getModel();

        model.dropApplications();
        model.loadApplications(false, SubMenuSettings.activeLauncher, false);
	}
	
	private void refreshCursor() {
		try {
			if(mCursor != null)
				mCursor.close();
			mCursor = mDatabase.query(false, "submenus", new String[] { "_id", "name" }, null, null, null, null, null, null);
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

    	Log.d("SubMenuManageSettings", "Loaded db "+mDatabase.getPath());

        refreshCursor();
        
        mListView.setOnCreateContextMenuListener(
        		new OnCreateContextMenuListener()
        		{
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {

						menu.add(0, mFirstEntry, 0, "Add new menu");
						//menu.add(0, mFirstEntry+1, 0, "Delete menu");
					}
        		}
        );
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		mCursor.close();
		mDatabase.close();
	}
	
	public class ApplicationsAdapter extends CursorAdapter {

		public ApplicationsAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			TextView menuNameText = (TextView) view.findViewById(R.id.txtTitle);
			String menuName = cursor.getString(1);

			menuNameText.setText(menuName);

		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.extended_drawersettings_row, null);
			bindView(view, context, cursor);
			return view;
		}
	}
}

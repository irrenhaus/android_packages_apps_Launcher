package com.irrenhaus.advancedlauncher;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.android.launcher.R;
import com.irrenhaus.advancedlauncher.SubMenuSettings.SubMenuDBHelper;

public class SubMenuManageMenusSettings extends ListActivity {
	private SQLiteDatabase		mDatabase;
	private Cursor		  		mCursor;
	private ListView			mListView;
	private LayoutInflater		mInflater;
	
	private final static int	mFirstEntry = Menu.FIRST+1;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add("Add menu");
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getTitle().equals("Add menu"))
		{
			Intent intent = new Intent(this, SubMenuAddMenu.class);
			this.startActivityForResult(intent, 0);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode != 0 || data == null)
			return;
		
		if(data.getStringExtra("com.irrenhaus.advancedlauncher.AddSubMenu") != null)
		{
			AddMenu(data.getStringExtra("com.irrenhaus.advancedlauncher.AddSubMenu"));
		}
		else if(data.getStringExtra("com.irrenhaus.advancedlauncher.RenameSubMenuOrig") != null &&
				data.getStringExtra("com.irrenhaus.advancedlauncher.RenameSubMenuNew") != null)
		{
			RenameMenu(data.getStringExtra("com.irrenhaus.advancedlauncher.RenameSubMenuOrig"), data.getStringExtra("com.irrenhaus.advancedlauncher.RenameSubMenuNew"));
		}
		
		refreshCursor();
		SubMenuSettings.refreshMenuList(mDatabase);
	}
	
	void AddMenu(String title)
	{
		ContentValues values = new ContentValues();
		values.put("name", title);
		
		mDatabase.insert("submenus", null, values);
		
		refreshCursor();
	}
	
	void RenameMenu(String which, String title)
	{
		ContentValues content_values = new ContentValues();
		content_values.put("submenu", title);
		
		ContentValues menu_values = new ContentValues();
		menu_values.put("name", title);
		
		mDatabase.update("submenus_entries", content_values, "submenu = '"+which+"'", null);
		
		mDatabase.update("submenus", menu_values, "name = '"+which+"'", null);
		
		refreshCursor();
	}
	
	void DeleteMenu(String title)
	{
		ContentValues values = new ContentValues();
		values.put("submenu", "MainMenu");
		
		mDatabase.update("submenus_entries", values, "submenu = '"+title+"'", null);
		mDatabase.delete("submenus", "name = '"+title+"'", null);
		
		refreshCursor();
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
        
        SubMenuDBHelper hlp = new SubMenuDBHelper(this, false); 
        mDatabase = hlp.getWritableDatabase();

    	Log.d("SubMenuManageSettings", "Loaded db "+mDatabase.getPath());

        refreshCursor();
        
        mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mCursor.moveToPosition(position);
				final String name = mCursor.getString(1);
				
				Intent intent = new Intent(SubMenuManageMenusSettings.this, SubMenuRenameMenu.class);
				intent.putExtra("com.irrenhaus.advancedlauncher.MenuName", name);
				
				SubMenuManageMenusSettings.this.startActivityForResult(intent, 0);
			}
        });
        
        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				mCursor.moveToPosition(position);
				final String name = mCursor.getString(1);
						
				AlertDialog.Builder builder = new AlertDialog.Builder(SubMenuManageMenusSettings.this);
				
				builder.setMessage("Delete this submenu?");
				builder.setPositiveButton("Delete", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Log.d("SubMenuManageMenus", "Trying to delete "+name);
						DeleteMenu(name);
					}
				});
				builder.setNegativeButton("Cancel", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
				
				AlertDialog alert = builder.create();
				
				alert.show();
				return false;
			}
        });
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		
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

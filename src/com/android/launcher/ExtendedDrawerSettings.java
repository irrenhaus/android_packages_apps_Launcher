package com.android.launcher;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class ExtendedDrawerSettings extends ListActivity {
	
	LayoutInflater mInflater;
	Cursor mCursor;
	SQLiteDatabase mDatabase;
	ApplicationsAdapter mAdapter;
	ListView mListView;
	public static Launcher activeLauncher;

	public static final int mnuAddItem = Menu.FIRST + 1;
	public static final int mnuDeleteItem = Menu.FIRST + 2;
		
	
	@Override 
	public boolean onContextItemSelected(MenuItem item) 
    {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int itemId = Integer.valueOf(String.valueOf(info.position));

		mCursor.moveToPosition(itemId);
		int ApplicationId = mCursor.getInt(0);
		
		switch(item.getItemId())
		{
		case mnuDeleteItem:
			DeleteApplication(ApplicationId);
			break;
		}

		refreshCursor();
		
    	return super.onContextItemSelected(item);
    }
	
	/** Database */
	protected static class ExtendedDrawerDBHelper extends SQLiteOpenHelper {
		public ExtendedDrawerDBHelper(Context context) {
			super(context, "extendeddrawer.sqlite", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String ddlScripts = "create table extendeddrawer_hidden (_id integer primary key, name text, intent text);";
			db.execSQL(ddlScripts);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	/** Cursor adapter */
	public class ApplicationsAdapter extends CursorAdapter {

		public ApplicationsAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			TextView ApplicationNameText = (TextView) view.findViewById(R.id.txtTitle);
			String applicationName = cursor.getString(1);

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
		mCursor = mDatabase.query(false, "extendeddrawer_hidden", new String[] { "_id", "name" }, null, null, null, null, null, null);
		mAdapter = new ApplicationsAdapter(this, mCursor);
		setListAdapter(mAdapter);
		
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
                
        setContentView(R.layout.extended_drawersettings);
        
        mListView = (ListView) findViewById(android.R.id.list);
        mInflater = getLayoutInflater();
        
        ExtendedDrawerDBHelper hlp = new ExtendedDrawerDBHelper(this);                 
        mDatabase = hlp.getWritableDatabase(); 

        refreshCursor();
        
        //Listview Context
        mListView.setOnCreateContextMenuListener(
        		new OnCreateContextMenuListener()
        		{
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {

						menu.add(0, mnuDeleteItem, 0, "Add to application drawer");

					}
        		}
        );
        
        //Listview clicked
        mListView.setOnItemClickListener(
        		new OnItemClickListener()
        		{
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						/*
						//Execute the selected script
						mCursor.moveToPosition(position);
						int ScriptId = mCursor.getInt(0);
						
						ExecuteScript(ScriptId);
						 */
					}
        		}
     	);
	}
		
	/**Delete application*/
	void DeleteApplication(int ApplicationId)
	{
		mDatabase.execSQL("DELETE FROM extendeddrawer_hidden WHERE _id = " + ApplicationId);
		refreshCursor();
		
        final LauncherModel model = Launcher.getModel();

        model.dropApplications();
        model.loadApplications(false, activeLauncher, false);
		
	}
}
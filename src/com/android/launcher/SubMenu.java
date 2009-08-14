package com.android.launcher;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.android.launcher.SubMenuSettings.SubMenuDBHelper;

public class SubMenu extends LinearLayout implements OnItemClickListener, OnItemLongClickListener, DragSource, DropTarget {
	public SubMenu(Context context) {
		super(context);
		this.context = context;
	}

	private String title = null;
	private Context context = null;
	private SubMenuAdapter adapter = null;
	private DragController mDragger = null;
	private GridView content = null;
	
	public void onOpen(Context c, String t) {
        
        context = c;
        
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        
        inflater.inflate(R.layout.submenu, this);
        
        title = t;
        
        Log.d("SubMenu", "Opened submenu "+title);
        
        Button closeButton = (Button)findViewById(R.id.close);
        content = (GridView)findViewById(R.id.content);
        
        adapter = new SubMenuAdapter(context, 0);
        
        closeButton.setText(title);
        closeButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Launcher.getModel().closeSubMenu(SubMenu.this);
			}
        	
        });
        
        adapter.generateAppsList(title);
        
        content.setAdapter(adapter);

        content.setOnItemClickListener(this);
        content.setOnItemLongClickListener(this);
        
        setDragger(SubMenuSettings.activeLauncher.getDragController());
	}
	
	public void onClose()
	{
		final Workspace workspace = SubMenuSettings.activeLauncher.getWorkspace();
        workspace.getChildAt(workspace.getCurrentScreen()).requestFocus();
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		context.startActivity(adapter.getItem(position).intent);
		if(com.android.launcher.extended.data.ExtendedSettings.Home_CloseFolders(parent.getContext()))
        {
            // Close the folder when an app is started
            Launcher.getModel().closeSubMenu(this);
        }
	}
	
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (!view.isInTouchMode()) {
            return false;
        }

        ApplicationInfo app = (ApplicationInfo) adapter.getItem(position);

        mDragger.startDrag(view, this, app, DragController.DRAG_ACTION_COPY);
        
        Launcher.getModel().closeSubMenu(this);

        return true;
    }

	public void onDropCompleted(View target, boolean success) {
		
	}

	public void setDragger(DragController dragger) {
		mDragger = dragger;
	}

	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {
		final ItemInfo item = (ItemInfo) dragInfo;
        final int itemType = item.itemType;
        final ApplicationInfo appInfo = (ApplicationInfo) dragInfo;
        return (itemType == LauncherSettings.Favorites.ITEM_TYPE_APPLICATION ||
                itemType == LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT) && !appInfo.isSubMenu;
	}

	public Rect estimateDropLocation(DragSource source, int x, int y,
			int xOffset, int yOffset, Object dragInfo, Rect recycle) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {
		// TODO Auto-generated method stub
		
	}

	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {
		// TODO Auto-generated method stub
		
	}

	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {
		// TODO Auto-generated method stub
		
	}

	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, Object dragInfo) {
		final ApplicationInfo item = (ApplicationInfo) dragInfo;
        SubMenuDBHelper hlp = new SubMenuDBHelper(context, false);
        SQLiteDatabase db = hlp.getWritableDatabase();
        SubMenuSettings.MoveApplication(db, this.title, item.title.toString(), item.intent.toURI(), false);
        Launcher.getModel().loadApplications(false, SubMenuSettings.activeLauncher, false);
        adapter.clear();
        adapter.generateAppsList(title);

    	Toast.makeText(this.getContext(), "Application '"+item.title+"' has been moved to '"+title+"'", Toast.LENGTH_SHORT).show();
	}
}

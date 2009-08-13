package com.android.launcher;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class SubMenu extends LinearLayout implements OnItemClickListener, OnItemLongClickListener, DragSource {
	public SubMenu(Context context) {
		super(context);
		this.context = context;
	}

	private String title = null;
	private Context context = null;
	private SubMenuAdapter adapter = null;
	private DragController mDragger = null;
	
	public void onOpen(Context c, String t) {
        
        context = c;
        
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        
        inflater.inflate(R.layout.submenu, this);
        
        title = t;
        
        Log.d("SubMenu", "Opened submenu "+title);
        
        Button closeButton = (Button)findViewById(R.id.close);
        GridView content = (GridView)findViewById(R.id.content);
        
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
		// TODO Auto-generated method stub
		
	}

	public void setDragger(DragController dragger) {
		mDragger = dragger;
	}
}

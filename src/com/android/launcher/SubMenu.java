package com.android.launcher;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class SubMenu extends Activity implements OnItemClickListener{
	private String title = null;
	private Context context = null;
	private SubMenuAdapter adapter = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        context = this.getApplicationContext();
        
        setContentView(R.layout.submenu);
        
        title = getIntent().getStringExtra("com.android.launcher.Extended.SubMenu");
        
        Log.d("SubMenu", "Opened submenu "+title);
        
        Button closeButton = (Button)findViewById(R.id.close);
        GridView content = (GridView)findViewById(R.id.content);
        
        adapter = new SubMenuAdapter(context, 0);
        
        closeButton.setText(title);
        closeButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				SubMenu.this.finish();
			}
        	
        });
        
        adapter.generateAppsList(title);
        
        content.setAdapter(adapter);
        
        content.setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.startActivity(adapter.getItem(position).intent);
		if(com.android.launcher.extended.data.ExtendedSettings.Home_CloseFolders(parent.getContext().getApplicationContext()))
        {
            // Close the folder when an app is started
            this.finish();
        }
	}
}

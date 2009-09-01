package com.irrenhaus.advancedlauncher;

import com.irrenhaus.advancedlauncher.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SubMenuRenameMenu extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.submenu_rename);

		Button buttonOK = (Button)findViewById(R.id.menuOK);
		Button buttonCancel = (Button)findViewById(R.id.menuCancel);
		final EditText menuName = (EditText)findViewById(R.id.menuName);
		
		final String name = getIntent().getStringExtra("com.irrenhaus.advancedlauncher.MenuName");
		
		if(name != null)
			menuName.setText(name);
		
		buttonOK.setText("OK");
		buttonCancel.setText("Quit");
		
		buttonOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("com.irrenhaus.advancedlauncher.RenameSubMenuOrig", name);
				intent.putExtra("com.irrenhaus.advancedlauncher.RenameSubMenuNew", menuName.getText().toString());
				SubMenuRenameMenu.this.setResult(0, intent);
				SubMenuRenameMenu.this.finish();
			}
		});
		
		buttonCancel.setOnClickListener(new OnClickListener()  {
			public void onClick(View v) {
				SubMenuRenameMenu.this.setResult(1);
				SubMenuRenameMenu.this.finish();
			}
		});
	}
}

package com.android.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SubMenuAddMenu extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.submenu_add);

		Button buttonOK = (Button)findViewById(R.id.menuOK);
		Button buttonCancel = (Button)findViewById(R.id.menuCancel);
		final EditText menuName = (EditText)findViewById(R.id.menuName);
		
		buttonOK.setText("OK");
		buttonCancel.setText("Quit");
		
		buttonOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("com.android.launcher.AddSubMenu", menuName.getText().toString());
				SubMenuAddMenu.this.setResult(0, intent);
				SubMenuAddMenu.this.finish();
			}
		});
		
		buttonCancel.setOnClickListener(new OnClickListener()  {
			public void onClick(View v) {
				SubMenuAddMenu.this.setResult(1);
				SubMenuAddMenu.this.finish();
			}
		});
	}
}

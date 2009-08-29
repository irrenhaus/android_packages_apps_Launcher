package com.irrenhaus.advancedlauncher.extended.display;

import android.R;
import android.content.Context;
import android.view.View;

public interface IMenuItem {
	public String Title = "-";
	public Boolean Enabled = true;
	public String Tag = "";
	public int Icon = R.drawable.ic_menu_more;
	public View GetView(Context ctx);
}

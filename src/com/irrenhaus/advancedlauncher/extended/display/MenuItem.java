package com.irrenhaus.advancedlauncher.extended.display;

import com.irrenhaus.advancedlauncher.*;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuItem implements IMenuItem {

	public String Title = "";
	public String Description = "";
	public String Tag = "";
	public Boolean Enabled = true;
	public int Icon = android.R.drawable.ic_menu_more;

	public View GetView(Context ctx) {

		LayoutInflater mInflater = LayoutInflater.from(ctx);
		View itemView = mInflater.inflate(R.layout.extended_menurow, null);

		TextView RowTitle = (TextView)itemView.findViewById(R.id.TextViewMenuTitle);
		TextView RowDescription = (TextView)itemView.findViewById(R.id.TextViewMenuDescription);
		ImageView RowImage = (ImageView)itemView.findViewById(R.id.ImageViewMenuIcon);

		String rowTitle = this.Title;		
		String rowDescription = this.Description;
		
		RowTitle.setText(rowTitle);
		RowDescription.setText(rowDescription);
		RowImage.setImageResource(this.Icon);

		return itemView;
	}
}

package com.android.launcher.extended.display;

import java.util.ArrayList;

import android.widget.BaseAdapter;
import android.content.Context;
import android.graphics.Color;
import android.opengl.Visibility;
import android.view.View;
import android.view.ViewGroup;

public class MenuAdapter extends BaseAdapter
{
	private Context context;
	private ArrayList<MenuItem> ItemList = new ArrayList<MenuItem>();
	public static String TAG = "Launcher Extended - MenuAdapter";	

	public MenuAdapter(Context ct, ArrayList<MenuItem> itemList)
	{
		context=ct;
		ItemList = itemList;
	}

	public int getCount() {
		return ItemList.size();
	}

	public Object getItem(int position) {
		return ItemList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = ItemList.get(position).GetView(context);
		
		if(!ItemList.get(position).Enabled)
			itemView.setEnabled(false);
		
		return itemView;
	}
}	

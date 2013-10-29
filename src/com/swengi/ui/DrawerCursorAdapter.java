package com.swengi.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.swengi.core.R;
import com.swengi.db.DepartmentTable;

public class DrawerCursorAdapter extends CursorAdapter {
	
	private LayoutInflater inflater;
	private ViewHolder holder;

	public DrawerCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
	}
	
	// To improve performance
	public static class ViewHolder {
		public TextView title;
		public ImageView icon;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		holder = (ViewHolder) view.getTag();
		
		int depTitleIndex = cursor.getColumnIndex(DepartmentTable.TITLE);
		String depTitle = cursor.getString(depTitleIndex);
		
		holder.title.setText(depTitle);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		View view = inflater.inflate(R.layout.drawer_list_item, parent, false);
		holder = new ViewHolder();

		holder.title = (TextView) view.findViewById(R.id.drawer_list_item_text);
		holder.icon = (ImageView) view.findViewById(R.id.drawer_list_item_icon);

		view.setTag(holder);

		return view;
	}
}

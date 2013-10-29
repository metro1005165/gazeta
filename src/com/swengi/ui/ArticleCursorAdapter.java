package com.swengi.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.swengi.core.MainApplication;
import com.swengi.core.R;
import com.swengi.db.ArticleTable;
import com.swengi.db.DepartmentTable;
import com.swengi.db.PictureTable;
import com.swengi.db.SwengiContentProvider;
import com.swengi.utils.BitmapCache;

public class ArticleCursorAdapter extends CursorAdapter {
	
	private LayoutInflater inflater;
	private Context context;
	private ViewHolder holder;
	private BitmapCache bitmapCache;

	public ArticleCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
		this.context = context;
		bitmapCache = MainApplication
				.getApplicationFromActivityContext(context).getBitmapCache();
		
	}
	
	// To improve performance
	public static class ViewHolder {
		public TextView date;
		public TextView header;
		public TextView lead;
		public TextView department;
		public ImageView thumbnail;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		holder = (ViewHolder) view.getTag();
		
		String imgId = getImageId(cursor.getInt(cursor
				.getColumnIndex(ArticleTable.ID)));
		String depTitle = getDepartmentTitle(cursor.getInt(cursor
				.getColumnIndex(ArticleTable.DEPARTMENT_ID)));
		
		if (imgId != null) {
			Bitmap thumbnail = bitmapCache.get(imgId);
			if (thumbnail != null) {
				holder.thumbnail.setVisibility(View.GONE);
				holder.thumbnail.setImageBitmap(thumbnail);
				holder.thumbnail.setVisibility(View.VISIBLE);
			} else {
				Bitmap loading = bitmapCache.get(MainApplication.LOADING_ID);
				if (loading != null) {
					holder.thumbnail.setVisibility(View.GONE);
					holder.thumbnail.setImageBitmap(loading);
					holder.thumbnail.setVisibility(View.VISIBLE);
				}
			}
		} else {
			Bitmap empty = bitmapCache.get(MainApplication.NO_IMAGE_ID);
			if (empty != null) {
				holder.thumbnail.setVisibility(View.GONE);
				holder.thumbnail.setImageBitmap(empty);
				holder.thumbnail.setVisibility(View.VISIBLE);
			}
		}
		
		int dateIndex = cursor.getColumnIndex(ArticleTable.DATE);
		int leadIndex = cursor.getColumnIndex(ArticleTable.LEAD);
		int mainHeaderIndex = cursor.getColumnIndex(ArticleTable.MAIN_HEADER);
		
		String mainHeader = cursor.getString(mainHeaderIndex);
		String lead = cursor.getString(leadIndex);
		String date = cursor.getString(dateIndex);
				
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Long dateInMillis = Long.parseLong(date);
		Date resultdate = new Date(dateInMillis);
		
		holder.date.setText(dateFormat.format(resultdate));
		holder.lead.setText(lead);
		holder.header.setText(mainHeader);
		holder.department.setText(depTitle);	
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		View view = inflater.inflate(R.layout.fragment_articles_list, parent, false);
		holder = new ViewHolder();
		
		holder.date = (TextView) view.findViewById(R.id.date);
		holder.header = (TextView) view.findViewById(R.id.heading);
		holder.lead = (TextView) view.findViewById(R.id.lead);
		holder.department = (TextView) view.findViewById(R.id.department);
		holder.thumbnail = (ImageView) view.findViewById(R.id.articleIcon);
		 
		view.setTag(holder);
		 
		 return view;
	}
	
	private String getDepartmentTitle(int depId) {
		
		String filter = DepartmentTable.ID + "=" + depId + "";
		Uri uri = SwengiContentProvider.DEPARTMENTS_URI;
		String depTitle = null;
		
		Cursor cursor = context.getContentResolver().query(uri, null, filter, null, null);
		
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				int depTitleIndex = cursor.getColumnIndex(DepartmentTable.TITLE);
				depTitle = cursor.getString(depTitleIndex);
			}
		}

		cursor.close();	
		return depTitle;	
	}
	
	private String getImageId(int artId) {
		
		String filter = PictureTable.ARTICLE_ID + "=" + artId + "";
		Uri uri = SwengiContentProvider.PICTURES_URI;
		String picId = null;
		
		Cursor cursor = context.getContentResolver().query(uri, null, filter, null, null);
		
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				int picIdIndex = cursor.getColumnIndex(PictureTable.PICTURE_ID);
				picId = cursor.getString(picIdIndex);
			}
		}

		cursor.close();	
		return picId;	
	}
}

package com.swengi.core;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.swengi.db.ArticleTable;
import com.swengi.db.DepartmentTable;
import com.swengi.db.SwengiContentProvider;

public class TestActivity extends Activity {
	
	private final String TAG = "TestActivity";
	
	private EditText etTitles;
	private EditText etIds;
	private Button btnTitles;
	private Button btnIds;
	
	private final int TITLES = R.id.btnDepTitle;
	private final int IDS = R.id.btnDepSerId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		etTitles = (EditText) findViewById(R.id.etTitles);
		etIds = (EditText) findViewById(R.id.etIds);
		btnTitles = (Button) findViewById(R.id.btnDepTitle);
		btnIds = (Button) findViewById(R.id.btnDepSerId);

		btnTitles.setOnClickListener(btnListener);
		btnIds.setOnClickListener(btnListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}
	
	private OnClickListener btnListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			switch(v.getId()) {
			
			case TITLES:
				String depTitle = etTitles.getText().toString();
				if (!depTitle.isEmpty()) {
					int id = performTitlesQuery(depTitle);
					if (id > 0) {
						String filter = ArticleTable.DEPARTMENT_ID + "=" + id
								+ "";
						Cursor cursor = getContentResolver()
								.query(SwengiContentProvider.ARTICLES_URI, null, filter,
										null, null);
						processCursor(cursor);
					}
				}
				break;
			case IDS:
				String depId = etIds.getText().toString();
				if (!depId.isEmpty()) {
					int id = performIdsQuery(depId);
					if (id > 0) {
						String filter = ArticleTable.DEPARTMENT_ID + "=" + id
								+ "";
						Cursor cursor = getContentResolver()
								.query(SwengiContentProvider.ARTICLES_URI, null, filter,
										null, null);
						processCursor(cursor);
					}
				}
				break;
			}
			
		}
	};
	
	private int performTitlesQuery(String depTitle) {
		
		String filter = DepartmentTable.TITLE + " LIKE '" + depTitle
				+ "'";
		
		Cursor cursor = getContentResolver()
				.query(SwengiContentProvider.DEPARTMENTS_URI, null, filter,
						null, null);
		
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				int idIndex = cursor.getColumnIndex(DepartmentTable.ID);
				int id = cursor.getInt(idIndex);
				
				return id;
			}
		}
		
		return 0;

	}
	
	private int performIdsQuery(String depId) {

		String filter = DepartmentTable.DEPARTMENT_ID + " LIKE '" + depId + "'";

		Cursor cursor = getContentResolver()
				.query(SwengiContentProvider.DEPARTMENTS_URI, null, filter,
						null, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				int idIndex = cursor.getColumnIndex(DepartmentTable.ID);
				int id = cursor.getInt(idIndex);

				return id;
			}
		}

		return 0;

	}
	
	private void processCursor(Cursor cursor) {
		
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				int idIndex = cursor.getColumnIndex(ArticleTable.ID);
				int serverIdIndex = cursor.getColumnIndex(ArticleTable.ARTICLE_ID);
				int mainHeaderIndex = cursor.getColumnIndex(ArticleTable.MAIN_HEADER);
				int leadIndex = cursor.getColumnIndex(ArticleTable.LEAD);
				int dateIndex = cursor.getColumnIndex(ArticleTable.DATE);
				int bodyTextIndex = cursor.getColumnIndex(ArticleTable.BODY_TEXT);
				
				String id = cursor.getString(idIndex);
				String serverId = cursor.getString(serverIdIndex);
				String mainHeader = cursor.getString(mainHeaderIndex);
				String lead = cursor.getString(leadIndex);
				String date = cursor.getString(dateIndex);
				String bodyText = cursor.getString(bodyTextIndex);
				
				String result = "Article [articleId = " + id + ", serverId = "
						+ serverId + ", mainHeader = " + mainHeader + ", lead=" + lead
						+ ", date = " + date
						+ ", bodyText = " + bodyText + "]";
				
				Log.e(TAG, result);	
			}
		}
	}
}

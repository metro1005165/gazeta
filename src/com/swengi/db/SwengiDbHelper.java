package com.swengi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SwengiDbHelper extends SQLiteOpenHelper {
	
	private static final String LOG_TAG = "SwengiDbHelper";
	private static final String DATABASE_NAME = "swengi.db";
	private static final int DATABASE_VERSION = 1;

	public SwengiDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ArticleTable.CREATE_TABLE_ARTICLE);	
		db.execSQL(DepartmentTable.CREATE_TABLE_DEPARTMENT);
		db.execSQL(EditorTable.CREATE_TABLE_EDITOR);
		db.execSQL(PictureTable.CREATE_TABLE_PICTURE);
		db.execSQL(CollaboratorTable.CREATE_TABLE_COLLABORATOR);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		Log.e(LOG_TAG, "Dropping Article table...");
		db.execSQL(ArticleTable.DROP_TABLE_ARTICLE);
		Log.e(LOG_TAG, "Dropping Department table...");
		db.execSQL(DepartmentTable.DROP_TABLE_DEPARTMENT);
		Log.e(LOG_TAG, "Dropping Editor table...");
		db.execSQL(EditorTable.DROP_TABLE_EDITOR);
		Log.e(LOG_TAG, "Dropping Picture table...");
		db.execSQL(PictureTable.DROP_TABLE_PICTURE);
		Log.e(LOG_TAG, "Dropping Collaborator table...");
		db.execSQL(CollaboratorTable.DROP_TABLE_COLLABORATOR);
		onCreate(db);
	}
}

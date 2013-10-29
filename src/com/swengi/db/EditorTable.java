package com.swengi.db;

public class EditorTable {

	// Editor table name
	public static final String TABLE_NAME = "editor_table";

	// Editor table columns
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String PHOTO_URL = "photo_url";

	// Create Editor Table
	public static final String CREATE_TABLE_EDITOR = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME
			+ "("
			+ ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ NAME
			+ " TEXT,"
			+ PHOTO_URL
			+ " TEXT"
			+ ");";

	// Drop Editor Table
	public static final String DROP_TABLE_EDITOR = "DROP TABLE IF EXISTS "
			+ TABLE_NAME + ";";

}

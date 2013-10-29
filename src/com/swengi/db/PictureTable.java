package com.swengi.db;

public class PictureTable {

	// Picture table name
	public static final String TABLE_NAME = "picture_table";

	// Picture table columns
	public static final String ID = "_id";
	public static final String PICTURE_ID = "picture_id";
	public static final String URL = "url";
	public static final String CAPTION = "caption";
	public static final String PHOTOGRAPHER = "photographer";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";

	// Article table FK
	public static final String ARTICLE_ID = "article_id";

	// Create Picture Table
	public static final String CREATE_TABLE_PICTURE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME
			+ "("
			+ ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ PICTURE_ID
			+ " TEXT,"
			+ URL
			+ " TEXT,"
			+ CAPTION
			+ " TEXT,"
			+ PHOTOGRAPHER
			+ " TEXT,"
			+ WIDTH
			+ " INTEGER,"
			+ HEIGHT
			+ " INTEGER,"
			+ ARTICLE_ID + " INTEGER NOT NULL" + ");";

	// Drop Picture Table
	public static final String DROP_TABLE_PICTURE = "DROP TABLE IF EXISTS "
			+ TABLE_NAME + ";";

}

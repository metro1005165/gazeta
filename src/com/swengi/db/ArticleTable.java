package com.swengi.db;

public class ArticleTable {

	// Article table name
	public static final String TABLE_NAME = "article_table";

	// Article table columns
	public static final String ID = "_id";
	public static final String ARTICLE_ID = "article_id";
	public static final String MAIN_HEADER = "main_header";
	public static final String LEAD = "lead";
	public static final String DATE = "date";
	public static final String BODY_TEXT = "body_text";

	// Department table FK
	public static final String DEPARTMENT_ID = "department_id";

	// Create Article Table
	public static final String CREATE_TABLE_ARTICLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME
			+ "("
			+ ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ ARTICLE_ID
			+ " TEXT,"
			+ MAIN_HEADER
			+ " TEXT,"
			+ LEAD
			+ " TEXT,"
			+ DATE
			+ " TEXT,"
			+ BODY_TEXT
			+ " TEXT,"
			+ DEPARTMENT_ID + " INTEGER NOT NULL" + ");";

	// Drop Article Table
	public static final String DROP_TABLE_ARTICLE = "DROP TABLE IF EXISTS "
			+ TABLE_NAME + ";";

}

package com.swengi.db;

public class DepartmentTable {

	// Department table name
	public static final String TABLE_NAME = "department_table";

	// Department table columns
	public static final String ID = "_id";
	public static final String DEPARTMENT_ID = "department_id";
	public static final String TITLE = "title";

	// Create Department Table
	public static final String CREATE_TABLE_DEPARTMENT = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME
			+ "("
			+ ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ DEPARTMENT_ID + " TEXT," + TITLE + " TEXT);";

	// Drop Department Table
	public static final String DROP_TABLE_DEPARTMENT = "DROP TABLE IF EXISTS "
			+ TABLE_NAME + ";";

}

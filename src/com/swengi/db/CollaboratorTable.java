package com.swengi.db;

public class CollaboratorTable {
	
	// Collaborator table name
	public static final String TABLE_NAME = "collaborator_table";

	// Collaborator table columns
	public static final String ID = "_id";
	// Foreign Keys for Many-to-Many relations
	public static final String EDITOR_ID = "editor_id";
	public static final String ARTICLE_ID = "article_id";

	// Create Collaborator Table
	public static final String CREATE_TABLE_COLLABORATOR = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME
			+ "("
			+ ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ EDITOR_ID
			+ " INTEGER,"
			+ ARTICLE_ID
			+ " INTEGER"
			+ ");";

	// Drop Collaborator Table
	public static final String DROP_TABLE_COLLABORATOR = "DROP TABLE IF EXISTS "
			+ TABLE_NAME + ";";

}

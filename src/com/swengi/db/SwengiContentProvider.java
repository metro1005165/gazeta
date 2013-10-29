package com.swengi.db;

import java.util.HashMap;

import com.swengi.network.ImageDownloadTask;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class SwengiContentProvider extends ContentProvider {

	// Reference to SwengiDbHelper
	private SwengiDbHelper dbHelper;
	
	// Class name for debugging 
	private static final String TAG = "SwengiContentProvider";

	// A unique name for SwengiDB content provider (signature)
	public static final String AUTHORITY = "swengi.db.content.provider";

	// Base path to this content provider
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	// Base paths for each table
	public static final Uri ARTICLES_URI = Uri.parse("content://" + AUTHORITY
			+ "/articles");
	public static final Uri DEPARTMENTS_URI = Uri.parse("content://" + AUTHORITY
			+ "/departments");
	public static final Uri PICTURES_URI = Uri.parse("content://" + AUTHORITY
			+ "/pictures");
	public static final Uri EDITORS_URI = Uri.parse("content://" + AUTHORITY
			+ "/editors");
	public static final Uri COLLABORATORS_URI = Uri.parse("content://" + AUTHORITY
			+ "/collaborators");

	// Aliases for URI queries
	static final int ARTICLES = 100;
	static final int DEPARTMENTS = 101;
	static final int PICTURES = 102;
	static final int EDITORS = 103;
	static final int COLLABORATORS = 104;
	
	// Creates a UriMatcher object.
	private static final UriMatcher uriMatcher;
	// To project columns in Article table
	private static HashMap<String, String> articlesProjectionMap;
	private static HashMap<String, String> departmentsProjectionMap;
	private static HashMap<String, String> picturesProjectionMap;
	private static HashMap<String, String> editorsProjectionMap;
	private static HashMap<String, String> collaboratorsProjectionMap;
	
	// Tables columns projections and aliases
	static {
		// Configuring the matcher
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "articles", ARTICLES);
		uriMatcher.addURI(AUTHORITY, "departments", DEPARTMENTS);
		uriMatcher.addURI(AUTHORITY, "pictures", PICTURES);
		uriMatcher.addURI(AUTHORITY, "editors", EDITORS);
		uriMatcher.addURI(AUTHORITY, "collaborators", COLLABORATORS);

		// Article table projection map
		articlesProjectionMap = new HashMap<String, String>();
		articlesProjectionMap.put(ArticleTable.ID, ArticleTable.ID);
		articlesProjectionMap.put(ArticleTable.ARTICLE_ID, ArticleTable.ARTICLE_ID);
		articlesProjectionMap.put(ArticleTable.MAIN_HEADER, ArticleTable.MAIN_HEADER);
		articlesProjectionMap.put(ArticleTable.LEAD, ArticleTable.LEAD);
		articlesProjectionMap.put(ArticleTable.DATE, ArticleTable.DATE);
		articlesProjectionMap.put(ArticleTable.BODY_TEXT, ArticleTable.BODY_TEXT);
		articlesProjectionMap.put(ArticleTable.DEPARTMENT_ID, ArticleTable.DEPARTMENT_ID);
		
		// Department table projection map
		departmentsProjectionMap = new HashMap<String, String>();
		departmentsProjectionMap.put(DepartmentTable.ID, DepartmentTable.ID);
		departmentsProjectionMap.put(DepartmentTable.DEPARTMENT_ID, DepartmentTable.DEPARTMENT_ID);
		departmentsProjectionMap.put(DepartmentTable.TITLE, DepartmentTable.TITLE);	
		
		// Picture table projection map
		picturesProjectionMap = new HashMap<String, String>();
		picturesProjectionMap.put(PictureTable.ID, PictureTable.ID);
		picturesProjectionMap.put(PictureTable.PICTURE_ID,PictureTable.PICTURE_ID);
		picturesProjectionMap.put(PictureTable.CAPTION,PictureTable.CAPTION);
		picturesProjectionMap.put(PictureTable.WIDTH,PictureTable.WIDTH);
		picturesProjectionMap.put(PictureTable.HEIGHT,PictureTable.HEIGHT);
		picturesProjectionMap.put(PictureTable.URL, PictureTable.URL);
		picturesProjectionMap.put(PictureTable.PHOTOGRAPHER,PictureTable.PHOTOGRAPHER);
		picturesProjectionMap.put(PictureTable.ARTICLE_ID, PictureTable.ARTICLE_ID);

		// Editor table projection map
		editorsProjectionMap = new HashMap<String, String>();
		editorsProjectionMap.put(EditorTable.ID, EditorTable.ID);
		editorsProjectionMap.put(EditorTable.NAME, EditorTable.NAME);
		editorsProjectionMap.put(EditorTable.PHOTO_URL, EditorTable.PHOTO_URL);

		// Collaborator table projection map
		collaboratorsProjectionMap = new HashMap<String, String>();
		collaboratorsProjectionMap.put(CollaboratorTable.ID, CollaboratorTable.ID);
		collaboratorsProjectionMap.put(CollaboratorTable.EDITOR_ID, CollaboratorTable.EDITOR_ID);
		collaboratorsProjectionMap.put(CollaboratorTable.ARTICLE_ID, CollaboratorTable.ARTICLE_ID);
	}

	@Override
	public boolean onCreate() {
		Log.e(TAG, "Creating SwengiDbHelper object...");
		dbHelper = new SwengiDbHelper(getContext());

		return true;
	}

	// Unimplemented methods **************************************
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Implement if you see a need for it...
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Implement if you see a need for it...
		return null;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	// ***************************************************************

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		ContentValues clonedValues;
		SQLiteDatabase swengiDb;

		// We can insert values into different tables
		// Hence we may have multiple cases
		switch (uriMatcher.match(uri)) {
		case ARTICLES:
			if (values != null) {
				clonedValues = new ContentValues(values);
			} else {
				clonedValues = new ContentValues();
			}

			Log.e(TAG, "Obtaining writable database...");
			// Open database for writing
			swengiDb = dbHelper.getWritableDatabase();
			Log.e(TAG, "swengiDb = " + swengiDb);

			if (swengiDb == null) {
				throw new SQLException("Unable to get writable database...");
			}

			// We need to make sure that we are not inserting duplicate articles
			Uri articleStatusUri = articleStatus(values);
			Log.e(TAG, "Checking article status: Status[Uri] = "
					+ articleStatusUri);
			if (articleStatusUri != null) {
				return articleStatusUri;
			}

			// If we are here it means this record is not duplicate.
			Log.e(TAG, "Inserting new article...");
			long artRowId = swengiDb.insert(ArticleTable.TABLE_NAME,
					ArticleTable.ARTICLE_ID, clonedValues);
			if (artRowId > 0) {
				Uri articleUri = ContentUris.withAppendedId(ARTICLES_URI,
						artRowId);
				Log.e(TAG, "Success! Art Uri = " + articleUri);
				getContext().getContentResolver()
						.notifyChange(articleUri, null);
				return articleUri;
			}

			throw new SQLException("Failed to insert article row into " + uri);

		case DEPARTMENTS:

			if (values != null) {
				clonedValues = new ContentValues(values);
			} else {
				clonedValues = new ContentValues();
			}

			Log.e(TAG, "Obtaining writable database...");
			// Open database for writing
			swengiDb = dbHelper.getWritableDatabase();
			Log.e(TAG, "swengiDb = " + swengiDb);

			if (swengiDb == null) {
				throw new SQLException("Unable to get writable database...");
			}

			// We need to make sure that we are not inserting duplicate departments
			Uri depStatusUri = departmentStatus(values);
			Log.e(TAG, "Checking department status: Status[Uri] = "
					+ depStatusUri);
			if (depStatusUri != null) {
				return depStatusUri;
			}

			// If we are here it means this record is not duplicate.
			Log.e(TAG, "Inserting new department...");
			long depRowId = swengiDb.insert(DepartmentTable.TABLE_NAME,
					DepartmentTable.DEPARTMENT_ID, clonedValues);
			if (depRowId > 0) {
				Uri depUri = ContentUris.withAppendedId(DEPARTMENTS_URI,
						depRowId);
				Log.e(TAG, "Success! Dep Uri = " + depUri);
				getContext().getContentResolver().notifyChange(depUri, null);
				return depUri;
			}

			throw new SQLException("Failed to insert department row into "
					+ uri);

		case PICTURES:

			if (values != null) {
				clonedValues = new ContentValues(values);
			} else {
				clonedValues = new ContentValues();
			}

			Log.e(TAG, "Obtaining writable database...");
			// Open database for writing
			swengiDb = dbHelper.getWritableDatabase();
			Log.e(TAG, "swengiDb = " + swengiDb);

			if (swengiDb == null) {
				throw new SQLException("Unable to get writable database...");
			}

			// We need to make sure that we are not inserting duplicate pictures
			Uri picStatusUri = pictureStatus(values);
			Log.e(TAG, "Checking picture status: Status[Uri] = "
					+ picStatusUri);
			if (picStatusUri != null) {
				return picStatusUri;
			}
			
			downloadPicture(clonedValues.getAsString(PictureTable.URL),
					clonedValues.getAsString(PictureTable.PICTURE_ID));

			// If we are here it means this record is not duplicate.
			Log.e(TAG, "Inserting new picture...");
			long picRowId = swengiDb.insert(PictureTable.TABLE_NAME,
					PictureTable.PICTURE_ID, clonedValues);
			if (picRowId > 0) {
				Uri picUri = ContentUris.withAppendedId(PICTURES_URI,
						picRowId);
				Log.e(TAG, "Success! Pic Uri = " + picUri);
				getContext().getContentResolver().notifyChange(picUri, null);
				return picUri;
			}

			throw new SQLException("Failed to insert picture row into "
					+ uri);

		case EDITORS:

			if (values != null) {
				clonedValues = new ContentValues(values);
			} else {
				clonedValues = new ContentValues();
			}

			Log.e(TAG, "Obtaining writable database...");
			// Open database for writing
			swengiDb = dbHelper.getWritableDatabase();
			Log.e(TAG, "swengiDb = " + swengiDb);

			if (swengiDb == null) {
				throw new SQLException("Unable to get writable database...");
			}

			// We need to make sure that we are not inserting duplicate editors
			Uri editorStatusUri = editorStatus(values);
			Log.e(TAG, "Checking editor status: Status[Uri] = "
					+ editorStatusUri);
			if (editorStatusUri != null) {
				return editorStatusUri;
			}

			// If we are here it means this record is not duplicate.
			Log.e(TAG, "Inserting new editor...");
			long editorRowId = swengiDb.insert(EditorTable.TABLE_NAME,
					null, clonedValues);
			if (editorRowId > 0) {
				Uri editorUri = ContentUris.withAppendedId(EDITORS_URI,
						editorRowId);
				Log.e(TAG, "Success! Editor Uri = " + editorUri);
				getContext().getContentResolver().notifyChange(editorUri, null);
				return editorUri;
			}

			throw new SQLException("Failed to insert editor row into "
					+ uri);

		case COLLABORATORS:

			if (values != null) {
				clonedValues = new ContentValues(values);
			} else {
				clonedValues = new ContentValues();
			}

			Log.e(TAG, "Obtaining writable database...");
			// Open database for writing
			swengiDb = dbHelper.getWritableDatabase();
			Log.e(TAG, "swengiDb = " + swengiDb);

			if (swengiDb == null) {
				throw new SQLException("Unable to get writable database...");
			}
			
			// We need to make sure that we are not inserting duplicate collaborators
			Uri collabStatusUri = collaboratorStatus(values);
			Log.e(TAG, "Checking collaborator status: Status[Uri] = "
					+ collabStatusUri);
			if (collabStatusUri != null) {
				return collabStatusUri;
			}

			// If we are here it means this record is not duplicate.
			Log.e(TAG, "Inserting new collaborator record...");
			long colRowId = swengiDb.insert(CollaboratorTable.TABLE_NAME,
					null, clonedValues);
			if (colRowId > 0) {
				Uri colUri = ContentUris.withAppendedId(COLLABORATORS_URI,
						colRowId);
				Log.e(TAG, "Success! Collaborator Uri = " + colUri);
				getContext().getContentResolver().notifyChange(colUri, null);
				return colUri;
			}

			throw new SQLException("Failed to insert collaborator row into "
					+ uri);

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		switch (uriMatcher.match(uri)) {

		case ARTICLES:
			queryBuilder.setTables(ArticleTable.TABLE_NAME);
			queryBuilder.setProjectionMap(articlesProjectionMap);
			break;
		case DEPARTMENTS:
			queryBuilder.setTables(DepartmentTable.TABLE_NAME);
			queryBuilder.setProjectionMap(departmentsProjectionMap);
			break;
		case PICTURES:
			queryBuilder.setTables(PictureTable.TABLE_NAME);
			queryBuilder.setProjectionMap(picturesProjectionMap);
			break;
		case EDITORS:
			queryBuilder.setTables(EditorTable.TABLE_NAME);
			queryBuilder.setProjectionMap(editorsProjectionMap);
			break;
		case COLLABORATORS:
			queryBuilder.setTables(CollaboratorTable.TABLE_NAME);
			queryBuilder.setProjectionMap(collaboratorsProjectionMap);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);

		}

		Log.e(TAG, "Obtaining readable database...");
		// Open database for reading
		SQLiteDatabase swengiDb = dbHelper.getWritableDatabase();
		Log.e(TAG, "swengiDb = " + swengiDb);
		
		if (swengiDb == null) {
			throw new SQLException("Unable to get readable database...");
		}

		Cursor cursor = queryBuilder.query(swengiDb, projection, selection,
				selectionArgs, null, null, sortOrder);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}
	
	private Uri articleStatus(ContentValues values) {
		
		String articleServerId = (String) values.get(ArticleTable.ARTICLE_ID);
		String filter = ArticleTable.ARTICLE_ID + " LIKE '" + articleServerId + "'";
		Uri returnUri = null;
		
		Cursor cursor = query(ARTICLES_URI, null, filter, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				int articleIdIndex = cursor.getColumnIndex(ArticleTable.ID);
				int articleId = cursor.getInt(articleIdIndex);
				returnUri = ContentUris.withAppendedId(ARTICLES_URI, articleId);
			}
		}

		cursor.close();	
		return returnUri;
	}

	private Uri departmentStatus(ContentValues values) {
		
		String depServerId = (String) values.get(DepartmentTable.DEPARTMENT_ID);
		String filter = DepartmentTable.DEPARTMENT_ID + " LIKE '" + depServerId + "'";
		Uri returnUri = null;
		
		Cursor cursor = query(DEPARTMENTS_URI, null, filter, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				int depIdIndex = cursor.getColumnIndex(DepartmentTable.ID);
				int depId = cursor.getInt(depIdIndex);
				returnUri = ContentUris.withAppendedId(DEPARTMENTS_URI, depId);
			}
		}

		cursor.close();	
		return returnUri;	
	}

	private Uri pictureStatus(ContentValues values) {

		String picServerId = (String) values.get(PictureTable.PICTURE_ID);
		String filter = PictureTable.PICTURE_ID + " LIKE '" + picServerId
				+ "'";
		Uri returnUri = null;

		Cursor cursor = query(PICTURES_URI, null, filter, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				int idIndex = cursor.getColumnIndex(PictureTable.ID);
				int id = cursor.getInt(idIndex);
				returnUri = ContentUris.withAppendedId(PICTURES_URI, id);
			}
		}

		cursor.close();
		return returnUri;
	}

	private Uri editorStatus(ContentValues values) {

		String editorName = (String) values.get(EditorTable.NAME);
		String filter = EditorTable.NAME + " LIKE '" + editorName
				+ "'";
		Uri returnUri = null;

		Cursor cursor = query(EDITORS_URI, null, filter, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				int idIndex = cursor.getColumnIndex(EditorTable.ID);
				int id = cursor.getInt(idIndex);
				returnUri = ContentUris.withAppendedId(EDITORS_URI, id);
			}
		}

		cursor.close();
		return returnUri;
	}

	private Uri collaboratorStatus(ContentValues values) {

		int editorID = values.getAsInteger(CollaboratorTable.EDITOR_ID);
		int articleID = values.getAsInteger(CollaboratorTable.ARTICLE_ID);
		
		String filter = CollaboratorTable.EDITOR_ID + "=" + editorID
				+ " AND " + CollaboratorTable.ARTICLE_ID + "=" + articleID;
		Uri returnUri = null;

		Cursor cursor = query(COLLABORATORS_URI, null, filter, null, null);

		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				int colIdIndex = cursor.getColumnIndex(CollaboratorTable.ID);
				int rowId = cursor.getInt(colIdIndex);
				returnUri = ContentUris.withAppendedId(COLLABORATORS_URI, rowId);
			}
		}

		cursor.close();
		return returnUri;
	}
	
	private void downloadPicture(String url, String imgId) {
		new ImageDownloadTask(getContext(), imgId).execute(url);
	}
}

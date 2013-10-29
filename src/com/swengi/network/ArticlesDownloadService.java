
package com.swengi.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.swengi.core.MainApplication;
import com.swengi.core.R;
import com.swengi.db.ArticleTable;
import com.swengi.db.CollaboratorTable;
import com.swengi.db.DepartmentTable;
import com.swengi.db.EditorTable;
import com.swengi.db.PictureTable;
import com.swengi.db.SwengiContentProvider;
import com.swengi.model.Article;
import com.swengi.model.Collaborator;
import com.swengi.model.Department;
import com.swengi.model.Editor;
import com.swengi.model.Picture;
import com.swengi.utils.BitmapCache;
import com.swengi.utils.TableType;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

/**
 * @author Yuri
 * The idea of this class is to download the latest articles 
 * from HS server, parse them and store into database
 *
 */
public class ArticlesDownloadService extends IntentService {

	private final String TAG = "ArticlesDownloadService";
	
	private BitmapCache bitmapCache;
	
	// URIs used to fill in the correct tables
	private final Uri acrticleBaseUri = SwengiContentProvider.ARTICLES_URI;
	private final Uri departmentBaseUri = SwengiContentProvider.DEPARTMENTS_URI;
	private final Uri pictureBaseUri = SwengiContentProvider.PICTURES_URI;
	private final Uri editorBaseUri = SwengiContentProvider.EDITORS_URI;
	private final Uri collaboratorBaseUri = SwengiContentProvider.COLLABORATORS_URI;
	
	public static final String GET_LATEST_ARTICLES = "http://www.hs.fi/rest/apps/1/k/articles/latest";
	public static final String RESULT = "result";
	// A package where a Broadcast Receiver resides
	// He will be notified upon service completion
	public static final String NOTIFICATION = "com.swengi.core";
	
	// Stores key-value pairs for further queries
	private HashMap<TableType, Integer> tableRowCache = new HashMap<TableType, Integer>();

	public ArticlesDownloadService() {
		super("ArticlesDownloadService");
	}

	// Will be called in background thread automatically
	@Override
	protected void onHandleIntent(Intent intent) {
		if (!isOnline()) {
			publishResults(5);
			return;
		}

		MainApplication app = (MainApplication) getApplication();
		bitmapCache = app.getBitmapCache();
		parseArticles(getLatestArticles());
		cacheAllImages("HS_Images");
		publishResults(5);
	}

private void cacheAllImages(String dirName) {
		
		Bitmap bitmap = null;
		String imgId = null;
		
		File cacheDir = getDir(dirName, Context.MODE_PRIVATE);
		if (!cacheDir.exists())
			return;
		
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		
		Bitmap bm = BitmapFactory.decodeResource(getResources(),
				R.drawable.no_image);
		Bitmap bm1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.loading);
		bitmapCache.put(MainApplication.NO_IMAGE_ID, bm);
		bitmapCache.put(MainApplication.LOADING_ID, bm1);
		
		for (File f : files) {
			imgId = imgIdFromFile(f);
			bitmap = bitmapFromFile(f);
			Log.e("cacheAllImages", "imgId = " + imgId + " bitmap = " + bitmap);
			if (imgId != null && bitmap != null) {
				bitmapCache.put(imgId, bitmap);
			}
		}	
	}
	
	private String imgIdFromFile(File file) {
		
		String filename = null;
		String extension = null;
		String imgId = null;
		
		if (file != null) {
			filename = file.getName();
	        extension = filename.substring(filename.lastIndexOf('.') + 1);
	        
	        if (extension.contains("png")) {
	        	imgId = filename.replaceAll("[^0-9]+", "");
	        }	
		}
		
		return imgId;	
	}
	
	private Bitmap bitmapFromFile(File file) {

		FileInputStream in;
		Bitmap bitmap = null;

		if (file != null) {
			try {
				in = new FileInputStream(file);
				bitmap = BitmapFactory.decodeStream(in);
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return bitmap;
	}

	private void publishResults(int result) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putExtra(RESULT, result);
		sendBroadcast(intent);
	}
	
	private String getLatestArticles() {
		
		BufferedReader in = null;
		String data = null;

		try {
			HttpClient client = new DefaultHttpClient();
			URI website = new URI(GET_LATEST_ARTICLES);
			HttpGet request = new HttpGet();
			request.setURI(website);
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String nline = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + nline);
			}
			in.close();
			data = sb.toString();
			return data;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
					return data;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	private void parseArticles(String data) {
	
		JSONObject fullContent = null;
		JSONArray primaryContent = null;
		JSONArray secondaryContent = null;
		JSONArray editors = null;
		JSONObject mainPicture = null;
		JSONObject elementPicture = null;
		JSONObject editor = null;
		JSONObject pArrayContent = null;
		JSONObject sArrayContent = null;
		
		try {
			fullContent = (new JSONObject(data)).getJSONObject("data");
			primaryContent = fullContent.optJSONArray("primaryContent");
			secondaryContent = fullContent.optJSONArray("secondaryContent");
			
			for (int i = 0; i < primaryContent.length(); i++) {
				
				pArrayContent = primaryContent.getJSONObject(i);
				sArrayContent = secondaryContent.getJSONObject(i);
				
				insertDepartment(pArrayContent, sArrayContent);
				insertArticle(pArrayContent, sArrayContent);
				
				mainPicture = pArrayContent.optJSONObject("mainPicture");
				elementPicture = pArrayContent.optJSONObject("elementPicture");
			
				Log.e(TAG, "Mainpicture: " + mainPicture + " ElementPicture: " + elementPicture);
				
				if (mainPicture != null && elementPicture != null) {
					insertPicture(mainPicture);	
				} else if (mainPicture != null && elementPicture == null) {
					insertPicture(mainPicture);		
				} else if (mainPicture == null && elementPicture != null) {
					insertPicture(elementPicture);		
				} 
				
				editors = sArrayContent.optJSONArray("editors");
				if (editors != null) {
					for (int j = 0; j < editors.length(); j++) {
						editor = editors.optJSONObject(j);
						int editorRow = insertEditor(editor);
						addCollaborator(editorRow, tableRowCache.get(TableType.ARTICLE));	
					}	
				}
				
				tableRowCache.clear();	
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}			
	}
	
	private void insertPicture(JSONObject picture) {
		
		ContentValues values = new ContentValues();
		Picture pic = new Picture();
		
		pic.setCaption(picture.optString("caption"));
		pic.setHeight(picture.optInt("height"));
		pic.setPhotographer(picture.optString("photographer"));
		pic.setPictureID(picture.optString("id"));
		pic.setUrl(picture.optString("url"));
		pic.setWidth(picture.optInt("width"));
		
		values.put(PictureTable.PICTURE_ID, pic.getPictureID());
		values.put(PictureTable.CAPTION, pic.getCaption());
		values.put(PictureTable.HEIGHT, pic.getHeight());
		values.put(PictureTable.PHOTOGRAPHER, pic.getPhotographer());
		values.put(PictureTable.WIDTH, pic.getWidth());
		values.put(PictureTable.URL, pic.getUrl());
		values.put(PictureTable.ARTICLE_ID, tableRowCache.get(TableType.ARTICLE));
		
		Uri uri = getContentResolver().insert(pictureBaseUri, values);

		String rowId = uri.getPathSegments().get(1);
		Log.e("insertPicture", "RowId: " + rowId);
		
		tableRowCache.put(TableType.PICTURE, Integer.parseInt(rowId));	
	}
	
	private void insertDepartment(JSONObject p, JSONObject s) {

		ContentValues values = new ContentValues();
		Department department = new Department();

		department.setDepartmentId(p.optString("departmentId"));
		department.setTitle(p.optString("departmentTitle"));

		values.put(DepartmentTable.DEPARTMENT_ID, department.getDepartmentId());
		values.put(DepartmentTable.TITLE, department.getTitle());

		Uri uri = getContentResolver().insert(departmentBaseUri, values);

		String rowId = uri.getPathSegments().get(1);
		Log.e("insertDepartment", "RowId: " + rowId);

		tableRowCache.put(TableType.DEPARTMENT, Integer.parseInt(rowId));
	}
	
	private void insertArticle(JSONObject p, JSONObject s) {

		ContentValues values = new ContentValues();
		Article article = new Article();
		
		article.setArticleId(p.optString("id"));
		article.setBodyText(s.optString("bodyText"));
		article.setDate(p.optString("date"));
		article.setLead(p.optString("lead"));
		article.setMainHeader(p.optString("mainHeader"));
		
		// Implement later no empty body text 
		
		values.put(ArticleTable.BODY_TEXT, article.getBodyText());
		values.put(ArticleTable.DATE, article.getDate());
		values.put(ArticleTable.ARTICLE_ID, article.getArticleId());
		values.put(ArticleTable.LEAD, article.getLead());
		values.put(ArticleTable.MAIN_HEADER, article.getMainHeader());
		values.put(ArticleTable.DEPARTMENT_ID, tableRowCache.get(TableType.DEPARTMENT));
		
		Uri uri = getContentResolver().insert(acrticleBaseUri, values);

		String rowId = uri.getPathSegments().get(1);
		Log.e("insertArticle", "RowId: " + rowId);
		
		tableRowCache.put(TableType.ARTICLE, Integer.parseInt(rowId));
	}	
	
	private int insertEditor(JSONObject editor) {

		ContentValues values = new ContentValues();
		JSONObject photo = editor.optJSONObject("picture");
		Editor edit = new Editor();
		
		edit.setName(editor.optString("name"));
		
		if (photo != null) {
			edit.setPhotoUrl(photo.optString("url"));
		}
		
		values.put(EditorTable.NAME, edit.getName());
		values.put(EditorTable.PHOTO_URL, edit.getPhotoUrl());
		
		Uri uri = getContentResolver().insert(editorBaseUri, values);

		String rowId = uri.getPathSegments().get(1);
		Log.e("insertEditor", "RowId: " + rowId);
		
		return Integer.parseInt(rowId);
	}
	
	private void addCollaborator(int editorId, int articleId) {
		
		ContentValues values = new ContentValues();
		Collaborator collaborator = new Collaborator();
		
		collaborator.setEditorRowId(editorId);
		collaborator.setArticleRowId(articleId);
		
		values.put(CollaboratorTable.EDITOR_ID, collaborator.getEditorRowId());
		values.put(CollaboratorTable.ARTICLE_ID, collaborator.getArticleRowId());
		
		Uri uri = getContentResolver().insert(collaboratorBaseUri, values);

		String rowId = uri.getPathSegments().get(1);
		Log.e("addCollaborator", "RowId: " + rowId);
	}

	// Checks the network state
	public boolean isOnline() {

		ConnectivityManager cm = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}

		return false;
	}
}

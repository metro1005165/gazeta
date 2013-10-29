package com.swengi.ui;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.swengi.core.R;
import com.swengi.db.ArticleTable;
import com.swengi.db.PictureTable;
import com.swengi.db.SwengiContentProvider;
import com.swengi.network.BigPicturesTask;

public class ArticleDetailsFragment extends Fragment {
	
	private Uri uri = SwengiContentProvider.ARTICLES_URI;
	private String filter = null;
	private TextView articleTitle;
	private TextView articleBody;
	private ImageView pic;
	private String link;
	
	public ArticleDetailsFragment() {
		
	}
	
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		getActivity().getSupportLoaderManager().initLoader(1, getArguments(), loader);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        if (container == null) {
            return null;
        }
        
        View view = inflater.inflate(R.layout.fragment_article_details, container, false);
        articleTitle = (TextView) view.findViewById(R.id.article_title);
        articleBody = (TextView) view.findViewById(R.id.article_body_text);
        pic = (ImageView) view.findViewById(R.id.article_picture);
        
        Bundle bundle = getArguments();
        
        if (bundle != null) {
        	filter = bundle.getString("FILTER");
        	if (filter != null) {
        		fillViewsComponents();
        	}
        }
        
        return view;    
    }
    
    private void fillViewsComponents() {
    	Cursor cursor = getActivity().getContentResolver()
				.query(uri, null, filter, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				int titleIndex = cursor.getColumnIndex(ArticleTable.MAIN_HEADER);
				int bodyIndex = cursor.getColumnIndex(ArticleTable.BODY_TEXT);
				String title = cursor.getString(titleIndex);
				String body = cursor.getString(bodyIndex);
				
				articleTitle.setText(title);
				articleBody.setText(Html.fromHtml(body));
			}
		}
    }
    
    private LoaderManager.LoaderCallbacks<Cursor> loader = new LoaderCallbacks<Cursor>() {
		
		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				if (cursor.moveToFirst()) {
					int idIndex = cursor.getColumnIndex(ArticleTable.ID);
					int id = cursor.getInt(idIndex);
					
					String filter = PictureTable.ARTICLE_ID + "=" + id;
					Bundle bundle = new Bundle();
					bundle.putString("FILTER", filter);
					Log.e("onLoadFinished#1", "Filter: " + filter);
					
					getActivity().getSupportLoaderManager().initLoader(2, bundle, loader2);
				}
			}	
		}
		
		@Override
		public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
			
			Bundle bundle = args;
	        
	        if (bundle != null) {
	        	String myFilter = bundle.getString("FILTER");
	        	if (filter != null) {
	        		return new CursorLoader(getActivity(), uri, null, myFilter, null, null);
	        	}
	        }
			return null;
		}
	};
	
	 private LoaderManager.LoaderCallbacks<Cursor> loader2 = new LoaderCallbacks<Cursor>() {
			
			@Override
			public void onLoaderReset(Loader<Cursor> loader) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
				if (cursor != null && cursor.getCount() > 0) {
					if (cursor.moveToFirst()) {
						int linkIndex = cursor.getColumnIndex(PictureTable.URL);
						link = cursor.getString(linkIndex);
						Log.e("onLoadFinished#2", "Link: " + link);
						new BigPicturesTask(getActivity(), pic).execute(link);
					}
				}	
			}
			
			@Override
			public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {
				
				Bundle bundle = args;
		        
		        if (bundle != null) {
		        	String myFilter = bundle.getString("FILTER");
		        	if (filter != null) {
		        		return new CursorLoader(getActivity(), SwengiContentProvider.PICTURES_URI, null, myFilter, null, null);
		        	}
		        }
				return null;
			}
		};
}

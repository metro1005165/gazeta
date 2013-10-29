package com.swengi.ui;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.swengi.db.ArticleTable;
import com.swengi.db.DepartmentTable;
import com.swengi.db.SwengiContentProvider;

public class ArticleListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	
	private final int ARTICLES_LOADER = 0x01;
	
	private ArticleCursorAdapter newsListAdapter;
	private OnHeadlineSelectedListener callBack;

	public ArticleListFragment() {
		
	}
	
    // The container Activity must implement this interface so the fragment can deliver messages
    public interface OnHeadlineSelectedListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onArticleSelected(View v);
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        // Create an empty adapter we will use to display the loaded data.  
        newsListAdapter = new ArticleCursorAdapter(getActivity(), null, true);
        setListAdapter(newsListAdapter);

        getLoaderManager().initLoader(ARTICLES_LOADER,  getArguments(), this);

	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
        	callBack = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		if (callBack != null) {
			// Notify the parent activity of selected item
	        callBack.onArticleSelected(v);
	        Log.e("onListItemClick", "Position = " + position);
	        Log.e("onListItemClick", "Id = " + id);
	        // Set the item as checked to be highlighted when in two-pane layout
	        getListView().setItemChecked(position, true);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		
		Uri uri = SwengiContentProvider.ARTICLES_URI;
		String filter = null;
		int status = args.getInt("STATUS");
		String title = args.getString("TITLE");
		
		if (status >= 0) {
			filter = createFilter(title);
		}
		
		return new CursorLoader(getActivity(), uri, null, filter, null, "date DESC");	
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
		newsListAdapter.swapCursor(cursor);	
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
		newsListAdapter.swapCursor(null);
	}
	
	private String createFilter(String depTitle) {
		String filter = DepartmentTable.TITLE + " LIKE '" + depTitle + "'";
		Cursor cursor = getActivity().getContentResolver()
				.query(SwengiContentProvider.DEPARTMENTS_URI, null, filter,
						null, null);
		if (cursor != null && cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				int idIndex = cursor.getColumnIndex(DepartmentTable.ID);
				int id = cursor.getInt(idIndex);
				
				String retFilter = ArticleTable.DEPARTMENT_ID + "=" + id;
				return retFilter;
			}
		}
		
		return null;
	}
}

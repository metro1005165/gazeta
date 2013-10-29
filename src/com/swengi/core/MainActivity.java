package com.swengi.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.database.Cursor;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.swengi.db.ArticleTable;
import com.swengi.db.SwengiContentProvider;
import com.swengi.ui.ArticleListFragment;
import com.swengi.ui.ArticleListFragment.OnHeadlineSelectedListener;
import com.swengi.ui.DrawerCursorAdapter;

public class MainActivity extends FragmentActivity implements
		OnHeadlineSelectedListener {
	
	private DrawerLayout drawerLayout;
    private ListView drawerList;
    private DrawerCursorAdapter drawerAdapter;
    private ActionBarDrawerToggle drawerToggle;

	// Article index to display when application starts
	//private final int DEFAULT_ARTICLE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		
		drawerAdapter = new DrawerCursorAdapter(this, null, true);
		drawerList.setAdapter(drawerAdapter);
		drawerList.setOnItemClickListener(drawerItemListener);
        getSupportLoaderManager().initLoader(0x02, null, drawerListLoader);
        
     // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.hs_icon, R.string.start_service,
				R.string.start_service) {

					@Override
					public void onDrawerClosed(View drawerView) {
						// something here
					}

					@Override
					public void onDrawerOpened(View drawerView) {
						// something here
					}
			
		};
		
		drawerLayout.setDrawerListener(drawerToggle);

		if (savedInstanceState == null) {
			prepareFragment(null, null, -1, 0);
		}	
	}
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	         // The action bar home/up action should open or close the drawer.
	         // ActionBarDrawerToggle will take care of this.
	        if (drawerToggle.onOptionsItemSelected(item)) {
	            return true;
	        }
	        
	        return false;
	    }

	/* 
	 * This method is called when article title is selected in list view
	 * The parameter is the view of the list item selected
	 * In order to display the correct article in the detail fragment
	 * we need to query it by date filter parameter. Since list displays
	 * articles titles in descending order list view id doesn't match with the
	 * article in the database, hence we really cannot query by ID
	 */
	@Override
	public void onArticleSelected(View listView) {
		
		TextView tvDate = null;
		Date date = null;
		
		Log.e("onArticleSelected", "View = " + listView);
		
		// If list entry view is null we return
		if (listView == null) {
			return;
		}
		
		// View exists lets get the child view we are interested in
		tvDate = (TextView)listView.findViewById(R.id.date);
		
		// If child view is null we return
		if (tvDate == null) {
			return;
		}
			
		try {
			// Trying to convert date of specific format back to milliseconds
			try {
				date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(tvDate.getText().toString());
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		long milliseconds = date.getTime();
		Log.e("onArticleSelected", "milliseconds = " + milliseconds);
		
		// We don't need to create URI for that filter will do the job
		String dateFilter = ArticleTable.DATE + " LIKE '" + milliseconds
				+ "'";
		
		Log.e("onArticleSelected", "dataFilter = " + dateFilter);
		
		Intent intent = new Intent(getBaseContext(), DetailsActivity.class);
		intent.putExtra("FILTER", dateFilter);
		startActivity(intent);
		
//		// Base URI path for querying Article table
//		Uri uri = SwengiContentProvider.ARTICLES_URI;
//		
//		// Get reference to a fragment that is currently in container
//		Fragment fragment = getFragmentManager().findFragmentById(R.id.details);
//		
//		// Make sure we have correct fragment and it is not null
//		if (fragment instanceof ArticleDetailsFragment && fragment != null) {
//			
//			// Assign the correct instance of fragment
//			detailsFragment = (ArticleDetailsFragment) fragment;
//			// Performing db query via content provider
//			Log.e("onArticleSelected", "uri = " + uri);
//	        
//			cursor = getContentResolver().query(uri, null,
//					dateFilter, null, null);
//			
//			Log.e("onArticleSelected", "cursor = " + cursor);
//			Log.e("onArticleSelected", "cursor count = " + cursor.getCount());
//			
//			// Check if query was successful and returned any results
//			if (cursor != null && cursor.getCount() > 0) {
//				// Now we can update the fragment view
//				detailsFragment.updateView(cursor);
//			}
//		}
	}
	
	private LoaderManager.LoaderCallbacks<Cursor> drawerListLoader = new LoaderCallbacks<Cursor>() {
		
		@Override
		public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
			return new CursorLoader(MainActivity.this,
					SwengiContentProvider.DEPARTMENTS_URI, null, null, null,
					null);

		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			// Swap the new cursor in.  (The framework will take care of closing the
	        // old cursor once we return.)
			drawerAdapter.swapCursor(cursor);	
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			// This is called when the last Cursor provided to onLoadFinished()
	        // above is about to be closed.  We need to make sure we are no
	        // longer using it.
			drawerAdapter.swapCursor(null);
		}
	};
	
	private ListView.OnItemClickListener drawerItemListener = new ListView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			prepareFragment(parent, view, position, id);
			
		}
	};
	
	private void prepareFragment(AdapterView<?> parent, View view,
			int position, long id) {	
	
		// update the main content by replacing fragments
        Fragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        if (view != null) {
        	TextView tvDepTitle = (TextView) view.findViewById(R.id.drawer_list_item_text);
        	args.putString("TITLE", tvDepTitle.getText().toString());		
        }
        args.putInt("STATUS", position);
  
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerList);	
	}
}

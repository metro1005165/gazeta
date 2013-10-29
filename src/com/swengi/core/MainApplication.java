package com.swengi.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.swengi.utils.BitmapCache;

// For global data access
public final class MainApplication extends Application {

	private BitmapCache bitmapCache = new BitmapCache();
	public final static String LOADING_ID = "100";
	public final static String NO_IMAGE_ID = "200";

	public BitmapCache getBitmapCache() {
		return bitmapCache;
	}

	public void setBitmapCache(BitmapCache bitmapCache) {
		this.bitmapCache = bitmapCache;
	}

	public static MainApplication getApplicationFromActivity(Activity activity) {
		return ((MainApplication) activity.getApplication());
	}

	public static MainApplication getApplicationFromActivityContext(
			Context context) {
		return getApplicationFromActivity(getActivityFromActivityContext(context));
	}

	private static Activity getActivityFromActivityContext(Context context) {
		return (Activity) context;
	}
}

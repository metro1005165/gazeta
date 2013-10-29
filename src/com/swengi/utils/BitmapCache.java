package com.swengi.utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * @author Yuri
 * Simple cache class aimed for Bitmaps caching based on Google LRU
 */
public class BitmapCache {
	
	private static final String TAG = "BitmapCache";
	// Creates a synchronized LinkedHashMap collection that will be the cache, 
	// Last argument should be true for LRU ordering
	private Map<String, Bitmap> cache = Collections
			.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));
	
	private long size = 0; // Current allocated size
	private long limit = 1000000; // Max memory size in bytes

	public BitmapCache() {
		// Use 25% of available heap size
		setLimit(Runtime.getRuntime().maxMemory() / 4);
	}
    
	public void setLimit(long new_limit) {
		limit = new_limit;
		Log.e(TAG, "BitmapCache will use up to " + limit / 1024. / 1024. + "MB");
	}

	public Bitmap get(String id) {
		try {
			if (!cache.containsKey(id))
				return null;
			// NullPointerException sometimes happen here
			// http://code.google.com/p/osmdroid/issues/detail?id=78
			return cache.get(id);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void put(String id, Bitmap bitmap) {
		try {
			if (cache.containsKey(id))
				// If the key exists it will be replaced with new one but we need to 
				// change the cache size by subtracting the previous value and adding new value size
				size -= getSizeInBytes(cache.get(id));
			cache.put(id, bitmap);
			size += getSizeInBytes(bitmap);
			checkSize();
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}
    
	// Any iterations of synchronized collections should be synchronized
	private synchronized void checkSize() {
		Log.e(TAG, "BitmapCache Size = " + size + " length = " + cache.size());
		if (size > limit) {
			Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();
			// Iterating through the collection and removing entries from start
			// Until the condition is satisfied
			while (iter.hasNext()) {
				Entry<String, Bitmap> entry = iter.next();
				size -= getSizeInBytes(entry.getValue());
				iter.remove();
				if (size <= limit)
					break;
			}
			Log.e(TAG, "Clean cache. New size " + cache.size());
		}
	}

	public void clear() {
		try {
			// NullPointerException sometimes happen here
			// http://code.google.com/p/osmdroid/issues/detail?id=78
			cache.clear();
			size = 0;
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}

	long getSizeInBytes(Bitmap bitmap) {
		if (bitmap == null)
			return 0;
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
	
	public synchronized void readContent() {
		Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Bitmap> entry = iter.next();
			Log.e("readContent", "Key: " + entry.getKey() + " Value: " + entry.getValue());
		}
	}	
}

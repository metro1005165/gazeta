package com.swengi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.swengi.core.MainApplication;
import com.swengi.core.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class ImageLoaderTask extends AsyncTask<String, Void, Void> {
	
	private BitmapCache bitmapCache;
	private Context context;
	
	public ImageLoaderTask(Context context) {
		this.context = context;
		bitmapCache = MainApplication
				.getApplicationFromActivityContext(context).getBitmapCache();
	}

	@Override
	protected Void doInBackground(String... params) {
		cacheAllImages(params[0]);
		return null;
	}

	private void cacheAllImages(String dirName) {
		
		Bitmap bitmap = null;
		String imgId = null;
		
		File cacheDir = context.getDir(dirName, Context.MODE_PRIVATE);
		if (!cacheDir.exists())
			return;
		
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.no_image);
		Bitmap bm1 = BitmapFactory.decodeResource(context.getResources(),
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
}

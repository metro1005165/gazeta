package com.swengi.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {

	private final String IMAGE_WIDTH = "130";
	private final String TYPE = "taysi";
	private final WeakReference<Context> contextRef;
	private String imgId;
	
	public ImageDownloadTask(Context context, String imgId) {
		this.contextRef = new WeakReference<Context>(context);
		this.imgId = imgId;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		if (!isOnline())
			return null;
		Bitmap bitmap = downloadBitmap(convertUrl(params[0]));
		saveBitmapInternal(bitmap);
		return bitmap;
	}

	private String convertUrl(String url) {

		String s1 = url.replaceFirst("\\{[a-zA-z0-9]*\\}", TYPE);
		String s2 = s1.replaceFirst("\\{[a-zA-z0-9]*\\}", IMAGE_WIDTH);

		return s2;
	}

	private Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.e("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			getRequest.abort();
			Log.e("ImageDownloader", e.toString());
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

	private void saveBitmapInternal(Bitmap bitmap) {
		Context context = null;
		File imgDir = null;
		
		if (contextRef != null) {
            context = contextRef.get();
            if (context != null) {
            	imgDir = context.getDir("HS_Images", Context.MODE_PRIVATE);
            }
        }
		
		if (imgDir == null)
			return;
		
		Log.e("[Writing] Internal Directory", "Path: " + imgDir);
		String image = "list_img_" + imgId + ".png";
		File imgFile = new File(imgDir, image);
		Log.e("[Writing] Internal Directory", "Image File: " + imgFile);

		FileOutputStream out;
		try {
			out = new FileOutputStream(imgFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.e("saveBitmapInternal", "Returning...");
	}
	
	// Checks the network state
	public boolean isOnline() {

		Context context = null;

		if (contextRef != null) {
			context = contextRef.get();
			if (context != null) {
				ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = cm.getActiveNetworkInfo();
				if (netInfo != null && netInfo.isConnectedOrConnecting()) {
					return true;
				}
			}
		}
		return false;
	}
}

package com.swengi.network;

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
import android.widget.ImageView;

public class BigPicturesTask extends AsyncTask<String, Void, Bitmap> {

	private final String IMAGE_WIDTH = "800";
	private final String TYPE = "taysi";
	private final WeakReference<Context> contextRef;
	private final WeakReference<ImageView> viewRef;

	public BigPicturesTask(Context context, ImageView picContainer) {
		Log.e("BigPicturesTask", "Start");
		this.contextRef = new WeakReference<Context>(context);
		viewRef = new WeakReference<ImageView>(picContainer);
		Log.e("BigPicturesTask", "Return");
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		if (!isOnline())
			return null;
		Bitmap bitmap = downloadBitmap(convertUrl(params[0]));
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {	
		if (viewRef != null) {
			ImageView picContainer = viewRef.get();
			if (picContainer != null) {
				picContainer.setImageBitmap(result);
			}
		}
	}

	private Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient
				.newInstance("Android");
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
					final Bitmap bitmap = BitmapFactory
							.decodeStream(inputStream);
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

	public boolean isOnline() {

		Context context = null;

		if (contextRef != null) {
			context = contextRef.get();
			if (context != null) {
				ConnectivityManager cm = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = cm.getActiveNetworkInfo();
				if (netInfo != null && netInfo.isConnectedOrConnecting()) {
					return true;
				}
			}
		}
		return false;
	}

	private String convertUrl(String url) {

		String s1 = url.replaceFirst("\\{[a-zA-z0-9]*\\}", TYPE);
		String s2 = s1.replaceFirst("\\{[a-zA-z0-9]*\\}", IMAGE_WIDTH);

		Log.e("convertUrl", s2);
		return s2;
	}

}

package com.swengi.core;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.swengi.network.ArticlesDownloadService;

public class ServiceActivity extends Activity {

	private ProgressDialog dialog;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		/* 
		 * When service is done this method is called
		 */
		@Override
		public void onReceive(Context context, Intent intent) {

			if (dialog != null) {
				dialog.dismiss();	
			}
			//new ImageLoaderTask(context).execute("HS_Images");
			startActivity(new Intent(ServiceActivity.this, MainActivity.class));		
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service);
		
		dialog = new ProgressDialog(this);
		dialog.setTitle("Checking for new articles...");
		dialog.setMessage("Please wait.");
		dialog.setCancelable(false);
		dialog.setIndeterminate(true);
		
		findViewById(R.id.btnGo).setOnClickListener(new OnClickListener() {

			@Override
					public void onClick(View v) {
						Intent intent = new Intent(ServiceActivity.this,
								ArticlesDownloadService.class);
						startService(intent);

						dialog.show();
					}
				});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		registerReceiver(receiver, new IntentFilter(ArticlesDownloadService.NOTIFICATION));
	}
}

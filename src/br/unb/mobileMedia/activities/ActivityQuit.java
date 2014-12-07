package br.unb.mobileMedia.activities;

import android.app.Activity;
import android.os.Bundle;

/**
 * Activity that forcibly quits the application.
 */
public class ActivityQuit extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
}

package br.unb.projetopositivo.mm.socialnetwork;


import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import br.unb.projetopositivo.mm.R;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class ShareButtonActivity extends Activity {
	// SocialAuth Component
	SocialAuthAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Button share = (Button)findViewById(R.id.btn_share);
		share.setText("Share");
		share.setTextColor(Color.WHITE);
		share.setBackgroundResource(R.drawable.button_gradient);

		// Add it to Library
		adapter = new SocialAuthAdapter(new ResponseListener());

		// Add providers
		adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
		adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
		adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);
		adapter.addProvider(Provider.MYSPACE, R.drawable.myspace);
		adapter.enable(share);

	}


	/**
	 * Listens Response from Library
	 * 
	 */

	private final class ResponseListener implements DialogListener 
	{
		public void onComplete(Bundle values) {

			// Variable to receive message status

			Log.d("ShareButton" , "Authentication Successful");

			// Get name of provider after authentication
			String providerName = values.getString(SocialAuthAdapter.PROVIDER);
			Log.d("ShareButton", "Provider Name = " + providerName);

			adapter.updateStatus("SocialAuth Android" + System.currentTimeMillis());
			Toast.makeText(ShareButtonActivity.this, "Message posted on " + providerName, Toast.LENGTH_SHORT).show();		

		}

		public void onError(SocialAuthError error) {
			Log.d("ShareButton" , "Authentication Error");
		}

		public void onCancel() {
			Log.d("ShareButton" , "Authentication Cancelled");
		}

	}
}

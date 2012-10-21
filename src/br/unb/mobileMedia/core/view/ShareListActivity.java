package br.unb.mobileMedia.core.view;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class ShareListActivity extends Activity{

	private SocialAuthAdapter adapter;
	private Button btTopArtists;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(br.unb.mobileMedia.R.layout.activity_share_list);
		
		btTopArtists = (Button)findViewById(br.unb.mobileMedia.R.id.btn_top_artists);
		btTopArtists.setText("Share");
		btTopArtists.setTextColor(Color.WHITE);
		btTopArtists.setBackgroundResource(br.unb.mobileMedia.R.drawable.button_gradient);
		
		Log.d(getLocalClassName(), "ESTOI AKI");
		ResponseListener listener = new ResponseListener();
		adapter = new SocialAuthAdapter(listener);
		Log.d(getLocalClassName(), "QUERENDO-TE");
		
		adapter.addProvider(Provider.FACEBOOK, br.unb.mobileMedia.R.drawable.facebook);
		adapter.addProvider(Provider.TWITTER, br.unb.mobileMedia.R.drawable.twitter);
		
		adapter.enable(btTopArtists);
	}
	
	private final class ResponseListener implements DialogListener {

		public void onCancel() {
			// TODO Auto-generated method stub
			
		}

		public void onComplete(Bundle arg0) {
			adapter.updateStatus("MM UnB Totalmente Funcional via App");
		}

		public void onError(SocialAuthError arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}


package br.unb.mobileMedia.core.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.audioPlayer.AudioPlayerList;
import br.unb.mobileMedia.core.domain.Audio;

public class AudioPlayerActivity extends Activity {

	public static final String EXECUTION_LIST = "EXECUTION_LIST";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_player);

		createUI();
	}

	private void createUI() {
		Parcelable[] ps = getIntent().getParcelableArrayExtra(EXECUTION_LIST);

		Audio[] executionList = new Audio[ps.length];

		for (int i = 0; i < ps.length; i++) {
			executionList[i] = (Audio) ps[i];
		}

		ListView listView = (ListView) findViewById(R.id.list_audio_player);

		listView.setAdapter(new AudioPlayerArrayAdapter(getApplicationContext(), executionList));
		listView.setItemChecked(0, true);

		try {
			AudioPlayerList player = new AudioPlayerList(getApplicationContext(), executionList);
			player.play();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_audio_player, menu);
		return true;
	}
}

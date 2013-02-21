package br.unb.mobileMedia.core.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
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
		Audio[] executionList = refreshAudioList();

		try {
			AudioPlayerList player = AudioPlayerList.getInstance(getApplicationContext());
			if (player.isPlaying()){ 
				player.stop();
				player.killPLaylist();
			}
			player.newPlaylist(executionList);
			player.play();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		findViewById(R.id.img_bt_play_pause).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (AudioPlayerList.getInstance(getApplicationContext()).isPlaying()) {
					AudioPlayerList.getInstance(getApplicationContext()).pause();
				} else {
					AudioPlayerList.getInstance(getApplicationContext()).play();
				}
			}
		});

		findViewById(R.id.img_btn_stop).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AudioPlayerList.getInstance(getApplicationContext()).stop();
			}
		});

		findViewById(R.id.btn_ff).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AudioPlayerList.getInstance(getApplicationContext()).nextTrack();
			}
		});

		findViewById(R.id.btn_re).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AudioPlayerList.getInstance(getApplicationContext()).previousTrack();
			}
		});
		
		findViewById(R.id.btn_add_a_music).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent startActivityIntent = new Intent(getApplicationContext(), AudioSelectActivity.class);
				startActivity(startActivityIntent);
			}
		});
	}

	private Audio[] refreshAudioList() {
		Parcelable[] ps = getIntent().getParcelableArrayExtra(EXECUTION_LIST);

		Audio[] executionList = new Audio[ps.length];

		for (int i = 0; i < ps.length; i++) {
			executionList[i] = (Audio) ps[i];
		}

		ListView listView = (ListView) findViewById(R.id.list_audio_player);

		listView.setAdapter(new AudioPlayerArrayAdapter(getApplicationContext(), executionList));
		listView.setItemChecked(0, true);
		return executionList;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_audio_player, menu);
		return true;
	}
}

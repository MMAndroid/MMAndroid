package br.unb.mobileMedia.core.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.audioPlayer.AudioPlayerList;
import br.unb.mobileMedia.core.domain.Audio;

public class AudioPlayerFragment extends Fragment{

	public static final String EXECUTION_LIST = "EXECUTION_LIST";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setTitle(R.string.title_activity_audio_player);
		return inflater.inflate(R.layout.activity_audio_player, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		createUI();
	}
	
	private void createUI() {
		Audio[] executionList = refreshAudioList();

		try {
			AudioPlayerList player = AudioPlayerList.getInstance(getActivity().getApplicationContext());
			if (player.isPlaying()){ 
				player.stop();
				player.killPLaylist();
			}
			player.newPlaylist(executionList);
			player.play();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		getActivity().findViewById(R.id.img_bt_play_pause).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (AudioPlayerList.getInstance(getActivity().getApplicationContext()).isPlaying()) {
					AudioPlayerList.getInstance(getActivity().getApplicationContext()).pause();
				} else {
					AudioPlayerList.getInstance(getActivity().getApplicationContext()).play();
				}
			}
		});

		getActivity().findViewById(R.id.img_btn_stop).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AudioPlayerList.getInstance(getActivity().getApplicationContext()).stop();
			}
		});

		getActivity().findViewById(R.id.btn_ff).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AudioPlayerList.getInstance(getActivity().getApplicationContext()).nextTrack();
			}
		});

		getActivity().findViewById(R.id.btn_re).setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AudioPlayerList.getInstance(getActivity().getApplicationContext()).previousTrack();
			}
		});
		
		getActivity().findViewById(R.id.btn_add_a_music).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Extract this to a method (repeated in MMUnBActivity too)
				Fragment newFragment = new AudioSelectFragment();
				FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
				if(getActivity().findViewById(R.id.main) != null){
					transaction.replace(R.id.main, newFragment);
					transaction.addToBackStack(null);
				}else{
					transaction.replace(R.id.content, newFragment);
				}
				transaction.commit();
			}
		});
	}

	private Audio[] refreshAudioList() {
		Parcelable[] ps = getActivity().getIntent().getParcelableArrayExtra(EXECUTION_LIST);
		if (ps != null){
			Audio[] executionList = new Audio[ps.length];

			for (int i = 0; i < ps.length; i++) {
				executionList[i] = (Audio) ps[i];
			}

			ListView listView = (ListView) getActivity().findViewById(R.id.list_audio_player);

			listView.setAdapter(new AudioPlayerArrayAdapter(getActivity().getApplicationContext(), executionList));
			listView.setItemChecked(0, true);
			return executionList;
		}
		return new Audio[0];
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getActivity().getMenuInflater().inflate(R.menu.activity_audio_player, menu);
		return true;
	}
}

package br.unb.mobileMedia.core.view;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.audioPlayer.AudioPlayerList;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.playlist.PlayListManager;

public class AudioPlayerFragment extends Fragment implements PlayListManager{

	public static final String EXECUTION_LIST = "EXECUTION_LIST";
	private List<Audio> musicList = new ArrayList<Audio>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("AudioPlayerFragment", "OnCreate...");
		
		initAudioList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("AudioPlayerFragment", "OnCreateView...");
		getActivity().setTitle(R.string.title_activity_audio_player);
		return inflater.inflate(R.layout.activity_audio_player, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i("AudioPlayerFragment", "OnActivityCreated...");
		createUI();
		updateAudioListView();
	}
	
	private void createUI() {
		Log.i("AudioPlayerFragment", "Creating UI...");
		try {
			AudioPlayerList player = AudioPlayerList.getInstance(getActivity().getApplicationContext());
			if (player.isPlaying()){ 
				player.stop();
				player.killPLaylist();
			}
			Audio[] music = musicList.toArray(new Audio[musicList.size()]);
			player.newPlaylist(music);
			player.play();
		} catch (RuntimeException e) {
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
				newFragment.setTargetFragment(AudioPlayerFragment.this, -1);
				
				FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
				if(getActivity().findViewById(R.id.main) != null){
					transaction.replace(R.id.main, newFragment);
					transaction.addToBackStack(null);
				}else{
					transaction.replace(R.id.content, newFragment);
					transaction.addToBackStack(null);
				}
				transaction.commit();
			}
		});
	}
	/**
	 * Initialize the audio list with arguments passed to fragment
	 */
	private void initAudioList() {
		Log.i("AudioPlayerFragment", "init audio list...");
		if(getArguments()==null){
			musicList = new ArrayList<Audio>();
			Log.i("AudioPlayerFragment", "getArguments() is null");
			return;
		}
		
		Parcelable[] ps = getArguments().getParcelableArray(EXECUTION_LIST);
		
		if (ps != null){
			Log.i("AudioPlayerFragment", "getParcelableArray(EXECUTION_LIST) has " + ps.length + " itens");
			musicList = new ArrayList<Audio>();

			for (int i = 0; i < ps.length; i++) {
				musicList.add((Audio) ps[i]);
				Log.i("AudioPlayerFragment", "Add music to musicList: " + (Audio) ps[i]);
			}
		}
	}
	
	/**
	 * Update the list view with audio list
	 */
	private void updateAudioListView(){
		Log.i("AudioPlayerFragment", "Updateding audio list view...");
		ListView listView = (ListView) getActivity().findViewById(R.id.list_audio_player);
		Audio[] music = musicList.toArray(new Audio[musicList.size()]);
		
		listView.setAdapter(new AudioPlayerArrayAdapter(getActivity().getApplicationContext(), music));
		listView.setItemChecked(0, true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.i("AudioPlayerFragment", "OnCreateOptionsMenu...");
		inflater.inflate(R.menu.activity_audio_player, menu);
	}
	
	/**
	 * Add a audio to audio list
	 * Doesn't updated the list view
	 */
	public void addMusic(Audio audio) {
		// TODO Auto-generated method stub
		Log.i("AudioPlayerFragment", "Adding music..." + audio);
		musicList.add(audio);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		AudioPlayerList player = AudioPlayerList.getInstance(getActivity().getApplicationContext());
		player.stop();
		player.killPLaylist();
	}
	
}

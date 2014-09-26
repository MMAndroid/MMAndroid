package br.unb.mobileMedia.core.view;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View.OnTouchListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.FileChooser.*;
import br.unb.mobileMedia.core.audioPlayer.AudioPlayerList;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.playlist.PlayListManager;

public class AudioPlayerFragment extends Fragment implements PlayListManager, SeekBar.OnSeekBarChangeListener{

	MediaPlayer mp;

	public static final String EXECUTION_LIST = "EXECUTION_LIST";
	private List<Audio> musicList = new ArrayList<Audio>();

	private AudioPlayerList player;
	private Audio[] music;

	private ImageButton btnPlayPause;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private SeekBar songProgressBar;

	private Handler mHandler = new Handler();
	private Utilities utils;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Log.i("AudioPlayerFragment", "OnCreate...");
		initAudioList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_audio_player, container, false);

		Log.i("AudioPlayerFragment", "OnCreateView...");

		// Song title
		songTitleLabel = (TextView) v.findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) v.findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) v.findViewById(R.id.songTotalDurationLabel);

		// All player buttons
		btnPlayPause = (ImageButton) v.findViewById(R.id.btnPlayPause);
		btnPlayPause = (ImageButton) v.findViewById(R.id.btnPlayPause);
		btnNext = (ImageButton) v.findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) v.findViewById(R.id.btnPrevious);
		btnRepeat = (ImageButton) v.findViewById(R.id.btnRepeat);
		btnShuffle = (ImageButton) v.findViewById(R.id.btnShuffle);
		songProgressBar = (SeekBar) v.findViewById(R.id.songProgressBar);

		player = AudioPlayerList.getInstance(getActivity().getApplicationContext());
		utils = new Utilities();
		songProgressBar.setOnSeekBarChangeListener(this);

		// set Progress bar values
		songProgressBar.setProgress(0);
		songProgressBar.setMax(100);
	
		getActivity().setTitle(R.string.title_activity_audio_player);

		return v;
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
		
		music = musicList.toArray(new Audio[musicList.size()]);
		
		try {			
			
			player.reset();
            btnPlayPause.setImageResource(R.drawable.btn_pause);

			player.newPlaylist(music);
			player.play();

			if(player.isPlaying()){
				songTitleLabel.setText(musicList.get(player.current()).getTitle().toString());
				updateProgressBar();
			}
		    
		}catch (RuntimeException e){
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		}


		btnPlayPause.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// check for already playing
                if(player.isPlaying()){
                    if(player!=null){
                        player.pause();
                        btnPlayPause.setImageResource(R.drawable.btn_play);
                    }
                }else{
                    // Resume song
                    if(player!=null){
                        player.play();
                        btnPlayPause.setImageResource(R.drawable.btn_pause);
                    }
                }

			}
		});

		btnNext.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                player.nextTrack();
//				AudioPlayerList.getInstance(getActivity().getApplicationContext()).nextTrack();
			}
		});

		btnPrevious.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				player.previousTrack();
//				AudioPlayerList.getInstance(getActivity().getApplicationContext()).previousTrack();
 			}
		});
		
		
		/**
		 * Button Click event for Repeat button
		 * Enables repeat flag to true
		 * */
		btnRepeat.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View arg0) {
				if(player.isRepeat()){
					player.setRepeat(false);
//					Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}else{
					// make repeat to true
					player.setRepeat(true);
//					Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
					// make shuffle to false
					player.setShuffle(false);
					btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}	
			}
		});
		
		/**
		 * Button Click event for Shuffle button
		 * Enables shuffle flag to true
		 * */
		btnShuffle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				if(player.isShuffle()){
					player.setShuffle(false);
//					Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}else{
					// make repeat to true
					player.setShuffle(true);
//					Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
					// make shuffle to false
					player.setRepeat(false);
					btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}	
			}
		});
		
		
		
		//Click no botao para adicionar uma musica Chamando o FileChooser
		getActivity().findViewById(R.id.btnPlaylist).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Extract this to a method (repeated in MMUnBActivity too)				
				Fragment newFragment =  new FileChooserFragment(); //AudioSelectFragment();
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

	public synchronized void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	/*
	 * Background Runnable thread
	 */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {		
			
			if(player.isPlayer()){
				long totalDuration = player.getDuration();
				long currentDuration = player.getCurrentPosition();
			
				songTotalDurationLabel.setText(""+ utils.milliSecondsToTimer(totalDuration));
				songCurrentDurationLabel.setText(""+ utils.milliSecondsToTimer(currentDuration));
	
				int progress = (int) (utils.getProgressPercentage(currentDuration,totalDuration));
//				// Log.d("Progress", ""+progress);
				songProgressBar.setProgress(progress);
				
			}else{
				Log.i("Player", "not isPlayer()");
				player.nextTrack();
			}
			
			mHandler.postDelayed(this, 100);
		}
		
		
	};

	
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		
		mHandler.removeCallbacks(mUpdateTimeTask);
		
		int totalDuration = player.getDuration();
		
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

		// forward or backward to certain seconds
		player.seekTo(currentPosition);
		
		// update timer progress again
		updateProgressBar();

	}

	/**
	 * Initialize the audio list with arguments passed to fragment
	 */
	private void initAudioList() {
		Log.i("AudioPlayerFragment", "init audio list...");

		if (getArguments() == null) {
			musicList = new ArrayList<Audio>();
			Log.i("AudioPlayerFragment", "getArguments() is null");
			return;
		}

		Parcelable[] ps = getArguments().getParcelableArray(EXECUTION_LIST);

		if (ps != null) {
			Log.i("AudioPlayerFragment", "getParcelableArray(EXECUTION_LIST) has " + ps.length + " itens");
			musicList = new ArrayList<Audio>();

			for (int i = 0; i < ps.length; i++) {
				musicList.add((Audio) ps[i]);
				Log.i("AudioPlayerFragment", "Add music to musicList: "	+ (Audio) ps[i]);
			}
		}
	}

	/**
	 * Update the list view with audio list
	 */
	private void updateAudioListView() {
		Log.i("AudioPlayerFragment", "Updateding audio list view...");
		ListView listView = (ListView) getActivity().findViewById(R.id.list_audio_player);
		music = musicList.toArray(new Audio[musicList.size()]);

		listView.setAdapter(new AudioPlayerArrayAdapter(getActivity().getApplicationContext(), music));
		listView.setItemChecked(0, true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.i("AudioPlayerFragment", "OnCreateOptionsMenu...");
		inflater.inflate(R.menu.activity_audio_player, menu);
	}

	/**
	 * Add a audio to audio list Doesn't updated the list view
	 */
	public void addMusic(Audio audio) {
		// TODO Auto-generated method stub
		Log.i("AudioPlayerFragment", "Adding music..." + audio);
		musicList.add(audio);
	}

	/**
	 * Receive the files selecetds in FileChooser
	 */
	public void receiveFileChooser(ArrayList<FileDetail> files) {

		Toast.makeText(getActivity(), "AudioPlayerFragment Receive " + files.size()	+ " Files of FileChooser", Toast.LENGTH_SHORT).show();
		String url = null;
		for (int i = 0; i < files.size(); i++) {
			try {
				url = files.get(i).getPath().toString();
				musicList.add(new Audio(files.get(i).getName().toString(), new URI(url)));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				Log.i("Exception", "receiveFileChooser()");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		player.stop();
		player.killPLaylist();
	}

}

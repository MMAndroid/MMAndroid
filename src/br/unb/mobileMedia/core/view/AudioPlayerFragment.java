package br.unb.mobileMedia.core.view;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.FileChooser.FileDetail;
import br.unb.mobileMedia.core.audioPlayer.AudioPlayerList;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.core.extractor.MediaExtractor;
import br.unb.mobileMedia.playlist.PlayListManager;
import br.unb.mobileMedia.util.AudioToParcelable;

public class AudioPlayerFragment extends Fragment implements PlayListManager,
		SeekBar.OnSeekBarChangeListener {

	public static final String EXECUTION_LIST = "EXECUTION_LIST";

	private AudioPlayerList player;
	private List<Audio> musicList = new ArrayList<Audio>();

	private ImageView   albumArt;
	private ImageButton btnPlayPause;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private SeekBar songProgressBar;

	private Handler mHandler = new Handler();
	private Utilities utils;
	private TextView songTitleLabel;
	private TextView author;
	private TextView genre;
	private TextView album;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	private MediaExtractor audioExtractor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("AudioPlayerFragment", "OnCreate...");
		initAudioList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_audio_player, container,false);

		Log.i("AudioPlayerFragment", "OnCreateView...");
		
		this.audioExtractor = new DefaultAudioExtractor(getActivity().getApplicationContext());

		
		this.author = (TextView) v.findViewById(R.id.author);
		this.album = (TextView) v.findViewById(R.id.album);
		this.genre = (TextView) v.findViewById(R.id.genre);
		
		//albumArt
		albumArt = (ImageView) v.findViewById(R.id.albumArt);
		
		// Song title
		songTitleLabel = (TextView) v.findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) v.findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) v.findViewById(R.id.songTotalDurationLabel);

		// All player buttons
		btnPlayPause = (ImageButton) v.findViewById(R.id.btnPlayPause);
		btnNext = (ImageButton) v.findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) v.findViewById(R.id.btnPrevious);
		btnRepeat = (ImageButton) v.findViewById(R.id.btnRepeat);
		btnShuffle = (ImageButton) v.findViewById(R.id.btnShuffle);
		songProgressBar = (SeekBar) v.findViewById(R.id.songProgressBar);
		
		
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
		try {
			
			player = AudioPlayerList.getInstance(getActivity().getApplicationContext());
			
			if (musicList.size() > 0) {
				player.newPlaylist(musicList);
				player.play(0);
			} else {
				player.stop();
			}

			if (player.isPlaying()) {
				btnPlayPause.setImageResource(R.drawable.btn_pause);
			} else {
				btnPlayPause.setImageResource(R.drawable.btn_play);
			}

			updateProgressBar();

		} catch (RuntimeException e) {
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(),
					"Exception: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT)
					.show();
		}
		
		btnPlayPause.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				player.playPause();
				if (player.isPlaying()) {
					btnPlayPause.setImageResource(R.drawable.btn_pause);
				} else {
					btnPlayPause.setImageResource(R.drawable.btn_play);
				}
			}
		});
		
		btnNext.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				player.nextTrack();
			}
		});
		
		btnPrevious.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				player.previousTrack();
			}
		});
		
		/**
		 * Button Click event for Repeat button Enables repeat flag to true
		 * */
		btnRepeat.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				if (player.isRepeat()) {
					player.setRepeat(false);
					// Toast.makeText(getApplicationContext(), "Repeat is OFF",
					// Toast.LENGTH_SHORT).show();
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				} else {
					player.setRepeat(true);
					player.setShuffle(false);
					btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}
			}
		});
		/**
		 * Button Click event for Shuffle button Enables shuffle flag to true
		 * */
		btnShuffle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				if (player.isShuffle()) {
					player.setShuffle(false);
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				} else {
					player.setShuffle(true);
					player.setRepeat(false);
					btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}
			}
		});

		// Click no botao para adicionar uma musica Chamando o FileChooser
//		getActivity().findViewById(R.id.btnPlaylist).setOnClickListener(
//			new OnClickListener() {
//				public void onClick(View v) {
//					// TODO Extract this to a method (repeated in
//					// MMUnBActivity too)
//					Fragment newFragment = new FileChooserFragment(); // AudioSelectFragment();
//					newFragment.setTargetFragment(AudioPlayerFragment.this,-1);
//					FragmentTransaction transaction = getActivity()
//							.getSupportFragmentManager().beginTransaction();
//					if (getActivity().findViewById(R.id.main) != null) {
//						transaction.replace(R.id.main, newFragment);
//						transaction.addToBackStack(null);
//					} else {
//						transaction.replace(R.id.content, newFragment);
//						transaction.addToBackStack(null);
//					}
//					transaction.commit();
//			}
//		});
//		
	}
	
		
	
	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}
	
	/*
	* Background Runnable thread
	*/
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {	
			if(player.isPlaying()){
				
				long totalDuration = player.getDuration();
				long currentDuration = player.getCurrentPosition();
				songTotalDurationLabel.setText(""+ utils.milliSecondsToTimer(totalDuration));
				songCurrentDurationLabel.setText(""+ utils.milliSecondsToTimer(currentDuration));
				songTitleLabel.setText(player.getTitleSong());
				
				author.setText(player.getAuthor());
				genre.setText(player.getGenre());
				album.setText(player.getAlbum());
				
				albumArt.setImageBitmap(player.getAlbumArt());
				int progress = (int) (utils.getProgressPercentage(currentDuration,totalDuration));
				// Log.d("Progress", ""+progress);
				songProgressBar.setProgress(progress);
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
		
		
		AudioToParcelable[] ps = (AudioToParcelable[]) getArguments().getParcelableArray(EXECUTION_LIST);
		
		if (ps != null) {
			
			Log.i("AudioPlayerFragment","getParcelableArray(EXECUTION_LIST) has " + ps.length+ " itens");
			
			musicList = new ArrayList<Audio>();
			
			for (int i = 0; i < ps.length; i++) {
				musicList.add((Audio) ps[i].getAudio());
				Log.i("AudioPlayerFragment", "Add music to musicList: "	+  ps[i].getAudio().getUrl());
			}
		}
	}


	/**
	 * Update the list view with audio list
	 */
	private void updateAudioListView(){
		Log.i("AudioPlayerFragment", "Updateding audio list view...");
//		ListView listView = (ListView) getActivity().findViewById(R.id.list_audio_player);
		Audio[] music = musicList.toArray(new Audio[musicList.size()]);
//		listView.setAdapter(new AudioPlayerArrayAdapter(getActivity().getApplicationContext(), music));
//		listView.setItemChecked(0, true);
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



	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		player = AudioPlayerList.getInstance(getActivity().getApplicationContext());
		player.stop();
		player.killPLaylist();
	}
}
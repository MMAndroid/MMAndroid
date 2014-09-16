package br.unb.mobileMedia.core.view;

import java.net.URI;
import java.net.URISyntaxException;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.FileChooser.*;
import br.unb.mobileMedia.core.audioPlayer.AudioPlayerList;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.playlist.PlayListManager;

public class AudioPlayerFragment extends Fragment implements PlayListManager{

	public static final String EXECUTION_LIST = "EXECUTION_LIST";
	private List<Audio> musicList = new ArrayList<Audio>();
	
	private ImageButton btnPlayPause;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("AudioPlayerFragment", "OnCreate...");
		
		initAudioList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.activity_audio_player, container, false);
		
		Log.i("AudioPlayerFragment", "OnCreateView...");

		// All player buttons
		//Ao inves de criar instancias o botoes em Oncreate
		// fiz em onCreateView pois os botoes no existem no neste fragment e nao
		// na activity principal
		btnPlayPause = (ImageButton) v.findViewById(R.id.btnPlayPause);
		btnPlayPause = (ImageButton) v.findViewById(R.id.btnPlayPause);
        btnForward   = (ImageButton) v.findViewById(R.id.btnForward);
        btnBackward  = (ImageButton) v.findViewById(R.id.btnBackward);
        btnNext      = (ImageButton) v.findViewById(R.id.btnNext);
        btnPrevious  = (ImageButton) v.findViewById(R.id.btnPrevious);
        
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
			AudioPlayerList player = AudioPlayerList.getInstance(getActivity().getApplicationContext());
			if (player.isPlaying()){
				player.stop();
				player.killPLaylist();
			}
			Audio[] music = musicList.toArray(new Audio[musicList.size()]);
			player.newPlaylist(music);
			player.play();
			
			// Verifica no inicio da activity se existe 
			//alguma musica tocando se existir o btnPlayPause vai exibir a imagem de pause.
			if (AudioPlayerList.getInstance(getActivity().getApplicationContext()).isPlaying()) {
				btnPlayPause.setImageResource(R.drawable.btn_pause);
			}
            
		} catch (RuntimeException e) {
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
		}

		btnPlayPause.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (AudioPlayerList.getInstance(getActivity().getApplicationContext()).isPlaying()) {
					
					if(AudioPlayerList.getInstance(getActivity().getApplicationContext())!=null){
						AudioPlayerList.getInstance(getActivity().getApplicationContext()).pause();
						btnPlayPause.setImageResource(R.drawable.btn_play);
					}
					
//					AudioPlayerList.getInstance(getActivity().getApplicationContext()).pause();
				} else {
					if(AudioPlayerList.getInstance(getActivity().getApplicationContext())!=null){
						AudioPlayerList.getInstance(getActivity().getApplicationContext()).play();
						btnPlayPause.setImageResource(R.drawable.btn_pause);
					}
//					AudioPlayerList.getInstance(getActivity().getApplicationContext()).play();
				}
			}
		});

//		getActivity().findViewById(R.id.img_btn_stop).setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				AudioPlayerList.getInstance(getActivity().getApplicationContext()).stop();
//			}
//		});

		btnForward.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AudioPlayerList.getInstance(getActivity().getApplicationContext()).nextTrack();
			}
		});

		btnBackward.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AudioPlayerList.getInstance(getActivity().getApplicationContext()).previousTrack();
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
		
//		getActivity().findViewById(R.id.btn_file_chooser).setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v){
//				Fragment newFragment = new FileChooserFragment();
//				
//				FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//				if(getActivity().findViewById(R.id.main) != null){
//					transaction.replace(R.id.main, newFragment);
//					transaction.addToBackStack(null);
//				}else{
//					transaction.replace(R.id.content, newFragment);
//					transaction.addToBackStack(null);
//				}
//				transaction.commit();
//			}
//		});

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
	
	
	
	/**
	 * Receive the files selecetds in FileChooser 
	 */
	public void receiveFileChooser(ArrayList<FileDetail> files){

		//TODO colocar para executar as musicas recebidas.
		
		Toast.makeText(getActivity(), "AudioPlayerFragment Receive " + files.size()+ " Files of FileChooser" , Toast.LENGTH_SHORT).show();
//		URI path = null;
//		
//		for(int i = 0; i< files.size(); i++){
//			
//			try {
//				path = new URI(files.get(i).getPath().toString());
//			} catch (URISyntaxException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			musicList.add(new Audio(i, files.get(i).getName().toString(), path ));
//		}
		
		
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

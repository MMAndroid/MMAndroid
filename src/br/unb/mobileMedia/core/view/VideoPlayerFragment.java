package br.unb.mobileMedia.core.view;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.FileChooser.*;
import br.unb.mobileMedia.core.videoPlayer.VideoPlayerList;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.db.DefaultVideoListDAO;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Video;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.playlist.PlayListManager;
import br.unb.mobileMedia.util.ListAllFiles;

public class VideoPlayerFragment extends Fragment implements PlayListManager{

	public static final String EXECUTION_LIST = "EXECUTION_LIST";
	private List<Video> videoList = new ArrayList<Video>();

	private VideoPlayerList player;

	private ImageButton btnPlayPause;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	
	private int rodando;
	private int indexActualVideo = 0;
	private int indexVideoWant;
	private int position = 0;

	
	private VideoView mVideoView;
	private AlertDialog alerta;
	
	private Utilities utils;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		
		try {
			initAudioList();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_video_player, container, false);
		
		rodando = 0;

		// All player buttons
		btnPlayPause = (ImageButton) v.findViewById(R.id.btnPlayPause);
		btnNext = (ImageButton) v.findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) v.findViewById(R.id.btnPrevious);
		
		mVideoView = (VideoView) v.findViewById(R.id.surface_view);
		
		player = new VideoPlayerList();
		
		
		//Possibilidade de sincronizar videos
		DefaultVideoListDAO countVideo = new DefaultVideoListDAO(getActivity().getApplicationContext());
		
		int countVideoInt = 0;
		
		
		try {
			countVideoInt = countVideo.countListVideoBanco();
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//if bank is void, sync all files
		if(countVideoInt == 0){
			
			//Alert
			mensagemSincronizarVazio();
			
		}
		
		utils = new Utilities();

		return v;
	}
	
	
	private void mensagemSincronizarVazio() { 
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle("Você não tem vídeos para executar."); 
		
		builder.setMessage("Gostaria de sincronizar seus vídeos ?"); 
		builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() { 
			
			public void onClick(DialogInterface arg0, int arg1) { 
				try {
					player.sincronizarTodosVideos();
				} catch (DBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
		}); 
		
		builder.setNegativeButton("Não", new DialogInterface.OnClickListener() { 
			
			public void onClick(DialogInterface arg0, int arg1) { 
				//Não quer sincronizar
			}
		}); 
		
		alerta = builder.create(); 
		alerta.show();
		
		
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.i("AudioPlayerFragment", "OnActivityCreated...");
		try {
			createUI();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//updateAudioListView();

	}
	
	public void runVideo(String path){
		
		if(path != null){
			
			mVideoView.setVideoPath(path);
			mVideoView.start();
			mVideoView.requestFocus();
		
		}else{
			
			Toast.makeText(getActivity().getApplicationContext(), "Infelizmente você não tem vídeos para executar. ", Toast.LENGTH_SHORT).show();
			
		}
		
	}

	
	public void playPause(){
		
		
		if(rodando == 1){ //apertou o pause
			
			
			mVideoView.pause();
			position = mVideoView.getCurrentPosition();
			btnPlayPause.setImageResource(R.drawable.btn_play_old);
			
			rodando = 0;
						
		}else{ //apertou o play
			
			
			String videoPath = player.getVideo(indexActualVideo);
			
			if(position == 0){
				
				runVideo(videoPath);
				
			}else{
				
				mVideoView.seekTo(position);
				mVideoView.start();
				position = 0;
				
				
			}
			
			btnPlayPause.setImageResource(R.drawable.btn_pause_old);
			
			rodando = 1;
		
		}
		
		
	}

	private void createUI() throws UnsupportedEncodingException, DBException, URISyntaxException {
		
		player = VideoPlayerList.getInstance(getActivity().getApplicationContext());

		btnPlayPause.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				playPause();				
					
			}
			
		});
		
		
		btnNext.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				indexVideoWant++;
				String videoPath = player.getVideo(indexVideoWant);
				
				if(videoPath != null){
					
					btnPlayPause.setImageResource(R.drawable.btn_pause_old);
					runVideo(videoPath);
					indexActualVideo = indexVideoWant;
					
				}else{
					
					indexVideoWant = indexActualVideo;
					Toast.makeText(getActivity().getApplicationContext(), "Fim dos Vídeos. ", Toast.LENGTH_SHORT).show();
					
				}
				
					
			}
			
		});
		
		
		btnPrevious.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				indexVideoWant--;
				
				if(indexVideoWant>=0){
					String videoPath = player.getVideo(indexVideoWant);
					
					if(videoPath != null){
						
						btnPlayPause.setImageResource(R.drawable.btn_pause_old);
						runVideo(videoPath);
						
						indexActualVideo = indexVideoWant;
						
					}else{
						
						indexVideoWant = indexActualVideo;
						Toast.makeText(getActivity().getApplicationContext(), "Fim dos Vídeos. ", Toast.LENGTH_SHORT).show();
						
					}
				}else{
					
					Toast.makeText(getActivity().getApplicationContext(), "Fim dos Vídeos. ", Toast.LENGTH_SHORT).show();
				}
					
			}
			
		});
		
	}

	
	/**
	 * Initialize the audio list with arguments of the bank
	 */

	
	private void initAudioList() throws UnsupportedEncodingException, DBException, URISyntaxException{
		
		DefaultVideoListDAO videosBank = new DefaultVideoListDAO(getActivity().getApplicationContext());
		
		videoList = videosBank.getVideosBanco();		
		
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.i("AudioPlayerFragment", "OnCreateOptionsMenu...");
		inflater.inflate(R.menu.activity_audio_player, menu);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	public void addMusic(Audio audio) {
		// TODO Auto-generated method stub
		
	}


}

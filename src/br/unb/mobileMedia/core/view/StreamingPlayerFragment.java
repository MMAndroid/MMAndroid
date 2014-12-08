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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.FileChooser.*;
import br.unb.mobileMedia.core.videoPlayer.VideoPlayerList;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.db.DefaultAudioListDAO;
import br.unb.mobileMedia.core.db.DefaultAuthorDAO;
import br.unb.mobileMedia.core.db.DefaultVideoListDAO;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Video;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.playlist.PlayListManager;
import br.unb.mobileMedia.util.ListAllFiles;

public class StreamingPlayerFragment extends Fragment implements PlayListManager{


	private VideoPlayerList player;

	private ImageButton btnPlayPause;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	
	EditText bSearch;
	
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
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_streaming_player, container, false);
		
		rodando = 0;

		// All player buttons
		btnPlayPause = (ImageButton) v.findViewById(R.id.btnPlayPause);
		btnNext = (ImageButton) v.findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) v.findViewById(R.id.btnPrevious);
		
		mVideoView = (VideoView) v.findViewById(R.id.surface_view);
		
		bSearch   = (EditText) v.findViewById(R.id.search);
		
		
		utils = new Utilities();

		return v;
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
	
	public void runStreaming(String path){
		
		if(rodando == 1){
			
			mVideoView.pause();
			btnPlayPause.setImageResource(R.drawable.btn_play);
			rodando = 0;
			
			return;
		}
		
		if(path != null){
			
			mVideoView.setVideoPath(path);
			mVideoView.start();
			mVideoView.requestFocus();
			btnPlayPause.setImageResource(R.drawable.btn_pause);
			
			rodando = 1;
		
		}else{
			
			Toast.makeText(getActivity().getApplicationContext(), "Infelizmente você não tem vídeos para executar. ", Toast.LENGTH_SHORT).show();
			
		}
		
	}

	

	private void createUI() throws UnsupportedEncodingException, DBException, URISyntaxException {
		
		player = VideoPlayerList.getInstance(getActivity().getApplicationContext());

		btnPlayPause.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				
				if(bSearch.getText().toString().length() > 10){
					
					runStreaming(bSearch.getText().toString());
					
				}else{
					
					
					Toast.makeText(getActivity().getApplicationContext(), "URL Inválida, informe outra.", Toast.LENGTH_SHORT).show();

				}
					
			}
			
		});
		
		
		
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

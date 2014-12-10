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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.FileChooser.*;
import br.unb.mobileMedia.core.videoPlayer.VideoPlayerList;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.db.DefaultStreamingListDAO;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Video;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.playlist.PlayListManager;
import br.unb.mobileMedia.util.ListAllFiles;

public class StreamingPlayerFragment extends Fragment implements PlayListManager{


	private VideoPlayerList player;

	private ImageButton btnPlayPause;
	private ImageButton btnList;
	private ImageButton btnNew;
	
	EditText bSearch;
	
	private int rodando;
	private int indexActualVideo = 0;
	private int indexVideoWant;
	private int position = 0;

	
	private VideoView mVideoView;
	
	String radio = "";
	
	private Utilities utils;
		

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		mVideoView = new VideoView(getActivity());
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_streaming_player, container, false);
		
		rodando = 0;

		// All player buttons
		btnPlayPause = (ImageButton) v.findViewById(R.id.btnPlayPause);
		btnList = (ImageButton) v.findViewById(R.id.btnList);
		btnNew = (ImageButton) v.findViewById(R.id.btnNew);
		
		mVideoView = (VideoView) v.findViewById(R.id.surface_view);
		
		bSearch   = (EditText) v.findViewById(R.id.search);
				
		utils = new Utilities();

		return v;
	}
	
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

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
			btnPlayPause.setImageResource(R.drawable.btn_play_old);
			rodando = 0;
			
			return;
		}
		
		
		if(path != null){

			mVideoView.setVideoPath(path);
			mVideoView.start();
			mVideoView.requestFocus();
			btnPlayPause.setImageResource(R.drawable.btn_pause_old);
			
			rodando = 1;
		
		}else{
			
			Toast.makeText(getActivity().getApplicationContext(), "Infelizmente você não tem vídeos para executar. ", Toast.LENGTH_SHORT).show();
			
		}
		
	}

	

	private void createUI() throws UnsupportedEncodingException, DBException, URISyntaxException {
		
		player = VideoPlayerList.getInstance(getActivity().getApplicationContext());

		btnPlayPause.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				
				if(radio.length() > 10){
					
					runStreaming(radio);
					
				}else{
					
					Toast.makeText(getActivity().getApplicationContext(), "URL Inválida, informe outra.", Toast.LENGTH_SHORT).show();

				}
					
			}
			
		});
		
		
		btnList.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
	
				try {
					showListRadios();
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
			
		});
		
		btnNew.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				showInputNewRadio();
	
			}
			
		});
		
		
		
	}
	
	public void showInputNewRadio(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		Button add;
				
		builder.setTitle("Adicionar Rádio");
		
		final FrameLayout frameView = new FrameLayout(getActivity());
		builder.setView(frameView);
		
		final AlertDialog alertDialog = builder.create();

		LayoutInflater inflater = alertDialog.getLayoutInflater();
		
		final View dialoglayout = inflater.inflate(R.layout.input_new_radio, frameView);
		
		add = (Button) dialoglayout.findViewById(R.id.buttonSubmitNewRadio);
		
		add.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				EditText radioAdress = (EditText) dialoglayout.findViewById(R.id.adressRadio);
				String valueRadioAdress = radioAdress.getText().toString();
				
				EditText radioName = (EditText) dialoglayout.findViewById(R.id.nameRadio);
				String valueRadioName = radioName.getText().toString();
				
				adicionaNovaRadio(valueRadioName, valueRadioAdress);
				
				alertDialog.cancel();
	
			}
			
		});
		
		alertDialog.show(); 
		
	}
	
	public void adicionaNovaRadio(String name, String adress){
		
		long pk;
		
		if(name.trim().length() > 1 && adress.trim().length() > 6){
			
			DefaultStreamingListDAO addRadio = new DefaultStreamingListDAO(getActivity().getApplicationContext());

			try {
				
				pk = addRadio.addRadio(name,adress);
				
				if(pk > 0){
				
					Toast.makeText(getActivity().getApplicationContext(), "Rádio cadastrada com sucesso.", Toast.LENGTH_SHORT).show();
				}
				
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}else{
			
			Toast.makeText(getActivity().getApplicationContext(), "DADOS Inválidos, informe novamente.", Toast.LENGTH_SHORT).show();
			
		}
		
		
	}
	
	public String getAdressRadio(String name) throws UnsupportedEncodingException, DBException, URISyntaxException{
		
		DefaultStreamingListDAO getRadios = new DefaultStreamingListDAO(getActivity().getApplicationContext());
		
		String adress = getRadios.getAdressStreamingBanco(name);
		
		return adress;
		
	}
	
	public String getAdressString(String[] all, int id){
		
		String[] separe = new String[2];
		separe = all[id].split("00000");
		
		return separe[1];
	}
	
	public void showListRadios() throws UnsupportedEncodingException, DBException, URISyntaxException{

		DefaultStreamingListDAO getRadios = new DefaultStreamingListDAO(getActivity().getApplicationContext());
				
		final String[] radios = getRadios.getStreamingsBanco();
		
		
		String[] radiosName = new String[radios.length];
		
		String[] separe = new String[2];
		
		for(int i=0; i<radios.length; i++){
			
			separe = radios[i].split("00000");
			radiosName[i] = separe[0];
			
		}
				

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle("Selecione a Radio");
		builder.setItems(radiosName, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    	
		    	radio = getAdressString(radios,which);
		    	rodando = 0;
		    			    	
		    	runStreaming(getAdressString(radios,which));
		    	
		    }
		});
		builder.show();
		
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

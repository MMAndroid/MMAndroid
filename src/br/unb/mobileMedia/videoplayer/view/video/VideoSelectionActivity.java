package br.unb.mobileMedia.videoplayer.view.video;

import java.io.File;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.util.MMConstants;


public class VideoSelectionActivity extends Activity {

	
	private ServiceConnection connection = null;
	private File file = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.video_player_activity);
		this.configureActivity();
	}

	private void configureActivity() {
		this.configureUI();
		this.configureService();
	}

	@SuppressWarnings("unused")
	private void startPlayerService() {
		Intent startPlayerServiceIntent = new Intent();
		startService(startPlayerServiceIntent);
		bindService(startPlayerServiceIntent, connection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * Para fazer uma exibi��o de v�deo em background
	 */
	private void configureService() {
		this.connection = new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
				
			}
			public void onServiceConnected(ComponentName name, IBinder service) {

			}
		};
	}

	private void configureUI() {

		if(getIntent().hasExtra(VideoListActivity.FILE_OBJ_PARAMETER)){
			this.file = (File) getIntent().getExtras().get(VideoListActivity.FILE_OBJ_PARAMETER);
		}
		
		
		((ImageButton)findViewById(R.id.btn_start)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				Log.i(MMConstants.TAG,"Caminho para o SDCard:" + Environment.getExternalStorageDirectory().getPath());

				VideoView videoView = (VideoView) findViewById(R.id.vv_video);
				MediaController mediaController = new MediaController(VideoSelectionActivity.this);
				mediaController.setAnchorView(videoView);

//				Recupera um v�deo local da aplica��o
//				Colocar um arquivo video.mp4, por exemplo, na pasta raw
//				videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() +"/"+R.raw.video));
				
				if(file!=null){
					if(file.exists()){
						Uri imageUri = Uri.fromFile(file);
						videoView.setVideoURI(imageUri);
						videoView.setMediaController(mediaController);
						videoView.start();
						Log.i(MMConstants.TAG,file.getPath());
					}else{
						Toast.makeText(getApplicationContext(), "V�deo n�o existe", Toast.LENGTH_LONG);
					}
				}else{
					Toast.makeText(getApplicationContext(), "Arquivo selecionado n�o � v�lido.", Toast.LENGTH_LONG);
				}

//				Exemplo de toast
//				Toast toast = Toast.makeText(VideoSelectionActivity.this.getApplicationContext(), "Voc� apertou bot�o de iniciar v�deo", Toast.LENGTH_LONG);
//				toast.show();
			}
		});
		
		((ImageButton)findViewById(R.id.btn_stop)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				VideoView videoView = (VideoView) findViewById(R.id.vv_video);
				if(videoView.isPlaying()){
					videoView.stopPlayback();
				}
				
//				Exemplo de tosast
//				Toast toast = Toast.makeText(VideoSelectionActivity.this.getApplicationContext(), "Voc� apertou bot�o de parar v�deo", Toast.LENGTH_LONG);
//				toast.show();
			}
		});
		
		((ImageButton)findViewById(R.id.btn_pause)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				VideoView videoView = (VideoView) findViewById(R.id.vv_video);
				if(videoView.isPlaying()){
					videoView.pause();
				}

				
//				Exemplo de toast
//				Toast toast = Toast.makeText(VideoSelectionActivity.this.getApplicationContext(), "Voc� apertou bot�o de pausar v�deo", Toast.LENGTH_LONG);
//				toast.show();
			}
		});
		
	
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(this.connection!=null){
			try{
				this.unbindService(connection);
			}catch (Exception e) {
				Log.e(MMConstants.TAG, "N�o foi poss�vel desconectar a aplica��o");
			}
		}
	}
	
}

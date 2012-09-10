package br.unb.projetopositivo.mm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import br.unb.projetopositivo.mm.playlist.PlayListActivity;
import br.unb.projetopositivo.mm.socialnetwork.ShareButtonActivity;
import br.unb.projetopositivo.mm.view.video.VideoListActivity;
// teste de push

public class MMUnBActivity extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        this.configureUI();
    }
    
    private void configureUI(){
    	
    	//botao que abre tela de media
    	((Button)findViewById(R.id.btn_media_list)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent startActivtyIntent = new Intent(getApplicationContext(), VideoListActivity.class);
				startActivity(startActivtyIntent);
			}
		});
    	
    	//botao que exibe as playlists
    	((Button)findViewById(R.id.btn_play_list)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent startActivtyIntent = new Intent(getApplicationContext(), PlayListActivity.class);
				startActivity(startActivtyIntent);
			}
		});
    	
    	//social network button
    	((Button)findViewById(R.id.btn_share)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent startActivtyIntent = new Intent(getApplicationContext(), ShareButtonActivity.class);
				startActivity(startActivtyIntent);
			}
		});
    	
    	//botao que fecha aplicao
    	((Button)findViewById(R.id.btn_exit)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MMUnBActivity.this.finish();
			}
		});
    	
    }
    
}
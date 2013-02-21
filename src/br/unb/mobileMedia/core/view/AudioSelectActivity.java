package br.unb.mobileMedia.core.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.audioPlayer.AudioPlayerList;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.manager.Manager;

public class AudioSelectActivity extends Activity {

	List<Audio> musicas = new ArrayList<Audio>();
	List<Integer> musicasAdicionadasId = new ArrayList<Integer>();
	private String names[];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_playlist_music_select);
		refreshListMusicLists();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_playlist_music_select, menu);
		return true;
	}

	//updates the listview with all the musics from the DB
	private void refreshListMusicLists(){

		ListView listMusicLists = (ListView) findViewById(R.id.list_musiclistselect);

		try {
			musicas = Manager.instance().listAllProduction(this);
		}
		catch (DBException e) {e.printStackTrace();}

		names = new String[musicas.size()];
		int i=0;
		for (Audio aux : musicas){names[i++]=aux.getTitle();}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1, 
				android.R.id.text1, 
				names);
		listMusicLists.setAdapter(adapter);

		//Calls the Playlist Editor when a playlist is pressed.
		listMusicLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {

				Audio aux = musicas.get(position);
				musicasAdicionadasId.add(aux.getId());

				AudioPlayerList.getInstance(getApplicationContext()).addMusic(aux);
				finish();

			}
		});



	}


	//when back button is pressed, saves the ids selected and return the array to the previous activity.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent();
			setResult(RESULT_CANCELED, intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}

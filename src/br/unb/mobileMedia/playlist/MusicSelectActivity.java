package br.unb.mobileMedia.playlist;

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
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.manager.Manager;

public class MusicSelectActivity extends Activity {

    List<Audio> musicas = new ArrayList<Audio>();
    List<Integer> musicasAdicionadasId = new ArrayList<Integer>();
    private String names[];
    private int playListId; 
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //get from intent the playlist`s id.
        Bundle extras = getIntent().getExtras();
        //the playlist id that the music will be added.
        playListId = extras.getInt(MainPlaylistListFragment.SELECTED_PLAYLIST_ID);
        
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
            
            	try{
					Audio aux = musicas.get(position);
					musicasAdicionadasId.add(aux.getId());
					
					Manager.instance().addMediaToPlaylist(getBaseContext(), playListId, musicasAdicionadasId);
					//Closes the activity. Only one music can be added per time.
					finish();
					
            		} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 		}
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

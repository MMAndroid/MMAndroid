package br.unb.mobileMedia.playlist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.manager.Manager;
import br.unb.mobileMedia.core.view.AudioPlayerActivity;

public class PlayListEditorActivity extends Activity {

	private int playListId;
	Playlist playlist;
	List<Audio> musicList = null;
	private String names[];
	
	int result=1;
	//String containing the playlist id that will be passed on to another activity through an intent.
		public final static String SELECTED_PLAYLIST_ID = "idPlaylist";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_editor);
        
        //get from intent the playlist`s id.
        Bundle extras = getIntent().getExtras();
        playListId = extras.getInt(MainPlaylistListActivity.SELECTED_PLAYLIST_ID);
        configureUI();
    }

    private void configureUI() {
    	//Update the List View
    			ListView listMusicLists = (ListView) findViewById(R.id.list_musiclist);
    			registerForContextMenu(listMusicLists);
    			
    			
    	((Button)findViewById(R.id.btn_addMusiclist)).setOnClickListener(new View.OnClickListener(){     
			public void onClick(View v) {
				
				//when button is clicked, start activity and wait for result.
				//result is caught in method onActivityResult.
				
				Intent startActivityIntent = new Intent(getApplicationContext(), MusicSelectActivity.class);
				startActivityIntent.putExtra(SELECTED_PLAYLIST_ID, playListId);
				startActivityForResult(startActivityIntent, result);
			}        

		});
    	
    	((Button)findViewById(R.id.btn_playPlaylist)).setOnClickListener(new View.OnClickListener(){     
			
    		public void onClick(View v) {
    			
    			//when button is clicked, the music list is send to player.
    			
    			List<Audio> listTmp = musicList;
				Audio[] executionList = new Audio[listTmp.size()]; 
				
				listTmp.toArray(executionList);
			
				Intent startActivtyIntent = new Intent(getApplicationContext(), AudioPlayerActivity.class);
				startActivtyIntent.putExtra(AudioPlayerActivity.EXECUTION_LIST, executionList);
				startActivity(startActivtyIntent);		    				
		    		
    			}
			
    	});
    	
		//Refresh the List View (List of Musics)
    	refreshListMusicLists();
	}

    private void refreshListMusicLists(){

		//Update the List View
		ListView listMusicLists = (ListView) findViewById(R.id.list_musiclist);
		registerForContextMenu(listMusicLists);
		try {
			playlist = Manager.instance().getSimplePlaylist(this,playListId);
			if(playlist!=null)
				musicList = Manager.instance().getMusicFromPlaylist(this, playListId);
			
			
			//String[] names;
			// check if there is any playlist
			if (musicList == null || musicList.size() == 0) {
				names = new String[1];
				// TODO Refactor: extract string to xml
				names[0] = "Nenhuma Musica Encontrada.";
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
				listMusicLists.setAdapter(adapter);
			} else {
				names = new String[musicList.size()];
				int i = 0;
				for (Audio p : musicList) {
					names[i++] = p.getTitle();
				}
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
						android.R.layout.simple_list_item_1, 
						android.R.id.text1, 
						names);
				listMusicLists.setAdapter(adapter);
			}
			
		} catch (DBException e) {e.printStackTrace();}

	}
    
    //catch result from another activity.
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
  
    	refreshListMusicLists();
    }
    
    
    //long press para deletar determinada musica
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId()==R.id.list_musiclist) {


			menu.setHeaderTitle("Menu:");
			String[] menuItems = getResources().getStringArray(R.array.menu_music);
			for (int i = 0; i<menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		//Index number of the selected option from the context menu
		int menuItemIndex = item.getItemId();

		List<Integer> song = new ArrayList<Integer>();		
		song.add(musicList.get(info.position).getId());


		//Option - REMOVE
		if(menuItemIndex == 0){
			
			
			
			try {
				Manager.instance().removeMediaFromPlaylist(this, playListId ,song );
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			refreshListMusicLists ();
		}
		return true;
	}

    
    /*
    private Intent createIntentForMusicPlayer(){
    	
    	//creates an audio list and add every music from the playlist to the audiolist
    	//after, i create an intent to the audio player activity.
    	//then i should add the music list to the parcel then add the parcel to the intent and return the intent
    	// but i dont know how to add the musics (or the musiclist) to the parcel
    	
    	List<Audio> musicList = null;
    	Intent startActivityIntent = new Intent(getApplicationContext(), AudioPlayerActivity.class);
    	
		try {musicList = Manager.instance().getMusicFromPlaylist(this, playListId);} 
		catch (DBException e) {e.printStackTrace();}
		ArrayList<? extends Parcelable> parcel = null;
		
		//adiciona as musicas da musiclist no arraylist de parcel. Nao consegui fazer
		for (Audio p : musicList) {}
    	
		startActivityIntent.putParcelableArrayListExtra("EXECUTION_LIST", parcel);
		return startActivityIntent;
    }
 */  
}

package br.unb.mobileMedia.playlist;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.manager.Manager;

public class MusicSelectFragment extends Fragment {

    List<Audio> musicas = new ArrayList<Audio>();
    List<Integer> musicasAdicionadasId = new ArrayList<Integer>();
    private String names[];
    private int playListId; 
    
        
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	getActivity().setTitle(R.string.title_activity_music_select);
		return inflater.inflate(R.layout.activity_playlist_music_select, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		Bundle extras = getArguments();
        //the playlist id that the music will be added.
        playListId = extras.getInt(MainPlaylistListFragment.SELECTED_PLAYLIST_ID);
        
        refreshListMusicLists();
        
    	Log.i("Choose music to ", "Playlist "+playListId);

	}

	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.activity_playlist_music_select, menu);
    }
    
    //updates the listview with all the musics from the DB
    private void refreshListMusicLists(){
    	
    	ListView listMusicLists = (ListView) getActivity().findViewById(R.id.list_musiclistselect);
    	
    	try {
    		musicas = Manager.instance().listAllAudio(getActivity());
    	}catch (DBException e) {
    		e.printStackTrace();
    	}
    	
    	names = new String[musicas.size()];
    	int i=0;
    	for (Audio aux : musicas){names[i++]=aux.getTitle();}
    	
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, 
				android.R.id.text1, names);
    	listMusicLists.setAdapter(adapter);
    
    	//Calls the Playlist Editor when a playlist is pressed.
    	listMusicLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
            
            	try{
					Audio aux = musicas.get(position);
					musicasAdicionadasId.add(aux.getId().intValue());
					
					Manager.instance().addMediaToPlaylist(getActivity().getBaseContext(), playListId, musicasAdicionadasId);
					//Closes the activity. Only one music can be added per time.
					getActivity().getSupportFragmentManager().popBackStack();
					
            		} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 		}
            });
    
    
    
    }
    
    //TODO review this method
    //when back button is pressed, saves the ids selected and return the array to the previous activity.
    //@Override
    /*
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	Intent intent=new Intent();
        	setResult(RESULT_CANCELED, intent);
        	finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    */
}

package br.unb.mobileMedia.core.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.audioPlayer.AudioPlayerList;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.manager.Manager;
import br.unb.mobileMedia.playlist.PlayListManager;

public class AudioSelectFragment extends Fragment{

	List<Audio> musicas = new ArrayList<Audio>();
	List<Integer> musicasAdicionadasId = new ArrayList<Integer>();
	private String names[];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setTitle(R.string.add_a_music);
		return inflater.inflate(R.layout.activity_playlist_music_select, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		refreshListMusicLists();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.activity_playlist_music_select, menu);
	}

	//updates the listview with all the musics from the DB
	private void refreshListMusicLists(){

		ListView listMusicLists = (ListView) getActivity().findViewById(R.id.list_musiclistselect);

		try {
			musicas = Manager.instance().listAllProduction(getActivity());
		}
		catch (DBException e) {e.printStackTrace();}

		names = new String[musicas.size()];
		int i=0;
		for (Audio aux : musicas){names[i++]=aux.getTitle();}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, 
				android.R.id.text1, 
				names);
		listMusicLists.setAdapter(adapter);

		//Calls the Playlist Editor when a playlist is pressed.
		listMusicLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {

				Audio aux = musicas.get(position);
				musicasAdicionadasId.add(aux.getId());

				AudioPlayerList.getInstance(getActivity().getApplicationContext()).addMusic(aux);
				
				Fragment target = getTargetFragment();
				if(target instanceof PlayListManager) {
					((PlayListManager) target).addMusic(aux);
				}
				
				
			    getActivity().getSupportFragmentManager().popBackStack();
			}
		});
	}


	//when back button is pressed, saves the ids selected and return the array to the previous activity.
	//@Override
	// TODO review this method
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent=new Intent();
			getActivity().setResult(getActivity().RESULT_CANCELED, intent);
			getActivity().finish();
			return true;
		}
		//return super.onKeyDown(keyCode, event);
		return false;
	}
}

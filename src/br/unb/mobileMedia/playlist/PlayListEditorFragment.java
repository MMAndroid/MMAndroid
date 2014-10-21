package br.unb.mobileMedia.playlist;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.AudioOld;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.manager.Manager;
import br.unb.mobileMedia.core.view.AudioPlayerFragment;

public class PlayListEditorFragment extends Fragment {
	private int playListId;
	Playlist playlist;
	List<Audio> musicList = null;
	private String names[];
	int result = 1;
	// String containing the playlist id that will be passed on to another
	// activity through an intent.
	public final static String SELECTED_PLAYLIST_ID = "idPlaylist";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.title_activity_playlist_editor);
		Log.i("Recebendo PlayList", "Clicada");
		return inflater.inflate(R.layout.activity_playlist_editor, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// get from intent the playlist`s id.
		Bundle extras = getArguments();
		playListId = extras.getInt(MainPlaylistListFragment.SELECTED_PLAYLIST_ID);
		Log.i("PlayList Name", "Num:" + playListId);
		configureUI();
	}

	private void configureUI() {
		// Update the List View
		ListView listMusicLists = (ListView) getActivity().findViewById(R.id.list_musiclist);
		registerForContextMenu(listMusicLists);
		// Refresh the List View (List of Musics)
		refreshListMusicLists();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.activity_playlist_editor_action_bar, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.AddMisicInPlayListActionBar:
			// To do insert animate rotate in this item of action bar
			addMusicInPlayList();
			break;
		case R.id.ExecutePlayList:
			executePlayList();
			break;
		default:
			Log.i("Message PL", "nao Implementado");
		}
		return super.onOptionsItemSelected(item);
	}

	private void addMusicInPlayList() {
		// when button is clicked, start activity and wait for result.
		// result is caught in method onActivityResult.
		Bundle args = new Bundle();
		args.putInt(SELECTED_PLAYLIST_ID, playListId);
		// TODO Extract this to a method (repeated in MMUnBActivity too)
		Fragment newFragment = new MusicSelectFragment();
		// Chamando o FileChooser para escolher as musicas
		// Fragment newFragment = new FileChooserFragment();
		newFragment.setArguments(args);
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		if (getActivity().findViewById(R.id.main) != null) {
			transaction.replace(R.id.main, newFragment);
			transaction.addToBackStack(null);
		} else {
			transaction.replace(R.id.content, newFragment);
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	private void executePlayList() {
		List<Audio> listTmp = musicList;
		AudioOld[] executionList = new AudioOld[listTmp.size()];
		listTmp.toArray(executionList);
		Bundle args = new Bundle();
		args.putParcelableArray(AudioPlayerFragment.EXECUTION_LIST,	executionList);
		// TODO Extract this to a method (repeated in MMUnBActivity too)
		Fragment newFragment = new AudioPlayerFragment();
		newFragment.setArguments(args);
		FragmentManager manager = getActivity().getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		if (getActivity().findViewById(R.id.main) != null) {
			transaction.replace(R.id.main, newFragment);
			transaction.addToBackStack(null);
		} else {
			transaction.replace(R.id.content, newFragment);
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	private void refreshListMusicLists() {
		// Update the List View
		ListView listMusicLists = (ListView) getActivity().findViewById(R.id.list_musiclist);
		registerForContextMenu(listMusicLists);
		try {
			playlist = Manager.instance().getPlaylistById(getActivity(), playListId);
			if (playlist != null) {
				musicList = Manager.instance().getMusicFromPlaylist(getActivity(), playListId);
			}
			// String[] names;
			// check if there is any playlist
			if (musicList == null || musicList.size() == 0) {
				names = new String[1];
				// TODO Refactor: extract string to xml
				names[0] = "Nenhuma Musica Encontrada.";
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						names);
				listMusicLists.setAdapter(adapter);
			} else {
				names = new String[musicList.size()];
				int i = 0;
				for (Audio p : musicList) {
					names[i++] = p.getTitle();
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						android.R.id.text1, names);
				listMusicLists.setAdapter(adapter);
			}
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

	// catch result from another activity.
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("PlayListEditor", "Executing onActivityResult");
		refreshListMusicLists();
	}

	// long press para deletar determinada musica
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list_musiclist) {
			menu.setHeaderTitle("Options PlayList");
			String[] menuItems = getResources().getStringArray(
					R.array.menu_music);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		// Index number of the selected option from the context menu
		int menuItemIndex = item.getItemId();
		List<Integer> song = new ArrayList<Integer>();
		song.add(musicList.get(info.position).getId().intValue());
		// Option - REMOVE
		if (menuItemIndex == 0) {
			try {
				Manager.instance().removeMediaFromPlaylist(getActivity(),playListId, song);
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			refreshListMusicLists();
		}
		return true;
	}
	/*
	 * private Intent createIntentForMusicPlayer(){ //creates an audio list and
	 * add every music from the playlist to the audiolist //after, i create an
	 * intent to the audio player activity. //then i should add the music list
	 * to the parcel then add the parcel to the intent and return the intent //
	 * but i dont know how to add the musics (or the musiclist) to the parcel
	 * List<Audio> musicList = null; Intent startActivityIntent = new
	 * Intent(getApplicationContext(), AudioPlayerActivity.class); try
	 * {musicList = Manager.instance().getMusicFromPlaylist(this, playListId);}
	 * catch (DBException e) {e.printStackTrace();} ArrayList<? extends
	 * Parcelable> parcel = null; //adiciona as musicas da musiclist no
	 * arraylist de parcel. Nao consegui fazer for (Audio p : musicList) {}
	 * startActivityIntent.putParcelableArrayListExtra("EXECUTION_LIST",
	 * parcel); return startActivityIntent; }
	 */
}
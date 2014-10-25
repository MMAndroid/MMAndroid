package br.unb.mobileMedia.playlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;
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
	private Map<Long, String> mapIdNameAuthor = new HashMap<Long, String>();
	private List<Author> authors;
	private ArrayAdapterMusic listviewadapter;
	private List<Audio> selecteds = new ArrayList<Audio>();

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
		playListId = extras
				.getInt(MainPlaylistListFragment.SELECTED_PLAYLIST_ID);
		Log.i("PlayList ID", "" + playListId);
		configureUI();
	}

	private void configureUI() {
		// Update the List View
		ListView list = (ListView) getActivity().findViewById(
				R.id.list_musiclist);
		registerForContextMenu(list);
		// Refresh the List View (List of Musics)
		refreshlist();
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
		newFragment.setArguments(args);
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
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
		Audio[] executionList = new Audio[listTmp.size()];
		listTmp.toArray(executionList);
		Bundle args = new Bundle();
		args.putParcelableArray(AudioPlayerFragment.EXECUTION_LIST,
				executionList);
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

	private void refreshlist() {
		// Update the List View
		final ListView list = (ListView) getActivity().findViewById(
				R.id.list_musiclist);

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
				list.setAdapter(adapter);
			} else {
				// Pass results to ListViewAdapter Class
				listviewadapter = new ArrayAdapterMusic(getActivity()
						.getApplicationContext(),
						R.layout.music_row_select_from_playlist, musicList,
						mapIdNameAuthor);

				// Binds the Adapter to the ListView
				list.setAdapter(listviewadapter);
				list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

				list.setMultiChoiceModeListener(new MultiChoiceModeListener() {

					public void onItemCheckedStateChanged(ActionMode mode,
							int position, long id, boolean checked) {
						// Capture total checked items
						final int checkedCount = list.getCheckedItemCount();
						// Set the CAB title according to total checked items
						mode.setTitle(checkedCount + " Selected");
						// Calls toggleSelection method from ListViewAdapter Class
						listviewadapter.toggleSelection(position);

						// add in selecteds itens clickeds

						if (!selecteds.contains(listviewadapter.getItem(position))) {
							selecteds.add(listviewadapter.getItem(position));
							Log.i("adicionado", ""
									+ listviewadapter.getItem(position).getId()
											.intValue());
						} else {
							selecteds.remove(listviewadapter.getItem(position));
							Log.i("removido", ""
									+ listviewadapter.getItem(position).getId()
											.intValue());

						}

					}

					public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
						switch (item.getItemId()) {
						case R.id.removeMedia:

							removeMedia();

							mode.finish();

							return true;
						default:
							return false;
						}
					}

					public boolean onCreateActionMode(ActionMode mode, Menu menu) {
						mode.getMenuInflater().inflate(
								R.menu.remove_audio_playlist, menu);
						return true;
					}

					public void onDestroyActionMode(ActionMode mode) {
						// TODO Auto-generated method stub
						listviewadapter.removeSelection();
					}

					public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
						// TODO Auto-generated method stub
						return false;
					}

				});
			}
		} catch (DBException e) {
			e.printStackTrace();
		}
	}

	
	
	private void removeMedia() {
		try {

			Manager.instance().removeMediaFromPlaylist(
					getActivity().getBaseContext(), playListId, selecteds);

			
			refreshlist();

		} catch (DBException e) {
			e.printStackTrace();
			Log.i("DBException", "in saveMedia - MusicSelectFragment");
		}

	}
	
	// catch result from another activity.
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("PlayListEditor", "Executing onActivityResult");
		refreshlist();
	}





}
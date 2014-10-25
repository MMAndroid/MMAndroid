package br.unb.mobileMedia.playlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;
import br.unb.mobileMedia.core.manager.Manager;

public class MusicSelectFragment extends Fragment {

	@SuppressLint("UseSparseArrays")
	private Map<Long, String> mapIdNameAuthor = new HashMap<Long, String>();
	private List<Audio> musicas = new ArrayList<Audio>();
	private List<Author> authors = new ArrayList<Author>();
	private List<Audio> selecteds = new ArrayList<Audio>();
	private ArrayAdapterMusic listviewadapter;
	private int playListId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		setHasOptionsMenu(true);

		getActivity().setTitle(R.string.title_activity_music_select);

		return inflater.inflate(R.layout.activity_playlist_music_select,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		Bundle extras = getArguments();
		// the playlist id that the music will be added.
		playListId = extras.getInt(MainPlaylistListFragment.SELECTED_PLAYLIST_ID);

		refreshListMusicLists();

		Log.i("MusicSelectFragment", "");
		Log.i("Add music to ", "Playlist " + playListId);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		menu.clear();

	}

	// updates the listview with all the musics from the DB
	private void refreshListMusicLists() {

		try {
			musicas = Manager.instance().listAllAudio(getActivity());
			authors = Manager.instance().listAuthors(
					getActivity().getApplicationContext());
		} catch (DBException e) {
			e.printStackTrace();
		}

		for (Author author : authors) {
			mapIdNameAuthor.put(author.getId(), author.getName());
		}

		// Binds the Adapter to the ListView
		final ListView list = (ListView) getActivity().findViewById(
				R.id.list_musiclistselect);

		// Pass results to ListViewAdapter Class
		listviewadapter = new ArrayAdapterMusic(getActivity()
				.getApplicationContext(),
				R.layout.music_row_select_from_playlist, musicas,
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
				case R.id.saveMedia:

					saveMedia();

					mode.finish();

					return true;
				default:
					return false;
				}
			}

			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(
						R.menu.activity_playlist_music_select, menu);
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

	
	private void saveMedia() {
		try {

			Manager.instance().addMediaToPlaylist(
					getActivity().getBaseContext(), playListId, selecteds);
			// Closes the activity. Only one music can be added per time.
			getActivity().getSupportFragmentManager().popBackStack();

		} catch (DBException e) {
			e.printStackTrace();
			Log.i("DBException", "in saveMedia - MusicSelectFragment");
		}

	}

}

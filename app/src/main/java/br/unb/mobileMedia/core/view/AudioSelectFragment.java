package br.unb.mobileMedia.core.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.core.extractor.MediaExtractor;
import br.unb.mobileMedia.core.manager.Manager;
import br.unb.mobileMedia.playlist.ArrayAdapterAudio;
import br.unb.mobileMedia.playlist.AudioViewItem;
import br.unb.mobileMedia.playlist.PlayListEditorFragment;
import br.unb.mobileMedia.util.EndlessScrollListener;

public class AudioSelectFragment extends ListFragment {

	public final static String SELECTED_PLAYLIST_ID = "idPlaylist";
	public final static String SELECTED_PLAYLIST_NAME = "namePlaylist";
	
	private final int GROUP_PLAYLIST_ID = 999;
	private final int ITEMS_PER_PAGE = 20;
	private List<Integer> selecteds = new ArrayList<Integer>();
	private Integer totalAudioInDb = 0;
	private List<Audio> audios = null;
	private MediaExtractor mediaExtractor;
	private ArrayAdapterAudio adapter;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.context = getActivity().getApplicationContext();
		this.totalAudioInDb = Manager.instance().countMedias(context);

		this.mediaExtractor = new DefaultAudioExtractor(this.getActivity()
				.getApplicationContext());

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		getListView().setDivider(
				getResources().getDrawable(android.R.color.background_dark));
		getListView().setDividerHeight(2);
		getListView().setSelector(R.drawable.list_selector);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

	}

	public void onChooseMore(int position) {
		if (!selecteds.contains(adapter.getItem(position).id)) {
			selecteds.add(adapter.getItem(position).id);
			Log.i("adicionado", "" + adapter.getItem(position).id.intValue());

		} else {
			selecteds.remove(adapter.getItem(position).id);
			Log.i("removido", "" + adapter.getItem(position).id.intValue());
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		loadMore(0, 0);// Show first Items in ListView

		// create adapter with kind of data: ArrayList<AudioViewItem>
		adapter = new ArrayAdapterAudio(context, R.layout.audio_row,
				new ArrayList<AudioViewItem>(), getListView());

		setListAdapter(adapter);

		refreshListMusicLists();
	}

	// updates the listview with all the musics from the DB
	private void refreshListMusicLists() {

		getListView().setOnScrollListener(new EndlessScrollListener() {

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				Log.e("Page", "" + page);
				loadMore(page, totalItemsCount);
			}

		});

		getListView().setMultiChoiceModeListener(new MultiChoiceModeListener() {

			//Add submenu for playlist
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// create SubMenu for add media direct in an playlist
				SubMenu sub = menu.addSubMenu("").setIcon(
						R.drawable.ic_add_playlist);

				List<Playlist> playlists = Manager.instance().listPlaylists(
						getActivity().getApplicationContext());
				
				
				for (Playlist playlist : playlists) {
//					sub.add(groupId, itemId, order, title)
					sub.add(GROUP_PLAYLIST_ID, playlist.getId() , Menu.NONE, playlist.getName());
				}

				return getActivity().onCreateOptionsMenu(menu);

			}

			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				Log.i("MenuItem selected", item.getItemId() + " " );
				
				switch (item.getGroupId() ) {
				
					//Case groupId is group Playlists
					case GROUP_PLAYLIST_ID:
						saveItems(item.getItemId(), String.valueOf(item.getTitle()));
						mode.finish();
				
				
					default:
						return false;
				}
			}

			public void onDestroyActionMode(ActionMode mode) {
				 adapter.removeSelection();
			}

			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				// Capture total checked items
				final int checkedCount = getListView().getCheckedItemCount();

				mode.setTitle(checkedCount + " Selected");

				adapter.toggleSelection(position);

				onChooseMore(position);

			}
		});

		// // Calls the Playlist Editor when a playlist is pressed.
		// listMusicLists.setOnItemClickListener(new
		// AdapterView.OnItemClickListener() {
		//
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		//
		// Audio aux = musicas.get(position);
		// musicasAdicionadasId.add(aux.getId().intValue());
		//
		// AudioPlayerList.getInstance(
		// getActivity().getApplicationContext())
		// .addMusic(aux);
		//
		// Fragment target = getTargetFragment();
		// if (target instanceof PlayListManager) {
		// ((PlayListManager) target).addMusic(aux);
		// }
		//
		// getActivity().getSupportFragmentManager()
		// .popBackStack();
		// }
		// });
	}

	
	/*
	 * save audios selecteds in playlistmedia
	 * */
	public void saveItems(int playListId, String namePlaylist) {
		try {
			
			Manager.instance().addMediaToPlaylist(
					getActivity().getBaseContext(), playListId, selecteds);

			
			Bundle args = new Bundle();
			args.putInt(SELECTED_PLAYLIST_ID, playListId);
			args.putString(SELECTED_PLAYLIST_NAME, namePlaylist);

			Fragment newFragment = new PlayListEditorFragment();
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


		} catch (Exception e) {
			e.printStackTrace();
			Log.i("DBException", "in saveMedia - AudioSelectFragment");
		}
	}
	
	
	
	private void loadMore(final int page, final int totalItemsCount) {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... voids) {

				try {
					int offset = 1 + ITEMS_PER_PAGE * (page - 1);

					audios = Manager.instance().listAllAudioPaginated(context,
							offset, ITEMS_PER_PAGE);

				} catch (DBException e) {
					Log.e("DBException", e.getMessage());
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				if (totalItemsCount < totalAudioInDb) {
					adapter.swapItem(audioViewItem(audios));
				}

				Log.e("onPostExecute: ", "onPostExecute");

			}
		}.execute();

	}

	private List<AudioViewItem> audioViewItem(List<Audio> audios) {

		List<AudioViewItem> mItemsTemp = new ArrayList<AudioViewItem>();
		for (Audio audio : audios) {
			try {
				
				Integer id = audio.getId();
				String title = mediaExtractor.getTitle(audio.getUrl());
				String album = mediaExtractor.getAlbum(audio.getUrl());
				String author = mediaExtractor.getAuthor(audio.getUrl());
				String bitRate = mediaExtractor.getBitRate(audio.getUrl());

				mItemsTemp.add(new AudioViewItem(id, title, album, author,
						bitRate));

			} catch (Exception e1) {
				Log.e("ExceptionMediaExtractor", e1.getMessage());
			}

		}

		return mItemsTemp;
	}

}

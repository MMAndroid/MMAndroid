package br.unb.mobileMedia.playlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;
import br.unb.mobileMedia.core.manager.Manager;
import br.unb.mobileMedia.playlist.MultiChoiceMode.OnChooseMoreListener;

public class MusicSelectFragment extends Fragment implements OnChooseMoreListener {

	public final static int menuInflate = R.menu.save_audio_playlist;
	private final int ITEMS_PER_PAGE = 9;

	@SuppressLint("UseSparseArrays")
	private Map<Long, String> mapIdNameAuthor = new HashMap<Long, String>();
	private List<Audio> musicList = new ArrayList<Audio>();
	private List<Audio> musics = new ArrayList<Audio>();
	private List<Author> authors = new ArrayList<Author>();
	private List<Audio> selecteds = new ArrayList<Audio>();
	private int playListId;
	private int listviewselection;
	private int totalAudioInDb = 0;
	
	private Context context;
	private View footerView;
	private ListView list = null;
	private ArrayAdapterMusic listviewadapter;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		setHasOptionsMenu(true);

		this.context = getActivity().getApplicationContext();
		
		getActivity().getActionBar().setSubtitle(R.string.title_activity_music_select);
		
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

		this.list = (ListView) getActivity().findViewById(R.id.list_musiclistselect);

		this.listviewadapter = new ArrayAdapterMusic(context,
				R.layout.music_row_select_from_playlist, musics,
				mapIdNameAuthor);

		this.footerView = ((LayoutInflater) getActivity().getSystemService(
				context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer,
				null, false);

		this.list.addFooterView(footerView, null, false);
		
		this.list.setAdapter(listviewadapter);
		
		try {
			this.musicList = Manager.instance().listAllAudio(context);
			this.totalAudioInDb = this.musicList.size();
			this.authors = Manager.instance().listAuthors(context);

			for (Author author : this.authors) {
				this.mapIdNameAuthor.put(author.getId(), author.getName());
			}

		} catch (DBException e) {
			e.printStackTrace();
		}
		
		
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

		// Pass results to ListViewAdapter Class
		this.listviewadapter = new ArrayAdapterMusic(context,
				R.layout.music_row_select_from_playlist, musics,
				mapIdNameAuthor);

		this.list.setOnScrollListener(new EndlessScrollListener(){
			@Override
			public void onLoadMore(int page, int totalItemsCount){
				Log.i("OnLoadMore", ""+page);
				loadMore(page, totalItemsCount);
				listviewadapter.setNotifyOnChange(true);
			}
		});
		
		list.setAdapter(listviewadapter);

		list.setMultiChoiceModeListener(new MultiChoiceMode(this, list, this.menuInflate));
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		list.setSelection(listviewselection);
		

	}

	


	public void onChooseMore(int position) {
		if (!selecteds.contains(listviewadapter.getItem(position))) {
			selecteds.add(listviewadapter.getItem(position));
//			Log.i("adicionado",""+ listviewadapter.getItem(position).getId().intValue());
		} else {
			selecteds.remove(listviewadapter.getItem(position));
//			Log.i("removido", ""+ listviewadapter.getItem(position).getId().intValue());
		}	
	}

	public void removeItems() {
		// TODO Auto-generated method stub
		
	}

	public void saveItems() {
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
	
	
	private void loadMore(final int page, final int totalItemsCount) {
		
		Log.e("Page", page+"-"+totalItemsCount);
		
		new AsyncTask<Void, Void, Void>() {
			List<Audio> audios;

			@Override
			protected Void doInBackground(Void... voids) {
				// Simulating delay to get more items from an API.
				try {
//					Log.e("Lisview items", ""+listviewadapter.getCount());
//					Log.e("totalItemsCount", ""+totalItemsCount);
//					Log.e("page", ""+page);

					Thread.sleep(1000);
					audios = Manager.instance().listAllAudioPaginated(context, listviewadapter.getCount(), ITEMS_PER_PAGE);
//					audios = Manager.instance().getMusicFromPlaylistPaginate(
//						context, playListId, listviewadapter.getCount(),
//						ITEMS_PER_PAGE);
					
					Log.e("listviewadapter: " + listviewadapter.getCount(), "musicList: "+musicList.size());

				} catch (Exception e) {
					e.getStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				
				list.addFooterView(footerView, null, false);
				
				if(totalItemsCount < totalAudioInDb){
					Log.e("totalItemsCount: " + totalItemsCount, "musicList: "+musicList.size());
					for (Audio audio : audios) {
						musics.add(audio);
					}
				}
				
				list.removeFooterView(footerView);

			}
		}.execute();
	}
	

}

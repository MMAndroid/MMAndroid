package br.unb.mobileMedia.playlist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.core.extractor.MediaExtractor;
import br.unb.mobileMedia.core.manager.Manager;
import br.unb.mobileMedia.core.view.AudioPlayerFragment;
import br.unb.mobileMedia.util.AudioToParcelable;

public class PlayListEditorFragment extends ListFragment {

	public final static String SELECTED_PLAYLIST_ID = "idPlaylist";
	public final static String SELECTED_PLAYLIST_NAME = "namePlaylist";

	public final static int menuInflate = R.menu.remove_audio_playlist;

	private final int ITEMS_PER_PAGE = 9;
	private int totalAudioInDb = 0;
	private View footerView;
	private ListView list = null;
	private Integer playListId;
	private String  namePlaylist;

	private List<AudioViewItem> mItems;

	private Context context;
	private ArrayAdapterAudio listviewadapter;
	private MediaExtractor mediaExtractor;

	private List<Audio> musicList = new ArrayList<Audio>();
	private List<Audio> selecteds = new ArrayList<Audio>();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		this.context = getActivity().getApplicationContext();

		mItems = new ArrayList<AudioViewItem>();
		mediaExtractor = new DefaultAudioExtractor(context);
		
	}
	
	

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().setDivider(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		Log.i("PlayListEditorFragment", "PlayListEditorFragment");

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle extras = getArguments();

		this.playListId   = extras.getInt(MainPlaylistListFragment.SELECTED_PLAYLIST_ID);
		this.namePlaylist = extras.getString(MainPlaylistListFragment.SELECTED_PLAYLIST_NAME);
				
		getActivity().getActionBar().setSubtitle("Playlist: " + namePlaylist);
		


		// this.list = (ListView)
		// getActivity().findViewById(R.id.list_musiclist);

		// this.listviewadapter = new ArrayAdapterMusic(context,
		// R.layout.audio_row, musics,
		// mapIdNameAuthor);

		// this.footerView = ((LayoutInflater) getActivity().getSystemService(
		// context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer,
		// null, false);

		// this.list.addFooterView(footerView, null, false);

		// this.list.setAdapter(listviewadapter);
		// this.setListAdapter(listviewadapter);
		// this.footerView(footerView, null, false);

		
		
		configureUI();
	}

	private void configureUI() {
		refreshlist();
	}

	private void refreshlist() {

		try {
			
			this.musicList = Manager.instance().getMusicFromPlaylist(context, playListId);
			
			for(Audio audio: musicList){
				
				Integer   id    = null;
				String title = null;
				String album = null;
				String author = null;
				String bitRate = null;
				
				try {
										
					id       = audio.getId();
					title    = this.mediaExtractor.getTitle(audio.getUrl());
					album    = this.mediaExtractor.getAlbum(audio.getUrl()); 
					author   = this.mediaExtractor.getAuthor(audio.getUrl());
					bitRate  = this.mediaExtractor.getBitRate(audio.getUrl());
					
				} catch (Exception e1) {
					bitRate  = this.mediaExtractor.getBitRate(audio.getUrl());
					Log.e("ExceptionMediaExtractor", e1.getMessage());
					
				}
				
				mItems.add(new AudioViewItem(id, title,	album, author, bitRate));	
			}
			

		} catch (DBException e) {
			e.printStackTrace();
		}

		setListAdapter(new ArrayAdapterAudio(getActivity(),  R.layout.audio_row, mItems, getListView()));

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
		
		Bundle args = new Bundle();
		args.putInt(SELECTED_PLAYLIST_ID, playListId);
		args.putString(SELECTED_PLAYLIST_NAME, namePlaylist);
		
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
		
		AudioToParcelable[] audioToParcelable = new AudioToParcelable[musicList.size()];
		
		for(int i = 0; i< musicList.size(); i++){
			audioToParcelable[i] = new AudioToParcelable(musicList.get(i));
		}
				
		Bundle args = new Bundle();
		args.putParcelableArray(AudioPlayerFragment.EXECUTION_LIST, audioToParcelable);
				
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

	// catch result from another activity.
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("PlayListEditor", "Executing onActivityResult");
		refreshlist();
	}

	private void loadMore(final int page, final int totalItemsCount) {

		Log.e("Page", page + "-" + totalItemsCount);

		new AsyncTask<Void, Void, Void>() {
			List<Audio> audios;

			@Override
			protected Void doInBackground(Void... voids) {
				// Simulating delay to get more items from an API.
				try {
					// Log.e("Lisview items", ""+listviewadapter.getCount());
					// Log.e("totalItemsCount", ""+totalItemsCount);
					// Log.e("page", ""+page);

					Thread.sleep(1000);
					// audios = Manager.instance().getMusicFromPlaylistPaginate(
					// context, playListId, listviewadapter.getCount(),
					// ITEMS_PER_PAGE);
					//
					Log.e("listviewadapter: " + listviewadapter.getCount(),
							"musicList: " + musicList.size());

				} catch (Exception e) {
					e.getStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				// list.addFooterView(footerView, null, false);

				// if(totalItemsCount < totalAudioInDb){
				// Log.e("totalItemsCount: " + totalItemsCount,
				// "musicList: "+musicList.size());
				// for (Audio audio : audios) {
				// musics.add(audio);
				// }
				// }

				// list.removeFooterView(footerView);

			}
		}.execute();
	}

	public void onChooseMore(int position) {
		if (!selecteds.contains(listviewadapter.getItem(position))) {
			// selecteds.add(listviewadapter.getItem(position));
			// Log.i("adicionado",""+
			// listviewadapter.getItem(position).getId().intValue());
		} else {
			selecteds.remove(listviewadapter.getItem(position));
			// Log.i("removido", ""+
			// listviewadapter.getItem(position).getId().intValue());
		}

	}

	public void removeItems() {
		// try {
		// remove database
		// Manager.instance().removeMediaFromPlaylist(this.context,
		// this.playListId, this.selecteds);
		// //remove adapter
		// for(Audio audio : this.selecteds){
		// this.totalAudioInDb--;
		// this.listviewadapter.remove(audio);
		// }
		//
		// this.listviewadapter.setNotifyOnChange(true);
		// configureUI();
		//
		// } catch (DBException e) {
		// e.printStackTrace();
		// Log.i("DBException", "in saveMedia - MusicSelectFragment");
		// }
	}

	public void saveItems() {
		// TODO Auto-generated method stub

	}

}
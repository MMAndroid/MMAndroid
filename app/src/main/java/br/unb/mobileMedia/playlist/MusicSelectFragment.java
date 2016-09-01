package br.unb.mobileMedia.playlist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.core.extractor.MediaExtractor;
import br.unb.mobileMedia.core.manager.Manager;
import br.unb.mobileMedia.util.EndlessScrollListener;

public class MusicSelectFragment extends ListFragment {

	public final static String SELECTED_PLAYLIST_ID = "idPlaylist";
	public final static String SELECTED_PLAYLIST_NAME = "namePlaylist";

	
	public final int menuInflate = R.menu.save_audio_playlist;
	private final int ITEMS_PER_PAGE = 20;
	private Integer totalAudioInDb = 0;
	private List<Audio> audios = null;

	private List<Integer> selecteds = new ArrayList<Integer>();
	private int playListId;
	private String namePlaylist;

	private ArrayAdapterAudio adapter;
	private Context context;

	private MediaExtractor mediaExtractor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.context = getActivity().getApplicationContext();

		mediaExtractor = new DefaultAudioExtractor(context);
	
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle extras = getArguments();

		this.playListId = extras.getInt(SELECTED_PLAYLIST_ID);
		
		this.namePlaylist = extras.getString(SELECTED_PLAYLIST_NAME);

		getActivity().getActionBar().setSubtitle("Add Music in PLaylist: "+ namePlaylist);
			
		this.totalAudioInDb = Manager.instance().countMedias(context);
			
		Log.i("totalAudioInDb", ""+totalAudioInDb);
		
		loadMore(0, 0);//Show first Items in ListView
		
		//create adapter with kind of data: ArrayList<AudioViewItem>
		adapter = new ArrayAdapterAudio(context, R.layout.audio_row, new ArrayList<AudioViewItem>(),
				getListView());

		setListAdapter(adapter);

		configureUI();

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// remove the dividers from the ListView of the ListFragment

		getListView().setDivider(getResources().getDrawable(android.R.color.background_dark));
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
			Log.i("removido", "" +adapter.getItem(position).id.intValue());
		}
	}

	
	
	
	// updates the listview with all the musics from the DB
	private void configureUI() {
	
		getListView().setOnScrollListener(new EndlessScrollListener() {

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				Log.e("Page", ""+page);
				loadMore(page, totalItemsCount);
			}

		});

		getListView().setMultiChoiceModeListener(new MultiChoiceModeListener() {

			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(menuInflate, menu);
				return true;
			}

			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.saveMedia:
					saveItems();
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
	}


	/*
	 * save audios selecteds in playlistmedia
	 * */
	public void saveItems() {
		try {
			Manager.instance().addMediaToPlaylist(
					getActivity().getBaseContext(), playListId, selecteds);
			
			// Closes the activity. Only one music can be added per time.
			getActivity().getSupportFragmentManager().popBackStack();

		} catch (Exception e) {
			e.printStackTrace();
			Log.i("DBException", "in saveMedia - MusicSelectFragment");
		}
	}
	
	

	private void loadMore(final int page, final int totalItemsCount) {

//		Log.e("Page", page + "-" + totalItemsCount);

		new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected Void doInBackground(Void... voids) {
				// Simulating delay to get more items from an API.
				try {
				
//					Log.e("totalItemsCount: ", "" + totalItemsCount);
					
					int offset = 1 + ITEMS_PER_PAGE * (page-1);
					
					audios = Manager.instance().listAllAudioPaginated(context,	offset, ITEMS_PER_PAGE);
					
//					Log.i("doInBackground", ""+audios.size());
						
				} catch (DBException e) {
					Log.e("DBException", e.getMessage());
				}

				return null;
			}

			
			
			@Override
			protected void onPostExecute(Void result) {
				
				if(totalItemsCount < totalAudioInDb){
					adapter.swapItem(audioViewItem(audios));
				}
				
				Log.e("onPostExecute: ", "onPostExecute");

			}
		}.execute();
		
		
	}
	
	
	private List<AudioViewItem> audioViewItem(List<Audio> audios){
		
		List<AudioViewItem> mItemsTemp = new ArrayList<AudioViewItem>();
		for (Audio audio : audios){
			try {

				Integer id = audio.getId();
				String title = mediaExtractor.getTitle(audio.getUrl());
				String album = mediaExtractor.getAlbum(audio.getUrl());
				String author =  mediaExtractor.getAuthor(audio.getUrl());
				String bitRate = mediaExtractor.getBitRate(audio.getUrl());
				
				
				mItemsTemp.add(new AudioViewItem(id, title,
						album, author, bitRate));
				
			} catch (Exception e) {
				 Log.e("Exception", e.getMessage());
			}

		}
		
		return mItemsTemp;
	}

}

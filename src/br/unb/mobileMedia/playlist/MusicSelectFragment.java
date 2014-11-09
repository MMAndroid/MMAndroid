package br.unb.mobileMedia.playlist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.Exception.ExceptionMediaExtractor;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.core.extractor.MediaExtractor;
import br.unb.mobileMedia.core.manager.Manager;

public class MusicSelectFragment extends ListFragment{

	public final int menuInflate = R.menu.save_audio_playlist;
	private final int ITEMS_PER_PAGE = 9;

	private List<Audio> musicList = new ArrayList<Audio>();
	private List<Long> selecteds = new ArrayList<Long>();
	private int playListId;
	
	private ArrayAdapterAudio adapter;


	private Context context;

	private List<AudioViewItem> mItems;
	private MediaExtractor mediaExtractor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.context = getActivity().getApplicationContext();

		mItems = new ArrayList<AudioViewItem>();
		mediaExtractor = new DefaultAudioExtractor(context);


	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle extras = getArguments();

		this.playListId = extras
				.getInt(MainPlaylistListFragment.SELECTED_PLAYLIST_ID);
				
		try {
			getActivity().getActionBar().setSubtitle("Add Music in PLaylist: " + Manager.instance().getPlaylistById(getActivity(), playListId).getTitle());
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		configureUI();
		

		
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// remove the dividers from the ListView of the ListFragment
				
		getListView().setDivider(null);
		
		
		getListView().setOnScrollListener(new AbsListView.OnScrollListener(){
			public void onScrollStateChanged(AbsListView absListView, int i) {
//			  Log.e("onScrollStateChanged", "onScrollStateChanged");
			}
		 
		  	public void onScroll(AbsListView absListView,  int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		  
			  if (totalItemCount != 0 && totalItemCount == firstVisibleItem + visibleItemCount) {
			  
			  }
		 
		  	}
		  });
	}
	
	
	
	public void onChooseMore(int position) {
	  if (!selecteds.contains(mItems.get(position).id)) {
		  selecteds.add(mItems.get(position).id);
		  Log.i("adicionado",""+ mItems.get(position).id.intValue());
		  
	  } else {
		  selecteds.remove(mItems.get(position).id);
		  Log.i("removido", ""+   mItems.get(position).id.intValue());
	  }
	}
   

	// updates the listview with all the musics from the DB
	private void configureUI() {

		try {

			this.musicList = Manager.instance().listAllAudio(context);

			for (Audio audio : musicList) {

				Long   id    = null;
				String title = null;
				String album = null;
				Bitmap artAlbum = null;
				String author = null;
				String bitRate = null;

				try {

					this.mediaExtractor.setMMR(audio.getUrl());

					id    = audio.getId();
					title = audio.getTitle();
					album = this.mediaExtractor.getAlbum();
					artAlbum = this.mediaExtractor.getAlbumArt();
					author = this.mediaExtractor.getAuthor();
					bitRate = this.mediaExtractor.getBitRate();

				} catch (ExceptionMediaExtractor e1) {

					Log.e("ExceptionMediaExtractor", e1.getMessage());

				}

				mItems.add(new AudioViewItem(id, artAlbum, title, album, author, bitRate));
			}

		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		adapter = new ArrayAdapterAudio(context, R.layout.audio_row, mItems, getListView());
		
		setListAdapter(adapter);
		
		getListView().setSelector(R.drawable.list_selector);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		
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

			public void onItemCheckedStateChanged(ActionMode mode, int position,
					long id, boolean checked) {
				// Capture total checked items
				final int checkedCount = getListView().getCheckedItemCount();
				// Set the CAB title according to total checked items
				mode.setTitle(checkedCount + " Selected");
				
//				getListView().getAdapter().
				adapter.toggleSelection(position);
				
				onChooseMore(position);
			
			}
			
		});
		

	}



	 public void saveItems() {
		 try {
			 Manager.instance().addMediaToPlaylist( getActivity().getBaseContext(), playListId, selecteds);
			 // Closes the activity. Only one music can be added per time.
			 getActivity().getSupportFragmentManager().popBackStack();
	
		 } catch (DBException e) {
			 e.printStackTrace();
			 Log.i("DBException", "in saveMedia - MusicSelectFragment");
		 }
	 }
	
	
	//
	// public void removeItems() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// public void saveItems() {
	// try {
	//
	// Manager.instance().addMediaToPlaylist(
	// getActivity().getBaseContext(), playListId, selecteds);
	// // Closes the activity. Only one music can be added per time.
	// getActivity().getSupportFragmentManager().popBackStack();
	//
	// } catch (DBException e) {
	// e.printStackTrace();
	// Log.i("DBException", "in saveMedia - MusicSelectFragment");
	// }
	// }
	//
	// private void loadMore(final int page, final int totalItemsCount) {
	//
	// Log.e("Page", page + "-" + totalItemsCount);
	//
	// new AsyncTask<Void, Void, Void>() {
	// List<Audio> audios;
	//
	// @Override
	// protected Void doInBackground(Void... voids) {
	// // Simulating delay to get more items from an API.
	// try {
	// // Log.e("Lisview items", ""+ getCount());
	// Log.e("totalItemsCount", "" + totalItemsCount);
	// Log.e("page", "" + page);
	//
	// // Thread.sleep(1000);
	// // audios =
	// // Manager.instance().listAllAudioPaginated(context,
	// // listviewadapter.getCount(), ITEMS_PER_PAGE);
	// // audios = Manager.instance().getMusicFromPlaylistPaginate(
	// // context, playListId, listviewadapter.getCount(),
	// // ITEMS_PER_PAGE);
	//
	// // Log.e("listviewadapter: " + listviewadapter.getCount(),
	// // "musicList: "+musicList.size());
	//
	// } catch (Exception e) {
	// e.getStackTrace();
	// }
	//
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void result) {
	//
	// // list.addFooterView(footerView, null, false);
	// //
	// // if(totalItemsCount < totalAudioInDb){
	// // Log.e("totalItemsCount: " + totalItemsCount,
	// // "musicList: "+musicList.size());
	// // for (Audio audio : audios) {
	// // musics.add(audio);
	// // }
	// // }
	// //
	// // list.removeFooterView(footerView);
	//
	// }
	// }.execute();
	// }

}

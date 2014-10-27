package br.unb.mobileMedia.playlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;
import br.unb.mobileMedia.core.manager.Manager;
import br.unb.mobileMedia.core.view.AudioPlayerFragment;
import br.unb.mobileMedia.playlist.MultiChoiceMode;
import br.unb.mobileMedia.playlist.MultiChoiceMode.OnChooseMoreListener;

public class PlayListEditorFragment extends Fragment implements
		 OnChooseMoreListener {


	public final static String SELECTED_PLAYLIST_ID = "idPlaylist";
	public final static int menuInflate = R.menu.remove_audio_playlist;

	private final int ITEMS_PER_PAGE = 9;
	private int totalAudioInDb = 0;
	private View footerView;
	private ListView list = null;
	private int playListId;
	private int listviewselection;
	
	private Context context;
	private ArrayAdapterMusic listviewadapter;

	@SuppressLint("UseSparseArrays")
	private Map<Long, String> mapIdNameAuthor = new HashMap<Long, String>();
	private List<Audio> musicList = new ArrayList<Audio>();
	private List<Audio> musics = new ArrayList<Audio>();
	private List<Author> authors = new ArrayList<Author>();
	private List<Audio> selecteds = new ArrayList<Audio>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

//		Log.i("Recebendo PlayList", "Clicada");

		setHasOptionsMenu(true);
		
		this.context = getActivity().getApplicationContext();

		getActivity().getActionBar().setSubtitle(R.string.title_activity_playlist_editor);
		
		return inflater.inflate(R.layout.activity_playlist_editor, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// get from intent the playlist`s id.
		Bundle extras = getArguments();
		
		this.playListId = extras.getInt(MainPlaylistListFragment.SELECTED_PLAYLIST_ID);

		this.list = (ListView) getActivity().findViewById(R.id.list_musiclist);

		this.listviewadapter = new ArrayAdapterMusic(context,
				R.layout.music_row_select_from_playlist, musics,
				mapIdNameAuthor);

		this.footerView = ((LayoutInflater) getActivity().getSystemService(
				context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer,
				null, false);

		this.list.addFooterView(footerView, null, false);
		
		this.list.setAdapter(listviewadapter);

		try {
			this.musicList = Manager.instance().getMusicFromPlaylist(context, playListId);
			this.totalAudioInDb = this.musicList.size();
			this.authors = Manager.instance().listAuthors(context);

			for (Author author : this.authors) {
				this.mapIdNameAuthor.put(author.getId(), author.getName());
			}

		} catch (DBException e) {
			e.printStackTrace();
		}

				
		configureUI();
	}

	
	private void configureUI() {	
		refreshlist();
	}

	
	private void refreshlist() {
		
		this.listviewadapter = new ArrayAdapterMusic(context,
				R.layout.music_row_select_from_playlist, musics,
				mapIdNameAuthor);
		try {		
			// check if there is any playlist
			if (musicList == null || totalAudioInDb == 0) {
				Toast.makeText(getActivity().getApplicationContext(),
						"No music.", Toast.LENGTH_LONG).show();
			}else{
				
				this.list.setOnScrollListener(new EndlessScrollListener(){
					@Override
					public void onLoadMore(int page, int totalItemsCount){
						Log.i("OnLoadMore", ""+page);
						loadMore(page, totalItemsCount);
						listviewadapter.setNotifyOnChange(true);
					}
				});
				
				this.list.setMultiChoiceModeListener(new MultiChoiceMode(this, this.list, menuInflate));
				this.list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
				this.list.setSelection(listviewselection);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

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


	// catch result from another activity.
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("PlayListEditor", "Executing onActivityResult");
		refreshlist();
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
					audios = Manager.instance().getMusicFromPlaylistPaginate(
						context, playListId, listviewadapter.getCount(),
						ITEMS_PER_PAGE);
					
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
		try {
			//remove database
			Manager.instance().removeMediaFromPlaylist(this.context, this.playListId,	this.selecteds);
			//remove adapter
			for(Audio audio : this.selecteds){
				this.totalAudioInDb--;
				this.listviewadapter.remove(audio);
			}
			
			this.listviewadapter.setNotifyOnChange(true);
			configureUI();
			
		} catch (DBException e) {
			e.printStackTrace();
			Log.i("DBException", "in saveMedia - MusicSelectFragment");
		}		
	}

	public void saveItems() {
		// TODO Auto-generated method stub
		
	}



}
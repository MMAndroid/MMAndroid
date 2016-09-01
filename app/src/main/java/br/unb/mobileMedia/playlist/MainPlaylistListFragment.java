package br.unb.mobileMedia.playlist;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
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
import android.widget.EditText;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.manager.Manager;
import br.unb.mobileMedia.core.view.AudioPlayerFragment;
import br.unb.mobileMedia.util.AudioToParcelable;

/**
 * The main Fragment of the Playlist feature.
 * 
 * @author willian
 */

public class MainPlaylistListFragment extends ListFragment {

	private String names[]; // Store the Playlists names to display in the
							// ListView
	private List<Playlist> playlists;
	private List<PlaylistViewItem> mItems;
	private Context context;
	private ArrayAdapterPlaylist adapter;
	public final static String SELECTED_PLAYLIST_ID = "idPlaylist";
	public final static String SELECTED_PLAYLIST_NAME = "namePlaylist";
	

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.context = getActivity().getApplicationContext();

		mItems = new ArrayList<PlaylistViewItem>();


	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(getListView());
		
		configureUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		getActivity().setTitle(R.string.title_activity_play_list);

		Log.i("MainPlaylistListFragment", "MainPlaylistListFragment");

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private void configureUI() {
		refreshListPlayLists();
	}

	private void refreshListPlayLists() {

		playlists = null;
		adapter = null;
		mItems.clear();
		
		try {

			playlists = Manager.instance().listPlaylists(getActivity());
			
			// check if there is any playlist
			if (playlists == null || playlists.size() == 0) {
				names = new String[1];
				// TODO Refactor: extract string to xml
				names[0] = "No playlist found.";
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						names);

				this.setListAdapter(adapter);

			} else {
				
				
				for (Playlist p : playlists) {
					
				 	int audioInPlaylist = Manager.instance().getMusicFromPlaylist(context, p.getId()).size();
				 					 	
				 	audioInPlaylist = (audioInPlaylist == 0) ? 0 : (audioInPlaylist);
					
				 	Log.i("audioInPlaylist", Manager.instance().getMusicFromPlaylist(context, p.getId()).size()+"");
				 	
					mItems.add(new PlaylistViewItem(p.getId(), p.getName(),	""+audioInPlaylist));
				}

				adapter = new ArrayAdapterPlaylist(getActivity()
						.getApplicationContext(), R.layout.playlist_row,
						mItems, getListView());
				
				this.setListAdapter(adapter);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.activity_playlist_action_bar, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refreshActionBar:
			// To do insert animate rotate in this item of action bar
			refreshListPlayLists();
			break;
		case R.id.AddPlayListActionBar:
			createPlayList();
			break;
		default:
			Log.i("Message PL", "nao Implementado");
		}
		return super.onOptionsItemSelected(item);
	}

	private void createPlayList() {
		// Dialog (Alert) to get the information of the new playlist
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(R.string.btn_addPlaylist);
		alert.setMessage(R.string.name);
		// Set an EditText view to get user input
		final EditText input = new EditText(getActivity());
		alert.setView(input);
		// Ok button
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// get name new playlist
				String newPlayListName = input.getText().toString();
				try {
					Manager.instance().newPlaylist(
							getActivity().getApplicationContext(),
							new Playlist(null, newPlayListName));
				} catch (DBException e) {
					Log.i("Create PL Exception", e.getMessage());
				}
				// refresh the ViewList with recent added playlist
				refreshListPlayLists();
			}
		});
		// Cancel button
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
		alert.show();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.context_menu_main_playlist_fragment, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// find out which menu item was pressed
		switch (item.getItemId()) {
		case R.id.menu_context_manager_playlist:
			managaerPlaylist(item);
			Log.d("TAG", "id = menu_context_add_audio");
			return true;

		case R.id.menu_context_exec_this_playlist:
			execPlaylist(item);
			return true;

		case R.id.menu_context_add_position:
			addPositionFromPlaylist(item);
			return true;

		case R.id.menu_context_edit:
			editPlaylist(item);
			Log.d("TAG", "id = menu_context_edit");
			return true;

		case R.id.menu_context_delete:
			deletePlaylist(item);
			Log.d("TAG", "id = menu_context_delete");
			return true;

		default:
			return false;
		}
	}

	/*
	 * ######################################### 
	 * Item of Context Menu one single Playlist
	 * #########################################
	 */
	private void managaerPlaylist(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		Log.i("Id", mItems.get(info.position).id.toString());
		
		int idPlaylist      = mItems.get(info.position).id;
		String namePlaylist = mItems.get(info.position).name;

		Bundle args = new Bundle();
		args.putInt(SELECTED_PLAYLIST_ID, idPlaylist);
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

	}

	private void addPositionFromPlaylist(MenuItem item) {

	}

	private void execPlaylist(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		final Integer idPlaylist = mItems.get(info.position).id;
		List<Audio> listTmp = new ArrayList<Audio>();

		try {
		
			listTmp = Manager.instance().getMusicFromPlaylist(getActivity(), idPlaylist);

		} catch (DBException e) {
			e.getStackTrace();
		}
		
		
		AudioToParcelable[] audioToParcelable = new AudioToParcelable[listTmp.size()];
		
		for(int i = 0; i< listTmp.size(); i++){
			audioToParcelable[i] = new AudioToParcelable(listTmp.get(i));
		}
				
		Bundle args = new Bundle();
		args.putParcelableArray(AudioPlayerFragment.EXECUTION_LIST, audioToParcelable);
		


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

	private void editPlaylist(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		// Retrieve the playlist name that we will be working on...
		final String listItemName = names[info.position];

		Log.e("Name Item:", listItemName);

		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle("Edit Playlist");
		alert.setMessage("New Name:");
		// Set an EditText view to get user input
		final EditText input = new EditText(getActivity());

		alert.setView(input);

		// Ok button
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newName = input.getText().toString();
				Playlist editedPlaylist = null;

				try {

					editedPlaylist = Manager.instance().getPlaylistByName(
							getActivity(), listItemName);
					editedPlaylist.setName(newName);// playlist with new values
					Manager.instance().editPlaylist(getActivity(),
							editedPlaylist);

				} catch (DBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				refreshListPlayLists();
			}
		});

		alert.show();
	}
	
	
	private void deletePlaylist(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		// Retrieve the playlist name that we will be working on...
		final Long idPlaylist = (long)mItems.get(info.position).id;

		Log.e("Delete", ""+idPlaylist);

		try {

			Manager.instance().removePlaylist(context, idPlaylist.intValue());			
			refreshListPlayLists();
			
		} catch (DBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
				

	}

}
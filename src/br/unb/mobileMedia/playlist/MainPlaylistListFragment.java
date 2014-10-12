package br.unb.mobileMedia.playlist;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.EditText;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.domain.PlaylistOld;
import br.unb.mobileMedia.core.manager.Manager;

/**
 * The main activity of the playlist feature.
 * 
 * @author willian
 */
// TODO change the extends from MainPlaylistListActivity from Activity to
// ListActivity
public class MainPlaylistListFragment extends Fragment {
	// Store the Playlists names to display in the ListView
	private String names[];
	List<PlaylistOld> playlists;
	// String containing the playlist id that will be passed on to another
	// activity through an intent.
	public final static String SELECTED_PLAYLIST_ID = "idPlaylist";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.title_activity_play_list);
		return inflater.inflate(R.layout.activity_play_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		configureUI();
	}

	private void configureUI() {
		// ListView to show all playlists in a scrollable view
		ListView listPlayLists = (ListView) getActivity().findViewById(
				R.id.list_playlist);
		// Associar a ListView ao ContextMenu
		registerForContextMenu(listPlayLists);
		// Refresh the List View (List of Playlists)
		refreshListPlayLists();
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
							new PlaylistOld(newPlayListName));
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
		if (v.getId() == R.id.list_playlist) {
			menu.setHeaderTitle("Menu:");
			String[] menuItems = getResources().getStringArray(
					R.array.menu_playlist);
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
		// Retrieve the name of all of the options from the context menu
		/*
		 * String[] menuItems =
		 * getResources().getStringArray(R.array.menu_playlist); String
		 * menuItemName = menuItems[menuItemIndex];
		 */
		// Retrieve the playlist name that we will be working on...
		final String listItemName = names[info.position];
		/*
		 * 
		 * NEEDS REFACTORING!!!
		 */
		// Option - EDIT
		if (menuItemIndex == 0) {
			// GET NEW NAME
			// Dialog (Alert) to get the information of the new playlist
			AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Edit Playlist");
			alert.setMessage("New Name:");
			// Set an EditText view to get user input
			final EditText input = new EditText(getActivity());
			alert.setView(input);
			// Ok button
			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String newName = input.getText().toString();
							PlaylistOld editedPlaylist = null;
							try {
								editedPlaylist = Manager.instance().getSimplePlaylist(getActivity(), listItemName);
										
								// playlist with new values
								editedPlaylist.setName(newName);
								Manager.instance().editPlaylist(getActivity(),
										editedPlaylist);
							} catch (DBException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							refreshListPlayLists();
						}
					});
			// Cancel button
			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});
			alert.show();
		}
		// Option - REMOVE
		if (menuItemIndex == 1) {
			try {
				Manager.instance().removePlaylist(getActivity(), listItemName);
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			refreshListPlayLists();
		}
		// Add geographical position
		if (menuItemIndex == 2) {
			PlaylistOld playlist = null;
			StubGPS location = new StubGPS();
			try {
				playlist = Manager.instance().getSimplePlaylist(getActivity(),
						listItemName);
				// playlist with new values
				Manager.instance().addPositionPlaylist(getActivity(), playlist,
						location.getLatitude(), location.getLongitude());
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			refreshListPlayLists();
		}
		return true;
	}

	private void refreshListPlayLists() {
		// Update the List View
		ListView listPlayLists = (ListView) getActivity().findViewById(
				R.id.list_playlist);
		playlists = null;
		try {
			playlists = Manager.instance().listSimplePlaylists(getActivity());
			// check if there is any playlist
			if (playlists == null || playlists.size() == 0) {
				names = new String[1];
				// TODO Refactor: extract string to xml
				names[0] = "No playlist found.";
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						names);
				listPlayLists.setAdapter(adapter);
			} else {
				names = new String[playlists.size()];
				int i = 0;
				for (PlaylistOld p : playlists) {
					names[i++] = p.getName();
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						android.R.id.text1, names);
				listPlayLists.setAdapter(adapter);
				// ##############################################################################################
				// ##############################################################################################
				// Calls the Playlist Editor when a playlist is pressed.
				listPlayLists
						.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// if(position == 1) {
								// Get the selected playlist name!
								String selectedPlaylistName = (String) parent
										.getItemAtPosition(position);
								// Log de qual PlayList foi clicada
								Log.i("PlayList: ", selectedPlaylistName
										+ " Clicada.");
								PlaylistOld recoveredPlaylist = null;
								try {
									recoveredPlaylist = Manager
											.instance()
											.getSimplePlaylist(
													getActivity()
															.getApplicationContext(),
													selectedPlaylistName);
								} catch (DBException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								int message = recoveredPlaylist.getId();
								Bundle args = new Bundle();
								args.putInt(SELECTED_PLAYLIST_ID, message);
								// TODO Extract this to a method (repeated in
								// MMUnBActivity too)
								Fragment newFragment = new PlayListEditorFragment();
								newFragment.setArguments(args);
								FragmentTransaction transaction = getActivity()
										.getSupportFragmentManager()
										.beginTransaction();
								if (getActivity().findViewById(R.id.main) != null) {
									transaction.replace(R.id.main, newFragment);
									transaction.addToBackStack(null);
								} else {
									transaction.replace(R.id.content,
											newFragment);
									transaction.addToBackStack(null);
								}
								transaction.commit();
								// }
							}
						});
			}
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
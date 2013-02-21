package br.unb.mobileMedia.playlist;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.manager.Manager;

/**
 * The main activity of the playlist feature.
 * 
 * @author willian
 */

// TODO change the extends from MainPlaylistListActivity from Activity to ListActivity
public class MainPlaylistListActivity extends Activity {

	//Store the Playlists names to display in the ListView
	private String names[];
	List<Playlist> playlists;	
	
	//String containing the playlist id that will be passed on to another activity through an intent.
	public final static String SELECTED_PLAYLIST_ID = "idPlaylist";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_list);
		configureUI();
	}

	private void configureUI() {
		//ListView to show all playlists in a scrollable view
		ListView listPlayLists = (ListView) findViewById(R.id.list_playlist);

		//Associar a ListView ao ContextMenu
		registerForContextMenu(listPlayLists);
		
		// Add playlist button
		((Button)findViewById(R.id.btn_addPlaylist)).setOnClickListener(new View.OnClickListener(){     
			public void onClick(View v) {                

				//Dialog (Alert) to get the information of the new playlist
				AlertDialog.Builder alert = new AlertDialog.Builder(MainPlaylistListActivity.this);

				alert.setTitle("Add Playlist");
				alert.setMessage("Name:");

				// Set an EditText view to get user input 
				final EditText input = new EditText(MainPlaylistListActivity.this);
				alert.setView(input);
				//Ok button
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

						String value = input.getText().toString();
						try {
							Manager.instance().newPlaylist(getApplicationContext(), new Playlist(value));
						} catch (DBException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


						//refresh the ViewList with recent added playlist
						refreshListPlayLists ();
					}
				});
				//Cancel button
				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

				alert.show();  
			}        

		});

		//Refresh the List View (List of Playlists)
		refreshListPlayLists ();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_play_list, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId()==R.id.list_playlist) {

			menu.setHeaderTitle("Menu:");
			String[] menuItems = getResources().getStringArray(R.array.menu_playlist);
			for (int i = 0; i<menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		//Index number of the selected option from the context menu
		int menuItemIndex = item.getItemId();

		//Retrieve the name of all of the options from the context menu
		/*
      String[] menuItems = getResources().getStringArray(R.array.menu_playlist);
      String menuItemName = menuItems[menuItemIndex];
		 */

		//Retrieve the playlist name that we will be working on...
		final String listItemName = names[info.position];

		/*
		 * 
		 * NEEDS REFACTORING!!!
		 * 
		 */


		//Option - EDIT
		if(menuItemIndex == 0 ){
		
			//GET NEW NAME
			
			//Dialog (Alert) to get the information of the new playlist
			AlertDialog.Builder alert = new AlertDialog.Builder(MainPlaylistListActivity.this);

			alert.setTitle("Edit Playlist");
			alert.setMessage("New Name:");

			// Set an EditText view to get user input 
			final EditText input = new EditText(MainPlaylistListActivity.this);
			alert.setView(input);
			//Ok button
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int whichButton) {

					String newName = input.getText().toString();
					
					Playlist editedPlaylist = null;
					
					try {
						editedPlaylist = Manager.instance().getSimplePlaylist(MainPlaylistListActivity.this, listItemName);
						// playlist with new values
						editedPlaylist.setName(newName);
						
						Manager.instance().editPlaylist(MainPlaylistListActivity.this, editedPlaylist);
						
					} catch (DBException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					refreshListPlayLists ();
				}
			});
			//Cancel button
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			alert.show();  
	

		}
		//Option - REMOVE
		if(menuItemIndex == 1){
			try {
				Manager.instance().removePlaylist(this, listItemName);
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			refreshListPlayLists ();
		}
		
		//Add geographical position
		if(menuItemIndex == 2 ){
			
			
			Playlist playlist = null;
			StubGPS location = new StubGPS();
			
			try {
				playlist = Manager.instance().getSimplePlaylist(MainPlaylistListActivity.this, listItemName);
				// playlist with new values
				
				Manager.instance().addPositionPlaylist(this, playlist, location.getLatitude(), location.getLongitude());
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			refreshListPlayLists ();
		}
		
		return true;
	}

	private void refreshListPlayLists (){

		//Update the List View
		ListView listPlayLists = (ListView) findViewById(R.id.list_playlist);
		playlists = null;
		try {
			playlists = Manager.instance().listSimplePlaylists(this);
			
			// check if there is any playlist
			if (playlists == null || playlists.size() == 0) {
				names = new String[1];
				// TODO Refactor: extract string to xml
				names[0] = "No playlist found.";
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
				listPlayLists.setAdapter(adapter);
			} else {
				names = new String[playlists.size()];
				int i = 0;
				for (Playlist p : playlists) {
					names[i++] = p.getName();
				}
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
						android.R.layout.simple_list_item_1, 
						android.R.id.text1, 
						names);
				listPlayLists.setAdapter(adapter);
				
				//Calls the Playlist Editor when a playlist is pressed.
				listPlayLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		            
		            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		            //if(position == 1) {
		            Intent intent = new Intent(getApplicationContext(), PlayListEditorActivity.class);
		            //Get the selected playlist name!
		            String selectedPlaylistName = (String) parent.getItemAtPosition(position);
		            Playlist recoveredPlaylist = null;		
		            try {
						recoveredPlaylist = Manager.instance().getSimplePlaylist(getApplicationContext(), selectedPlaylistName);
					} catch (DBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            
		            int message = recoveredPlaylist.getId();
		            
		        	intent.putExtra(SELECTED_PLAYLIST_ID, message);
		            
		            startActivity(intent);
		            		//	}	
		            		}
		            });
		    	
				
			}
			
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}

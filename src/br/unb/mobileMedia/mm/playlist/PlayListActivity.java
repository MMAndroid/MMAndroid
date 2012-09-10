package br.unb.projetopositivo.mm.playlist;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import br.unb.projetopositivo.mm.R;

public class PlayListActivity extends Activity {
	
	//Store the Playlists names to display in the ListView
	private String names[];
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
       
        //ListView to show all playlists in a scrollable view
        ListView listPlayLists = (ListView) findViewById(R.id.list_playlist);
       
        //Associar a ListView ao ContextMenu
        registerForContextMenu(listPlayLists);
      
        //Variable to manage the playlist database
        PlayListManager manager = new PlayListManager(this);
        
        //Retrieve the database or create a new one.
        manager.open();
        
        Button btnAddPlaylist = (Button) findViewById(R.id.btn_addPlaylist);         
       
        btnAddPlaylist.setOnClickListener(new View.OnClickListener(){     
        	  

           public void onClick(View v) {                 
        	   
        	   	//Open the playlist manager to add a new playlist
        	    final PlayListManager manager = new PlayListManager(PlayListActivity.this);
                
        	    //Retrieve the database
        	    manager.open();
                
                //Dialog (Alert) to get the information of the new playlist
                AlertDialog.Builder alert = new AlertDialog.Builder(PlayListActivity.this);
               
                alert.setTitle("Add Playlist");
                alert.setMessage("Name:");

                // Set an EditText view to get user input 
                final EditText input = new EditText(PlayListActivity.this);
                alert.setView(input);
                //Ok button
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	               
                	public void onClick(DialogInterface dialog, int whichButton) {
	                	
	                	String value = input.getText().toString();
	                	manager.addPlayList(new PlayList(value, " "));
	                	
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
      String listItemName = names[info.position];
      
      /*
       * 
       * NEEDS REFACTORING!!!
       * 
       */
      
      
      //Option - EDIT
      if(menuItemIndex == 0 ){
    	  
    	  
      }
      //Option - REMOVE
      if(menuItemIndex == 1){
    	  
    	//Variable to manage the playlist database
          PlayListManager manager = new PlayListManager(this);
          
          //Retrieve the database or create a new one.
          manager.open();
          
          manager.deletePlayList(listItemName);
          
          refreshListPlayLists ();
    	  
      }
      return true;
    }
    
    private void refreshListPlayLists (){
    	
    	//Variable to manage the playlist database
        PlayListManager manager = new PlayListManager(this);
        
        //Retrieve the database or create a new one.
        manager.open();
        
  	  //Update the List View
        ListView listPlayLists = (ListView) findViewById(R.id.list_playlist);
        List<PlayList> playLists = manager.listAll();
        
        names = new String[playLists.size()];
        int i = 0;
        for (PlayList p : playLists) {
			names[i++] = p.getName();
		}
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_list_item_1, 
        		android.R.id.text1, 
        		names);
        listPlayLists.setAdapter(adapter);
    	
    
    }
    
}

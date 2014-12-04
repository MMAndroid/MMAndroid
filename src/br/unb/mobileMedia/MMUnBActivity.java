package br.unb.mobileMedia;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.manager.Manager;
import br.unb.mobileMedia.core.manager.Preferences;
import br.unb.mobileMedia.core.view.AlbumListFragment;
import br.unb.mobileMedia.core.view.AudioSelectFragment;
import br.unb.mobileMedia.core.view.AuthorListFragment;
import br.unb.mobileMedia.playlist.MainPlaylistListFragment;

@SuppressLint("NewApi")
public class MMUnBActivity extends FragmentActivity implements Observer{

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	private MenuItem menuItem;
	
	private Preferences preferences;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.main);
		
		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		// Array with icons of navDrawer
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
	
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		preferences = new Preferences(getApplicationContext());
		
//		preferences.SetSyncPreference(false, 0, 0, 0, 0);
		
		Manager.instance().addObserver(this);
		
		mTitle = mDrawerTitle = getTitle();

		navDrawerItems = createNavDrawer();
		
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
		
		adapter = new NavDrawerListAdapter(this.getApplicationContext(),navDrawerItems);

		mDrawerList.setAdapter(adapter);
		
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
			R.drawable.ic_drawer, R.string.drawer_open,
			R.string.drawer_close) {
			    
//			    /** Called when drawer is closed */
//			    public void onDrawerClosed(View view) {
//			        getActionBar().setTitle(mTitle);
//			        invalidateOptionsMenu();
//			    }
			    
			    /** Called when a drawer is opened */
			    public void onDrawerOpened(View drawerView) {
			        getActionBar().setTitle("Mobile Media");
			        invalidateOptionsMenu(); 
			    }	    
			};
			
			
		// Setting DrawerToggle on DrawerLayout
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		
		if (savedInstanceState == null) {
			displayView(0);// on first time display view for first nav item
		}

	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
				
		mDrawerToggle.syncState();
		 
		//se nao existir musica no banco de dados eh exec o primeiro sync
		if(Manager.instance().countMedias(getApplicationContext()) == 0){
			new SyncAudios(this.getApplicationContext()).execute();	
		}
		
		
		Log.i("Number playlist", Manager.instance().countPlaylists(getApplicationContext())+"");
		
	}
	
	
	public void update(Observable observable, Object data) {
		
		preferences.setTotalAuthor(Manager.instance().countAuthors(getApplicationContext()).intValue());
		preferences.setTotalAlbum(Manager.instance().countAlbum(getApplicationContext()).intValue());
		preferences.setTotalAudio(Manager.instance().countMedias(getApplicationContext()).intValue());
		preferences.setTotalPlaylist(Manager.instance().countPlaylists(getApplicationContext()).intValue());
		
		adapter.clear();
		
		
		runOnUiThread (new Thread(new Runnable() { 
	         public void run(){
	    
	        	 adapter.swapItems(createNavDrawer());
	        	 
	         }
		}));

		
		Log.i("Observable", "Observer MMUnB ");
		
	}


	private ArrayList<NavDrawerItem> createNavDrawer(){
		
		ArrayList<NavDrawerItem> items = new ArrayList<NavDrawerItem>();
		
		// adding nav drawer items to array
		items.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));		// Home
		
		items.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1), true,
				preferences.getTotalAuthor()+""));// Authors
		
		items.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1), true,
				preferences.getTotalAlbum()+""));// Albums
		
		items.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, 
				preferences.getTotalPlaylist()+""));// Playlist
		
		items.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1), true,
				preferences.getTotalAudio()+""	));// Audios
		
		return items;		
		
	}
	

	/* Slide menuItem click listener */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Menu actionBar()
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}



	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		
			case R.id.syncFiles:
				menuItem = item;
				menuItem.setActionView(R.layout.sync_files_load);
				menuItem.expandActionView();
				// Sync files when is clicked
				new SyncAudios(this.getApplicationContext()).execute();
				break;
			
			case R.id.action_settings:
				break;
			default:
				break;
		}
		
		return super.onOptionsItemSelected(item);

	}
	
	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
//		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}
	
	
	private void displayView(int position) { 
		// update the main content by replacing fragments

		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new ExampleFragment();
			break;
			
		 case 1:
			 fragment = new AuthorListFragment();
		 break;
		 
		 case 2:
			 fragment = new AlbumListFragment();
		 break;
		 
		 case 3:
			 fragment = new MainPlaylistListFragment();
			 break;
			 
		 case 4:
			 fragment = new AudioSelectFragment();
			 break;
		
		default:
			break;
		}


		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.main, fragment).commit();
			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}


	private void exit() {
		try {
			Manager.instance().synchronizeMedia(getApplicationContext());
			Toast.makeText(getApplicationContext(),
					R.string.message_synchronization_finished,
					Toast.LENGTH_LONG).show();
		} catch (DBException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	
	
	// Parametro, Progresso, Resultado
	private class SyncAudios extends AsyncTask<Void, Void, Void> {

		private Context context;

		public SyncAudios(Context c) {
			context = c;
		}

		@Override
		protected Void doInBackground(Void... v) {

			try {
				
				Manager.instance().synchronizeMedia(context);
				
			} catch (DBException e) {
				Log.i("Exception", e.getCause().toString());
				e.getStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			if (menuItem != null && menuItem.getActionView() != null) {
				menuItem.collapseActionView();
				menuItem.setActionView(null);
			}
			
			Log.i("onPostExecute", "MMUnBActivity");

		}

	}



}

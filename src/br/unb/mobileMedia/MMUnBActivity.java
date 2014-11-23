package br.unb.mobileMedia;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
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
import br.unb.mobileMedia.core.view.AlbumListFragment;
import br.unb.mobileMedia.core.view.AudioSelectFragment;
import br.unb.mobileMedia.core.view.AuthorListFragment;
import br.unb.mobileMedia.playlist.MainPlaylistListFragment;

@SuppressLint("NewApi")
public class MMUnBActivity extends FragmentActivity {

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
	private ActionBar actionBar;
	private SyncFiles syncFiles;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.main);

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));		// Authors
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));		// Albums
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));		// Playlist
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));		// Audios

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),	navDrawerItems);
		
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
				    
				    /** Called when drawer is closed */
				    public void onDrawerClosed(View view) {
				        getActionBar().setTitle(mTitle);
				        invalidateOptionsMenu();
				    }
				    
				    /** Called when a drawer is opened */
				    public void onDrawerOpened(View drawerView) {
				        getActionBar().setTitle("Mobile Media");
				        invalidateOptionsMenu(); 
				    }
				    
				};

				// Setting DrawerToggle on DrawerLayout
				mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}


		// SyncFile auto
		syncFiles = new SyncFiles(this);
		syncFiles.execute();

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
		// getMenuInflater().inflate(R.menu.activity_mmunb_action_bar, menu);
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//
//		Fragment newFragment = null;
//
//		switch (item.getItemId()) {
//		case R.id.exitActionBar:
//			MMUnBActivity.this.finish();
//			break;
//
//		case R.id.syncFiles:
//			menuItem = item;
//			menuItem.setActionView(R.layout.sync_files_load);
//			menuItem.expandActionView();
//
//			// Sync files when is clicked
//			new SyncFiles(this).execute();
//
//			break;
//
//		case R.id.twiiterAction:
//			newFragment = new ShareListFragment();
//			break;
//
//		case R.id.PlayList:
//			newFragment = new MainPlaylistListFragment();
//			break;
//
//		}

		// if (newFragment != null) {
		// FragmentManager manager = getSupportFragmentManager();
		// FragmentTransaction transaction = manager.beginTransaction();
		//
		// if (findViewById(R.id.main) != null) {
		// transaction.replace(R.id.main, newFragment);
		// transaction.addToBackStack(null);
		// } else {
		// transaction.replace(R.id.content, newFragment);
		// }
		// transaction.commit();
		// }
		//
//		return super.onOptionsItemSelected(item);
//	}

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

//	public void onItemClicked(int menuItem) {
//		Fragment newFragment = null;
//		switch (menuItem) {
//		case R.id.btn_list_authors:
//			newFragment = new AuthorListFragment();
//			break;
//
//		case R.id.btn_open_music_player:
//			newFragment = new AudioPlayerFragment();
//			break;
//
//		case R.id.exitActionBar:
//			exit();
//			break;
//
////		 case R.id.btn_synchronize:
////		 try {
////		 Manager.instance().synchronizeMedia(getApplicationContext());
////		 Toast.makeText(getApplicationContext(),
////		 R.string.message_synchronization_finished, Toast.LENGTH_LONG).show();
////		 }catch(DBException e) {
////		 Toast.makeText(getApplicationContext(), e.getMessage(),
////		 Toast.LENGTH_LONG).show();
////		 }
////		 break;

//		default:
//			Toast.makeText(getApplicationContext(),
//					R.string.need_to_be_implemented, Toast.LENGTH_LONG).show();
//		}
		// TODO Extract this to a method (repeated in AuthorListFragment too)
//		if (newFragment != null) {
//			FragmentManager manager = getSupportFragmentManager();
//			FragmentTransaction transaction = manager.beginTransaction();
//
//			if (findViewById(R.id.main) != null) {
//				transaction.replace(R.id.main, newFragment);
//				transaction.addToBackStack(null);
//			} else {
//				transaction.replace(R.id.content, newFragment);
//			}
//			transaction.commit();
//		}
//
//	}

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
	private class SyncFiles extends AsyncTask<Void, Void, Void> {

		private Context context;
		private Integer TotalPlaylist;
		private Integer TotalAudio;
		private Integer TotalAuthor;
		private Integer TotalAlbum;


		public SyncFiles(Context c) {
			context = c;
		}

		@Override
		protected Void doInBackground(Void... v) {

			try {

				Manager.instance().synchronizeMedia(context);
				
				TotalAuthor   = Manager.instance().listAuthors(context).size();
				TotalAlbum    = Manager.instance().countAllAlbum(context).intValue();
				TotalPlaylist = Manager.instance().listPlaylists(getApplicationContext()).size();
				TotalAudio    = Manager.instance().countAllAudio(context).intValue();
				
			} catch (Exception e) {
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
			
			navDrawerItems.get(1).setCounterVisibility(true);
			navDrawerItems.get(2).setCounterVisibility(true);
			navDrawerItems.get(3).setCounterVisibility(true);
			navDrawerItems.get(4).setCounterVisibility(true);
			
			navDrawerItems.get(1).setCount(TotalAuthor.toString());
			navDrawerItems.get(2).setCount(TotalAlbum.toString());
			navDrawerItems.get(3).setCount(TotalPlaylist.toString());
			navDrawerItems.get(4).setCount(TotalAudio.toString());

			adapter = null;
			adapter =  new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
			
			mDrawerList.setAdapter(adapter);
			
			Log.i("onPostExecute", "MMUnBActivity");

		}

	};

}

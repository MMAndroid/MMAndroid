package br.unb.mobileMedia;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import br.unb.mobileMedia.activities.ActivityQuit;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.services.ServicePlayMusic;
import br.unb.mobileMedia.services.ServicePlayMusic.MusicBinder;

/**
 * Just a class that contains the logic behind the player.
 *
 * This class contains logic that are shared between the Activities.
 *
 */
public class MMAndroid {

	/**
	 * All the app's configurations/preferences/settings.
	 */
	public static Settings settings = new Settings();

	/**
	 * Our custom service that allows the music to play
	 * even when the app is not on focus.
	 */
	public static ServicePlayMusic musicService = null;
	
//	/**
//	 * Contains the songs that are going to be shown to
//	 * the user on a particular menu.
//	 *
//	 * @note IGNORE THIS - don't mess with it.
//	 *
//	 * Every `ActivityMenu*` uses this temporary variable to
//	 * store subsections of `SongList` and set `ActivityListSongs`
//	 * to display it.
//	 */
//	public static ArrayList<Song> musicList = null;

	/**
	 * List of the songs being currently played by the user.
	 */
	public static List<Audio> nowPlayingList = null;

	/**
	 * Flag that tells if the Main Menu has an item that
	 * sends the user to the Now Playing Activity.
	 *
	 * It's here because when firstly initializing the
	 * application, there's no Now Playing Activity.
	 */
	public static boolean mainMenuHasNowPlayingItem = false;

	// GENERAL PROGRAM INFO
	public static String applicationName = "kure Music Player";
	public static String packageName = "<unknown>";
	public static String versionName = "<unknown>";
	public static int    versionCode = -1;

	/**
	 * Retrieving information. Call only at the beginning.
	 */
	public static void initialize(Context c) {

		MMAndroid.packageName = c.getPackageName();

		try {
			//Retrieving information
			PackageInfo info = c.getPackageManager().getPackageInfo(MMAndroid.packageName, 0);
			MMAndroid.versionName = info.versionName;
			MMAndroid.versionCode = info.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			//Already started with default values.
		}
	}

	/**
	 * Clean everything, call at the end.
	 */
	public static void destroy() {
		//TODO: clean
	}

	/**
	 * Connection to the MusicService. Started with an Intent.
	 */
	public static ServiceConnection musicConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			MusicBinder binder = (MusicBinder)service;

			//Create the MusicService
			musicService = binder.getService();
			musicService.setList(MMAndroid.nowPlayingList);
			musicService.musicBound = true;
		}

		public void onServiceDisconnected(ComponentName name) {
			musicService.musicBound = false;
		}
	};

	private static Intent musicServiceIntent = null;

	/**
	 * Initializes the Music Service at Activity/Context c.
	 *
	 * @note Only starts the service once - does nothing when
	 *       called multiple times.
	 */
	public static void startMusicService(Context c) {
		if (musicServiceIntent != null) {
			return;
		}
		
		if (MMAndroid.musicService != null) {
			return;
		}

		//Intent to bind Music Connection to the MusicService.
		musicServiceIntent = new Intent(c, ServicePlayMusic.class);
		c.bindService(musicServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
		c.startService(musicServiceIntent);
	}

	/**
	 * Service stop and clean itself
	 */
	public static void stopMusicService(Context c) {
		if (musicServiceIntent == null) {
			return;
		}
		c.stopService(musicServiceIntent);
		musicServiceIntent = null;
		MMAndroid.musicService = null;
	}

	/**
	 * Forces the application to exit.
	 */
	public static void forceExit(Activity c) {
		Intent intent = new Intent(c, ActivityQuit.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		// Clear all other Activities
		c.startActivity(intent);
		c.finish();												//Clear the Activity calling this function
	}
	
}

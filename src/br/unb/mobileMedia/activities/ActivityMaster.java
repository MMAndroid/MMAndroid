package br.unb.mobileMedia.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import br.unb.mobileMedia.MMAndroid;
import br.unb.mobileMedia.R;

/**
 * Master Activity from which others Activity inherits
 */
@SuppressLint("Registered")
public class ActivityMaster extends Activity {

	//The current theme name
	protected String currentTheme = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		refreshTheme();
		MMAndroid.startMusicService(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		//Check for theme changes
		if (refreshTheme()) {
			recreate();
		}
	}

	/**
	 * Tests if our current theme is the same as the one
	 * specified on `Settings`, reapplying the theme if
	 * not the case.
	 *
	 * @return Flag that tells if we've changed the theme.
	 */
	public boolean refreshTheme() {
		String theme = MMAndroid.settings.get("themes", "default");

		if (currentTheme != theme) {
			//Testing
//			if(theme.equals("default")) {
//				setTheme();
//			} else if (theme.equals("light")) {
//				setTheme();
//			}

			currentTheme = theme;
			return true;
		}
		return false;
	}

	/**
	 * Let's set a context menu (menu that appears when
	 * the user presses the "menu" button).
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Default options
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_context, menu);

		if (MMAndroid.mainMenuHasNowPlayingItem) {
			menu.findItem(R.id.context_menu_now_playing).setVisible(true);
		}

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * This method gets called whenever the user clicks an
	 * item on the context menu.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.context_menu_end:
				MMAndroid.forceExit(this);
				break;
			case R.id.context_menu_settings:
				//startActivity(new Intent(this, ActivityMenuSettings.class));
				break;
			case R.id.context_menu_now_playing:
				Intent nowPlayingIntent = new Intent(this, ActivityNowPlaying.class);
				nowPlayingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(nowPlayingIntent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}


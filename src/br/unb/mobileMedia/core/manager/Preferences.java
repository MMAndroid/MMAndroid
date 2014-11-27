package br.unb.mobileMedia.core.manager;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences{ 

	public static final String IS_SYNC = "br.com.mobileMedia.sync.isSync";
	public static final String TOTAL_AUDIO = "br.com.mobileMedia.sync.totalAudio";
	public static final String TOTAL_ALBUM = "br.com.mobileMedia.sync.totalAlbum";
	public static final String TOTAL_AUTHOR = "br.com.mobileMedia.sync.totalAuthor";
	public static final String TOTAL_PLAYLIST = "br.com.mobileMedia.sync.totalPlaylist";
	
	private SharedPreferences preferences;
	private Context context;
	
	public Preferences(Context c){
		this.context = c;
		this.preferences = this.context.getSharedPreferences("br.unb.mobileMedia", Context.MODE_PRIVATE);
	}
	
	
	public void SetSyncPreference(boolean isSync, int totalAudio, int totalAlbum, int totalAuthor, int totalPlaylist){
		
		Editor editor = this.preferences.edit();
		
		editor.putBoolean(IS_SYNC, isSync);
		editor.putInt(TOTAL_AUDIO, totalAudio);
		editor.putInt(TOTAL_ALBUM, totalAlbum);
		editor.putInt(TOTAL_AUTHOR, totalAuthor);
		editor.putInt(TOTAL_PLAYLIST, totalPlaylist);
		editor.commit();
		
	}

	
	public void setTotalAudio(int totalAudio){
		Editor editor = this.preferences.edit();
		editor.putInt(TOTAL_AUDIO, totalAudio);
		editor.commit();
	}
	
	
	public void setTotalAlbum(int totalAlbum){
		Editor editor = this.preferences.edit();
		editor.putInt(TOTAL_ALBUM, totalAlbum);
		editor.commit();
	}

	
	public void setTotalAuthor(int totalAuthor){
		Editor editor = this.preferences.edit();
		editor.putInt(TOTAL_AUTHOR, totalAuthor);
		editor.commit();
	}
	
	public void setTotalPlaylist(int totalPlaylist){
		Editor editor = this.preferences.edit();
		editor.putInt(TOTAL_PLAYLIST, totalPlaylist);
		editor.commit();
	}
	
	public boolean getIsSync(){
		return this.preferences.getBoolean(IS_SYNC, false);
	}
	
	public int getTotalAuthor(){
		return preferences.getInt(TOTAL_AUTHOR, 0);
	}

	public int getTotalAlbum(){
		return preferences.getInt(TOTAL_ALBUM, 0);
	}
	
	public int getTotalAudio(){
		return preferences.getInt(TOTAL_AUDIO, 0);
	}
	
	
	public int getTotalPlaylist(){
		return preferences.getInt(TOTAL_PLAYLIST, 0);
	}	
}

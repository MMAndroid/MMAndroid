package br.unb.projetopositivo.mm.view.playlist;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * A manager class for Play Lists
 * 
 * @author positivo unb team
 */
public class PlayListManager {

	private Context context;
	private SQLiteDatabase db;
	private PlayListDBhelper dbHelper;
	
	/**
	 * A basic constructor expecting an application context.
	 * The context is mainly necessary to handle database operations, since 
	 * we should access the proper database. 
	 *  
	 * @param c the application context
	 */
	public PlayListManager(Context c){
		context = c;
		
		dbHelper = new PlayListDBhelper(context, PlayListDBConstants.DATABASE_NAME, null, PlayListDBConstants.DATABASE_VERSION);
	}
	/**
	 * Open the connection with the database.
	 */
	public void open() {
		try {
			db = dbHelper.getWritableDatabase();
		}
		catch(SQLiteException e) {
			Log.w("A fail occured while opening the playlist database", e.getMessage());
			db = dbHelper.getReadableDatabase();
		}
	}
	

	/**
	 * Add a playlist to the database
	 */
	public void addPlayList(PlayList playList) {
		ContentValues values = new ContentValues();
		
		values.put(PlayListDBConstants.NAME_COLUMN, playList.getName());
		values.put(PlayListDBConstants.DESCRIPTION_COLUMN, playList.getDescription());
		
		db.beginTransaction();
		try {
			db.insert(PlayListDBConstants.PLAYLIST_TABLE, null, values);
			db.setTransactionSuccessful();
		}
		catch(SQLiteException e) {
			Log.v("Failed to insert a playlist", e.getMessage());
		}
		finally {
			db.endTransaction();
		}
	}
	
	/**
	 * Delete a playlist from the database.
	 * 
	 * @param name The name of the playlist that will 
	 * be removed from the database.
	 */
	public void deletePlayList(String name) {
		db.beginTransaction();
		try {
			db.delete(PlayListDBConstants.PLAYLIST_TABLE, PlayListDBConstants.NAME_COLUMN + "=?", new String[] { name });
			db.setTransactionSuccessful();		
		}
		catch(SQLiteException e) {
			Log.v("Failed while trying to delete a playlist", e.getMessage());
		}
		finally {
			db.endTransaction();
		}
	}
	
	/**
	 * Delete all playlists from the database. It is really 
	 * dangerous to call this method. 
	 * 
	 * @deprecated We have to decide if this method should be implemented or not. 
	 */
	public void deleteAllPlayLists() {
		db.beginTransaction();
		try {
			db.delete(PlayListDBConstants.PLAYLIST_TABLE, null, null);
			db.setTransactionSuccessful();	
		}
		catch(SQLiteException e) {
			Log.v("Failed while trying to delete all playlists", e.getMessage());
		}
		finally {
			db.endTransaction();
	
		}
	}
	
	/**
	 * Retrieves the list of playlists. 
	 * @return The list of playlists in the database
	 */
	public List<PlayList> listAll() {
		Cursor cursor = db.rawQuery(PlayListDBConstants.QUERY, null);
		
		List<PlayList> playLists = new ArrayList<PlayList>();
		
		if(cursor.moveToFirst()){
			do {
				String name = cursor.getString(cursor.getColumnIndex(PlayListDBConstants.NAME_COLUMN));
				String description = cursor.getString(cursor.getColumnIndex(PlayListDBConstants.DESCRIPTION_COLUMN));
			
				playLists.add(new PlayList(name, description));
			} while(cursor.moveToNext());
		}
		return playLists;
	}
}

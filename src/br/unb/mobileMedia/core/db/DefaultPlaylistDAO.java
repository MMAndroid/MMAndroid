package br.unb.mobileMedia.core.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Playlist;

/**
 * Default implementation of PlaylistDAO.
 * 
 * @author willian
 */

public class DefaultPlaylistDAO implements PlaylistDAO {
	
	private Context context;
	private SQLiteDatabase db;
	private DBHelper dbHelper;

	/**
	 * Constructor of DefaultPlaylistDAO.
	 * 
	 * @param c the application context.
	 */
	public DefaultPlaylistDAO(Context c) {
		context = c;
		dbHelper = new DBHelper(context, DBConstants.DATABASE_NAME, null,
				DBConstants.DATABASE_VERSION);
	}
	
	/**
	 * @see PlaylistDAO#newPlaylist(Playlist playlist)
	 */
	public void newPlaylist(Playlist playlist) throws DBException {
		try {
			db = dbHelper.getWritableDatabase();

			Cursor cursor = db.rawQuery(DBConstants.SELECT_SIMPLE_PLAYLIST_BY_NAME,
					new String[] { playlist.getName() });

			if (cursor.getCount() == 0) {
				// here we must save the playlist, since it does not exist.
				//Create a Row in DBConstants.PLAYLIST_TABLE
				ContentValues values = new ContentValues();
				

				values.put(DBConstants.PLAYLIST_NAME_COLUMN, playlist.getName());

				db.beginTransaction();
				db.insert(DBConstants.PLAYLIST_TABLE, null, values);
				db.setTransactionSuccessful();
				
				//Create a Row in DBConstants.TB_MEDIA_FROM_PLAYLIST
				/*
				 * 
				 */
			}
//			else
//				 TODO externalize the string to the xml file
//				throw new Exception("Playlist already exists");
		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultPlaylistDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		} finally {
			if (db.inTransaction()) {
				db.endTransaction();
			}
			db.close();
			dbHelper.close();
		}
	}

	public void addToPlaylist(int idPlaylist, List<Integer> mediaList)
			throws DBException {
		// TODO Needs to be tested.
		try {
			db = dbHelper.getWritableDatabase();


				// here we need a for to save each music into the table
				for (Integer musicID : mediaList) {  
				    
				
				ContentValues values = new ContentValues();

				values.put(DBConstants.MEDIA_FROM_PLAYLIST_FK_PLAYLIST, idPlaylist);
				values.put(DBConstants.MEDIA_FROM_PLAYLIST_FK_MEDIA, musicID);

				db.beginTransaction();
				db.insert(DBConstants.MEDIA_FROM_PLAYLIST_TABLE, null, values);
				db.setTransactionSuccessful();
				
				
				}
				

		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultPlaylistDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		} finally {
			if (db.inTransaction()) {
				db.endTransaction();
			}
			db.close();
			dbHelper.close();
		}
		
		

	}

	public List<Playlist> listPlaylists() throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Playlist> listSimplePlaylists() throws DBException {
		try {
			db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(DBConstants.SELECT_SIMPLE_PLAYLIST, null);

			List<Playlist> playlists = new ArrayList<Playlist>();

			if (cursor.moveToFirst()) {
				do {
					Playlist playlist = cursorToPlaylist(cursor);
					playlists.add(playlist);
				} while (cursor.moveToNext());
			}
			cursor.close();
			return playlists;
		} catch (SQLiteException e) {
			Log.e(DefaultPlaylistDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		} finally {
			db.close();
			dbHelper.close();
		}
	}
	
	public Playlist getSimplePlaylist(int idPlaylist) throws DBException {
		db = dbHelper.getReadableDatabase();
		
		//Converts the playlist id to string to the SQL query
		String name = Integer.toString(idPlaylist);
		Cursor cursor = db.rawQuery(DBConstants.SELECT_SIMPLE_PLAYLIST_BY_ID,
				new String[] { name });

		if (cursor.getCount() == 0) {
			return null;
		}

		cursor.moveToFirst();
		db.close();
		dbHelper.close();
		return cursorToPlaylist(cursor);
	}
	
	/**
	 * @see PlaylistDAO#getPlaylist(String name)
	 */
	public Playlist getSimplePlaylist(String name) throws DBException {
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(DBConstants.SELECT_SIMPLE_PLAYLIST_BY_NAME,
				new String[] { name });

		if (cursor.getCount() == 0) {
			return null;
		}

		cursor.moveToFirst();
		db.close();
		dbHelper.close();
		return cursorToPlaylist(cursor);
	}
	
	public Playlist getPlaylist(int idPlaylist) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Playlist getPlaylist(String name) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @see PlaylistDAO#deletePlaylist(String namePlaylist)
	 */
	public void deletePlaylist(String namePlaylist) throws DBException {
		db = dbHelper.getReadableDatabase();
		try {
			db.beginTransaction();
			db.delete(DBConstants.PLAYLIST_TABLE, DBConstants.PLAYLIST_NAME_COLUMN + "=?", new String[] { namePlaylist });
			db.setTransactionSuccessful();		
		}
		catch(SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultPlaylistDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
		}
		finally {
			if (db.inTransaction()) {
				db.endTransaction();
			}
			db.close();
			dbHelper.close();
		}
	}

	public void editPlaylist(Playlist editedPlaylist) throws DBException {
		
		try {
			db = dbHelper.getWritableDatabase();

			Cursor cursor = db.rawQuery(DBConstants.SELECT_SIMPLE_PLAYLIST_BY_ID,
					new String[] { "" + (editedPlaylist.getId()) });

			if (cursor.getCount() != 0) {
				// here we must save the new playlist name, since it exists.
				//Update a Row in DBConstants.PLAYLIST_TABLE
				ContentValues values = new ContentValues();
				

				values.put(DBConstants.PLAYLIST_NAME_COLUMN, editedPlaylist.getName());
				values.put(DBConstants.PLAYLIST_ID_COLUMN, editedPlaylist.getId());

				db.beginTransaction();
				db.update(DBConstants.PLAYLIST_TABLE, values, DBConstants.PLAYLIST_ID_COLUMN + "=" + editedPlaylist.getId(), null);
				db.setTransactionSuccessful();
				
				//Create a Row in DBConstants.TB_MEDIA_FROM_PLAYLIST
				/*
				 * 
				 */
			}
//			else
//				 TODO externalize the string to the xml file
//				throw new Exception("Playlist already exists");
		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultPlaylistDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		} finally {
			if (db.inTransaction()) {
				db.endTransaction();
			}
			db.close();
			dbHelper.close();
		}
		
	}
	
	public void addPositionPlaylist(Playlist Playlist, double latitude, double longitude) throws DBException {
		
		try {
			db = dbHelper.getWritableDatabase();

			Cursor cursor = db.rawQuery(DBConstants.SELECT_SIMPLE_PLAYLIST_BY_ID,
					new String[] { "" + (Playlist.getId()) });

			if (cursor.getCount() != 0) {
				//Delete a geographical position in PLAYLIST_LOCATION_TABLE			
				db.beginTransaction();
				db.delete(DBConstants.PLAYLIST_LOCATION_TABLE, DBConstants.PLAYLIST_LOCATION_FK_PLAYLIST + "=?", new String[] { "" + Playlist.getId() });
				db.setTransactionSuccessful();
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultPlaylistDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		} finally {
			if (db.inTransaction()) {
				db.endTransaction();
			}
			db.close();
			dbHelper.close();
		}
		
		try {
			db = dbHelper.getWritableDatabase();

			Cursor cursor = db.rawQuery(DBConstants.SELECT_SIMPLE_PLAYLIST_BY_ID,
					new String[] { "" + (Playlist.getId()) });

			if (cursor.getCount() != 0) {
				//Add a geographical position in PLAYLIST_LOCATION_TABLE
				ContentValues values = new ContentValues();

				values.put(DBConstants.PLAYLIST_LOCATION_FK_PLAYLIST, Playlist.getId());
				values.put(DBConstants.PLAYLIST_LOCATION_LATITUDE, latitude);
				values.put(DBConstants.PLAYLIST_LOCATION_LONGITUDE, longitude);
				
				db.beginTransaction();
				db.insert(DBConstants.PLAYLIST_LOCATION_TABLE, null, values);
				db.setTransactionSuccessful();
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultPlaylistDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		} finally {
			if (db.inTransaction()) {
				db.endTransaction();
			}
			db.close();
			dbHelper.close();
		}
		
	}
	
	public void removeMedias(int idPlaylist, List<Integer> mediaList) throws DBException {
		
		try {
			db = dbHelper.getWritableDatabase();

			String stringIdPlaylist = new String();
			String stringMusicId;
				// here we need a for to save each music into the table
				for (Integer musicID : mediaList) {  
				    
			
				stringIdPlaylist = String.valueOf(idPlaylist);
				stringMusicId =  String.valueOf(musicID);
				db.beginTransaction();
				db.delete(DBConstants.MEDIA_FROM_PLAYLIST_TABLE, DBConstants.MEDIA_FROM_PLAYLIST_FK_PLAYLIST + "=" +
						stringIdPlaylist + " AND " + DBConstants.MEDIA_FROM_PLAYLIST_FK_MEDIA + "=" +
						stringMusicId, null);
				db.setTransactionSuccessful();
				
				
				}
				

		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultPlaylistDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		} finally {
			if (db.inTransaction()) {
				db.endTransaction();
			}
			db.close();
			dbHelper.close();
		}
		
		
	}
	
	/*
	 * Converts a cursor into an Playlist.
	 */
	private Playlist cursorToPlaylist(Cursor cursor) {
		int id = cursor.getInt(cursor
				.getColumnIndex(DBConstants.PLAYLIST_ID_COLUMN));
		String name = cursor.getString(cursor
				.getColumnIndex(DBConstants.PLAYLIST_NAME_COLUMN));
		Playlist playlist = new Playlist(name);
		playlist.setId(id);
		return playlist;
	}

	public List<Audio> getMusicFromPlaylist(int idPlaylist) throws DBException {
		List<Integer> mediaList = new ArrayList<Integer>();;
		
		//Retrieve the list of MusicIDs from the table TB_MEDIA_FROM_PLAYLIST from a certain playlist.
		try {
			db = dbHelper.getWritableDatabase();

			Cursor cursor = db.rawQuery(DBConstants.SELECT_MUSIC_FROM_A_PLAYLIST,
					new String[] { String.valueOf(idPlaylist) });

			
				// here we need a for to save each musicid into the medialist

			if (cursor.moveToFirst()) {
				do {
					
					mediaList.add(cursor.getInt(cursor
							.getColumnIndex(DBConstants.MEDIA_FROM_PLAYLIST_FK_MEDIA)));
				} while (cursor.moveToNext());
			}
			cursor.close();
				

		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultPlaylistDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		} finally {
			if (db.inTransaction()) {
				db.endTransaction();
			}
			db.close();
			dbHelper.close();
		}
		
		
		
		//Then Retrive the Audio file from each of those musicIDS
		
		List<Audio> musics = new ArrayList<Audio>();

			
			
			
		for (Integer musicID : mediaList) {  
			    
			try {	
				db = dbHelper.getReadableDatabase();
				Cursor cursor = db.rawQuery(DBConstants.SELECT_AUDIO_BY_ID,new String[] { musicID.toString()});

				
				if (cursor.moveToFirst()) {
					do {
						Audio audio = DefaultAuthorDAO.cursorToAudio(cursor);;
						musics.add(audio);
					} while (cursor.moveToNext());
				}
				cursor.close();
				
			} catch (SQLiteException e) {
				Log.e(DefaultAuthorDAO.class.getCanonicalName(),
						e.getLocalizedMessage());
				throw new DBException();
			} finally {
				db.close();
				dbHelper.close();
			}
				
				
		}
			
			
			
			
			return musics;
			
		
		
	}



}

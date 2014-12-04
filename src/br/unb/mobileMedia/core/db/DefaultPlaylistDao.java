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

public class DefaultPlaylistDao implements IPlaylistDao {

	private Context context;
	private DBHelper dbHelper;
	private SQLiteDatabase db;

	
	public DefaultPlaylistDao(Context c){
		this.context = c;
		this.dbHelper = new DBHelper(context, DBConstants.DATABASE_NAME, null,
				DBConstants.DATABASE_VERSION);
	}
	
	
	private void endDb() {
		if (db.inTransaction()) {
			db.endTransaction();
		}

		if (db.isOpen() || db.isReadOnly())
			db.close();

		dbHelper.close();
	}

	
	public void newPlaylist(Playlist playlist) throws DBException {
		try {

			this.db = this.dbHelper.getWritableDatabase();

			Cursor cursor = db.rawQuery(DBConstants.SELECT_PLAYLIST_BY_NAME,
					new String[] { playlist.getName() });

			if (cursor.getCount() == 0) {

				// here we must save the author, since it does not exist.
				ContentValues values = new ContentValues();

				values.put(DBConstants.PLAYLIST_NAME_COLUMN, playlist.getName());

				this.db.beginTransaction();
				this.db.insert(DBConstants.PLAYLIST_TABLE, null, values);
				this.db.setTransactionSuccessful();
			}

		} catch (SQLiteException e) {

			Log.e("DefaultPlaylistDao", e.getLocalizedMessage());

			throw new DBException("DefaultPlaylistDao-newPlaylist: "
					+ e.getLocalizedMessage());

		} finally {

			this.endDb();

		}
		
	}

	public void addToPlaylist(int playlistId, List<Integer> mediaList)	throws DBException {
		try {
							// here we need a for to save each music into the table
				for (Integer musicID : mediaList) {  
				    
				
//				ContentValues values = new ContentValues();
//
//				values.put(DBConstants.MEDIA_FROM_PLAYLIST_FK_PLAYLIST, idPlaylist);
//				values.put(DBConstants.MEDIA_FROM_PLAYLIST_FK_MEDIA, musicID);
//
//				db.beginTransaction();
//				db.insert(DBConstants.MEDIA_FROM_PLAYLIST_TABLE, null, values);
//				db.setTransactionSuccessful();
//				
				
				}
				

		} catch (SQLiteException e) {
//			trataException(e);
		} finally{
//			endTx();
		}
	}

	public List<Playlist> listPlaylists() throws DBException {
		try{
			
			List<Playlist> playlists = new ArrayList<Playlist>();
			
			this.db = this.dbHelper.getReadableDatabase();
			
			Cursor cursor = this.db.rawQuery(DBConstants.SELECT_PLAYLISTS, null);
			
			if(cursor.moveToFirst()){
				do{
					playlists.add(cursorToPlaylist(cursor));
				}while(cursor.moveToNext());
			}
			
			cursor.close();
			
			return playlists;
			
		}catch(SQLiteException e){
			Log.e("DefaultPlaylistDao", e.getLocalizedMessage());

			throw new DBException("DefaultPlaylistDao-listPlaylists: "
					+ e.getLocalizedMessage());
		}finally{
			endDb();
		}
	}
	
	
	public Integer countPlaylists() throws DBException{
		try{
			
			this.db = this.dbHelper.getReadableDatabase();
			
			Cursor cursor = this.db.rawQuery(DBConstants.COUNT_PLAYLISTS, null);
			
			int count = 0;
			
			if(cursor.moveToFirst())
				count = cursor.getInt(0);

			cursor.close();
			
			return count;
			
		} catch (SQLiteException e) {
			Log.e("DefaultPlaylistDao", e.getLocalizedMessage());

			throw new DBException("DefaultPlaylistDao-countPlaylists: "
					+ e.getLocalizedMessage());
		} finally {
			endDb();
		}
	}
	
	


	public Playlist getPlaylistById(Integer playlistId) throws DBException {
		
		try{
		
			Playlist playlist = null;
			this.db = this.dbHelper.getReadableDatabase();
			
			Cursor cursor = db.rawQuery(DBConstants.SELECT_SIMPLE_PLAYLIST_BY_ID,	new String[] { playlistId.toString()});

			if(cursor.moveToFirst()){
				playlist = cursorToPlaylist(cursor);
			}
			
			cursor.close();
			
			return playlist;
			
		} catch (SQLiteException e) {
			Log.e("DefaultPlaylistDao", e.getLocalizedMessage());

			throw new DBException("DefaultPlaylistDao-getPlaylistById: "
					+ e.getLocalizedMessage());
		} finally {
			endDb();
		}
	}
	
	public Playlist getPlaylistByName(String name) throws DBException {
//		QueryBuilder<Playlist> qb = playlistDao.queryBuilder();
//		qb.where(Properties.Title.eq(name));
//		
//		return qb.list().get(0);
//	}
//	
//	public Playlist getPlaylist(int idPlaylist) throws DBException {
//		
//		Playlist p = this.playlistDao.load((long)idPlaylist);
//		
//		if(p == null)
//			throw new DBException();
//		
//		return p;
		
		return null;
	}



	
	public void deletePlaylist(Long id) throws DBException {
		
		
//		try{
//			Playlist playlist = getPlaylist(id.intValue());
//			
//			if(playlist != null)
//				playlistDao.delete(playlist);
//				
//		}catch(DBException e){
//			throw e;
//		}finally{
//			endTx();
//		}
//		
	}
	
	
	public void editPlaylist(Playlist editedPlaylist) throws DBException {
		
//		this.playlistDao.update(editedPlaylist);
			
	}
	
	public void addPositionPlaylist(Playlist playlist, double latitude,
			double longitude) throws DBException {
		// TODO Auto-generated method stub
		
	}


	public List<Audio> getMusicFromPlaylist(int idPlaylist) throws DBException {
		// TODO Auto-generated method stub
		List<Audio> audios = new ArrayList<Audio>();
		audios.add(new Audio(5));
		return audios;
	}


	
	
	
	
	private Playlist cursorToPlaylist(Cursor cursor){
		
		Integer id = cursor.getInt(cursor.getColumnIndex(DBConstants.PLAYLISTID_COLUMN));
		String name = cursor.getString(cursor.getColumnIndex(DBConstants.PLAYLIST_NAME_COLUMN));
		
		return new Playlist(id, name);
	}


	
}

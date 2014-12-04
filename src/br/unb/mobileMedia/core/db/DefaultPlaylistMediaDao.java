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
import br.unb.mobileMedia.core.domain.PlaylistMedia;

public class DefaultPlaylistMediaDao implements IPlaylistMediaDao {

	private Context context;
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	

	public DefaultPlaylistMediaDao(Context c) {
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
	
	
	public void addMediaToPlaylist(Integer audioId, Integer playlistId)
			throws DBException {
		
		try {
			
			this.db = this.dbHelper.getWritableDatabase();
			
			Cursor cursor = this.db.rawQuery(DBConstants.SELECT_MEDIA_FROM_PLAYLIST, new String[]{audioId.toString(), playlistId.toString()});

		    if(cursor.getCount() == 0){
		    	ContentValues values = new ContentValues();
				values.put(DBConstants.PLAYLIST_MEDIA_FK_MEDIA_COLUNM, audioId);
				values.put(DBConstants.PLAYLIST_MEDIA_FK_PLAYLIST_COLUNM, playlistId);
				
				this.db.beginTransaction();
				this.db.insert(DBConstants.PLAYLIST_MEDIA_TABLE, null, values);
				this.db.setTransactionSuccessful();
		    }
					
					
					
		} catch (SQLiteException e) {
			Log.e("DefaultPlaylistMediaDao", e.getLocalizedMessage());

			throw new DBException("DefaultPlaylistMediaDao-addAudioToPlaylist: "
					+ e.getLocalizedMessage());

		}finally {
			endDb();
		}

		
	}

	public PlaylistMedia getPlaylistByMediaInPlaylist(Audio audio,
			Playlist playlist) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeMediaFromPlaylist(Integer idMediaPlaylist)
			throws DBException {
		// TODO Auto-generated method stub
		
	}

	
	public List<PlaylistMedia> getMusicFromPlaylist(Integer playlistId) throws DBException{

		try {

			List<PlaylistMedia> playlistMedias = new ArrayList<PlaylistMedia>();
			this.db = this.dbHelper.getReadableDatabase();
			
			Cursor cursor = this.db.rawQuery(DBConstants.SELECT_MEDIAS_FROM_PLAYLIST, new String[]{playlistId.toString()});
			
			if(cursor.moveToFirst())
				do{
					playlistMedias.add(cursorToPlaylistMedia(cursor));
				}while(cursor.moveToNext());
			
			cursor.close();
			
			return playlistMedias;
			
		} catch (SQLiteException e) {
			Log.e("DefaultPlaylistMediaDao", e.getLocalizedMessage());

			throw new DBException("DefaultPlaylistMediaDao-addAudioToPlaylist: "
					+ e.getLocalizedMessage());

		}finally {
			endDb();
		}
	}
	

	
	
	
	private PlaylistMedia cursorToPlaylistMedia(Cursor cursor){
		
		Integer id = cursor.getInt(cursor.getColumnIndex(DBConstants.PLAYLIST_MEDIA_ID_COLUNM));
		Integer Fk_Media_Id = cursor.getInt(cursor.getColumnIndex(DBConstants.PLAYLIST_MEDIA_FK_MEDIA_COLUNM));
		Integer Fk_Playlist_Id = cursor.getInt(cursor.getColumnIndex(DBConstants.PLAYLIST_MEDIA_FK_PLAYLIST_COLUNM));
		
		return new PlaylistMedia(id, Fk_Media_Id, Fk_Playlist_Id);
	}



	


}

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

public class DefaultMediaDao implements IMediaDao {

	private SQLiteDatabase db;
	private DBHelper dbHelper;
	private Context context;

	public DefaultMediaDao(Context c) {
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

	public void saveAudio(Audio audio) throws DBException {
		try {
			
			this.db = this.dbHelper.getWritableDatabase();
			
			Cursor cursor  = this.db.rawQuery(DBConstants.SELECT_MEDIA_BY_PATH, new String[]{audio.getUrl()});
			if(cursor.getCount() == 0){
				
				ContentValues values = new ContentValues();
				values.put(DBConstants.MEDIA_URL_COLUMN, audio.getUrl());
				values.put(DBConstants.MEDIA_ALBUM_ID_COLUMN, audio.getAlbumId());

				this.db.beginTransaction();
				this.db.insert(DBConstants.MEDIA_TABLE, null, values);
				this.db.setTransactionSuccessful();
			}
			
		} catch (SQLiteException e) {
			Log.e("DefaultAudioDao", e.getLocalizedMessage());

			throw new DBException("DefaultAudioDao-saveAudio: "
					+ e.getLocalizedMessage());
		} finally {
			endDb();
		}

	}
	

	public Audio listAudioById(Integer audioId) throws DBException {

		try {
		
			Audio audio = null;
			
			this.db = this.dbHelper.getReadableDatabase();
			
			Cursor cursor = this.db.rawQuery(DBConstants.SELECT_MEDIA_BY_ID, new String[]{audioId.toString()});
			
			if(cursor.moveToFirst()){		
				audio = cursorToAudio(cursor);
			}
			
			cursor.close();
			
			return audio;

		} catch (SQLiteException e) {
			Log.e("DefaultAudioDao", e.getLocalizedMessage());

			throw new DBException("DefaultAudioDao-listAudioById: "
					+ e.getLocalizedMessage());

		} finally {
			endDb();
		}

	}

	public List<Audio> listAudioByTitle(Audio audio) throws DBException {

		try {
			return null;

		} catch (SQLiteException e) {
			Log.e("DefaultAudioDao", e.getLocalizedMessage());

			throw new DBException("DefaultAudioDao-listAudioByTitle: "
					+ e.getLocalizedMessage());

		} finally {
			endDb();
		}
	}

	public List<Audio> listAudioByPath(Audio audio) throws DBException {

		return null;
	}

	public List<Audio> listAudioByAlbum(Integer albumId) throws DBException {
		try {
			List<Audio> audios = new ArrayList<Audio>();

			this.db = this.dbHelper.getReadableDatabase();
			
			Cursor cursor = this.db.rawQuery(DBConstants.SELECT_MEDIA_BY_ALBUM, new String[]{albumId.toString()});
			
			if(cursor.moveToFirst()){
				do{
					audios.add(cursorToAudio(cursor));
				}while(cursor.moveToNext());
			}
			
			cursor.close();
			
			return audios;

		} catch (SQLiteException e) {
			Log.e("DefaultAudioDao", e.getLocalizedMessage());

			throw new DBException("DefaultAudioDao-listAllAudioByAlbum: "
					+ e.getLocalizedMessage());

		} finally {
			endDb();
		}

	}


	public List<Audio> listAllAudio() throws DBException {

		try {
			
			List<Audio> audios = new ArrayList<Audio>();

			this.db = this.dbHelper.getReadableDatabase();
			
			Cursor cursor = this.db.rawQuery(DBConstants.SELECT_MEDIAS, null);
			
			if(cursor.moveToFirst()){
				do{
					audios.add(cursorToAudio(cursor));
				}while(cursor.moveToNext());
			}
			
			cursor.close();
			
			return audios;

		} catch (SQLiteException e) {
			Log.e("DefaultAudioDao", e.getLocalizedMessage());

			throw new DBException("DefaultAudioDao-listAllAudio: "
					+ e.getLocalizedMessage());

		} finally {
			endDb();
		}

	}

	public Integer countAudios() throws DBException {
		try{
			this.db = this.dbHelper.getReadableDatabase();
			
			Cursor cursor = this.db.rawQuery(DBConstants.COUNT_MEDIA, null);
			
			int count = 0;
			
			if(cursor.moveToFirst())
				count = cursor.getInt(0);			
			
			cursor.close();
			
			return count;
			
			
			
		}catch (SQLiteException e) {

			Log.e("DefaultAudioDao", e.getLocalizedMessage());

			throw new DBException("DefaultAthorDao-countAudios: "
					+ e.getLocalizedMessage());

		} finally {

			endDb();

		}
	}

	public List<Audio> listAllAudioPaginated(Integer ofset, Integer limit)
			throws DBException {

		try{
			List<Audio> audios = new ArrayList<Audio>();

			this.db = this.dbHelper.getReadableDatabase();
			
			Cursor cursor = this.db.rawQuery(DBConstants.SELECT_MEDIA_PAGINATED, new String[]{ofset.toString(), limit.toString()});
			
			if(cursor.moveToFirst()){
				do{
					audios.add(cursorToAudio(cursor));
				}while(cursor.moveToNext());
			}
			
			cursor.close();
			return audios;
			
		}catch (SQLiteException e) {

			Log.e("DefaultAudioDao", e.getLocalizedMessage());

			throw new DBException("DefaultAthorDao-listAllAudioPaginated: "
					+ e.getLocalizedMessage());

		} finally {

			endDb();

		}

	}
	
	

	public boolean updateAudio(Audio audio) throws DBException {

		try {
			
			return false;
			
		} catch (SQLiteException e) {
			Log.e("DefaultAudioDao", e.getLocalizedMessage());

			throw new DBException("DefaultAudioDao-updateAudio: "
					+ e.getLocalizedMessage());


		} finally {
			endDb();
		}
	
	
	}

	public boolean deleteAudio(Audio audio) throws DBException {

		try {
		
			return false;
			
		} catch (SQLiteException e) {
			Log.e("DefaultAudioDao", e.getLocalizedMessage());

			throw new DBException("DefaultAudioDao-deleteAudio: "
					+ e.getLocalizedMessage());


		} finally {
			endDb();
		}
		
		
	}
	
	

	
	private Audio cursorToAudio(Cursor cursor){
		
		Integer id      = cursor.getInt(cursor.getColumnIndex(DBConstants.MEDIA_ID_COLUMN));
		String url      = cursor.getString(cursor.getColumnIndex(DBConstants.MEDIA_URL_COLUMN));
		Integer albumId = cursor.getInt(cursor.getColumnIndex(DBConstants.MEDIA_ALBUM_ID_COLUMN));
		
		return new Audio(id, url, albumId);
	}
	
}

package br.unb.mobileMedia.core.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.domain.Album;
import br.unb.mobileMedia.core.domain.Audio;

public class DefaultAlbumDao implements IAlbumDao {

	private Context context;
	private SQLiteDatabase db;
	private DBHelper dbHelper;

	public DefaultAlbumDao(Context c) {
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

	public void saveAlbum(Album album) throws DBException {
		try {

			this.db = this.dbHelper.getWritableDatabase();
			
			Cursor cursor = db.rawQuery(DBConstants.SELECT_ALBUMS_BY_NAME,
					new String[] { album.getName() });

			if (cursor.getCount() == 0) {

				ContentValues values = new ContentValues();
				values.put(DBConstants.ALBUM_NAME_COLUMN, album.getName());
				values.put(DBConstants.ALBUM_IMAGE_COLUNM, album.getImage());
				values.put(DBConstants.ALBUM_FK_AUTHOR_ID, album.getAuthorId());

				this.db.beginTransaction();
				this.db.insert(DBConstants.ALBUM_TABLE, null, values);
				this.db.setTransactionSuccessful();
			}

			cursor.close();
			
		} catch (SQLiteException e) {
			Log.e("DefaultAlbumDao-saveAlbum:", e.getLocalizedMessage());
			throw new DBException("DefaultAlbumDao-saveAlbum:" + e.getMessage());

		} finally {
			endDb();
		}

	}
	
	
	
	
	public List<Album> listAlbumsByAuthor(Integer authorId) throws DBException{
		
		try{
			List<Album> albums = new ArrayList<Album>();
			this.db = this.dbHelper.getReadableDatabase();
			
			Cursor cursor = this.db.rawQuery(DBConstants.SELECT_ALBUMS_BY_AUTHOR, new String[]{authorId.toString()});
			
			if (cursor.moveToFirst()) {
				do {
					albums.add(cursorToAlbum(cursor));
				} while (cursor.moveToNext());
			}

			cursor.close();
			
			return albums;
			
		}catch (SQLiteException e) {

			Log.e("DefaultAlbumDao", e.getLocalizedMessage());

			throw new DBException("DefaultAlbumDao-listAlbumsByAuthor: "
					+ e.getLocalizedMessage());

		} finally {
			endDb();
		}
	}
	
	
	
	
	

	public List<Album> listAlbums() throws DBException {
		try {

			List<Album> albums = new ArrayList<Album>();
			this.db = this.dbHelper.getReadableDatabase();

			Cursor cursor = db.rawQuery(DBConstants.SELECT_ALL_ALBUM, null);

			if (cursor.moveToFirst()) {
				do {
					albums.add(cursorToAlbum(cursor));
				} while (cursor.moveToNext());
			}

			cursor.close();

			Log.i("Total Albums:", ""+ albums.size());
			
			return albums;

		} catch (SQLiteException e) {

			Log.e("DefaultAlbumDao", e.getLocalizedMessage());

			throw new DBException("DefaultAlbumDao-listAlbums: "
					+ e.getLocalizedMessage());

		} finally {
			endDb();
		}

	}

	public Album findByName(String name) throws DBException {
		try {
			this.db = this.dbHelper.getReadableDatabase();

			Cursor cursor = this.db.rawQuery(DBConstants.SELECT_ALBUMS_BY_NAME,
					new String[] { name });

			if (cursor.getCount() == 0) {
				return null;
			} else if (cursor.getCount() == 1) {
				cursor.moveToFirst();
				cursor.close();
				return cursorToAlbum(cursor);
			} else {
				throw new DBException(
						"Inconsistency in album table, exists more than an album with the same name!");
			}
		} catch (SQLiteException e) {
			throw new DBException("DefaultAlbumDao-findByName: "
					+ e.getLocalizedMessage());
		} finally {
			endDb();
		}
		
		
	}

	public List<Audio> listAllProductionOfAlbum(Integer albumId)
			throws DBException {

		try{
			
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
			
			
		}catch(SQLiteException e){
				throw new DBException("DefaultAlbumDao-listAllProductionOfAlbum: "
						+ e.getLocalizedMessage());
		}finally{
			endDb();
		}
		
	}

	public Integer countAlbum() throws DBException {

		try{
			this.db = this.dbHelper.getReadableDatabase();
			
			Cursor cursor = this.db.rawQuery(DBConstants.COUNT_ALBUMS, null);
			
			int count = 0;
			
			if(cursor.moveToFirst())
				count = cursor.getInt(0);
			
			cursor.close();
			
			return count;
			
		}catch (SQLiteException e) {

			Log.e("DefaultAlbumDao", e.getLocalizedMessage());

			throw new DBException("DefaultAlbumDao-countAlbum: "
					+ e.getLocalizedMessage());

		} finally {

			endDb();

		}

	}

	/*
	 * Converts a cursor into an Album.
	 */
	private Album cursorToAlbum(Cursor cursor) {

		Integer id = cursor.getInt(cursor
				.getColumnIndex(DBConstants.ALBUM_ID_COLUMN));
		String name = cursor.getString(cursor
				.getColumnIndex(DBConstants.ALBUM_NAME_COLUMN));
		byte[] image = cursor.getBlob(cursor
				.getColumnIndex(DBConstants.ALBUM_IMAGE_COLUNM));
		Integer authorId = cursor.getInt(cursor
				.getColumnIndex(DBConstants.ALBUM_FK_AUTHOR_ID));

		Album album = new Album(id, name, image, authorId);
		return album;
	}
	
	
	
	/*
	 * Converts a cursor into an Audio.
	 */
	private Audio cursorToAudio(Cursor cursor) {

		Integer id = cursor.getInt(cursor
				.getColumnIndex(DBConstants.MEDIA_ID_COLUMN));
		String url = cursor.getString(cursor
				.getColumnIndex(DBConstants.MEDIA_URL_COLUMN));
		Integer albumId = cursor.getInt(cursor
				.getColumnIndex(DBConstants.ALBUM_ID_COLUMN));

		Audio audio = new Audio(id, url, albumId);
		return audio;
	}

}

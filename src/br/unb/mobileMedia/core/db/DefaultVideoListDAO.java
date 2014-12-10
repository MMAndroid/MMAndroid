package br.unb.mobileMedia.core.db;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.domain.Video;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.domain.Video;

/**
 * Default implementation of PlaylistDAO.
 * 
 * @author michael
 */


public class DefaultVideoListDAO {
	
	private Context context;
	private SQLiteDatabase db;
	private DBHelper dbHelper;
	
	public List<Video> videoList;

	/**
	 * Constructor of DefaultPlaylistDAO.
	 * 
	 * @param c the application context.
	 */
	
	public DefaultVideoListDAO(Context c) {
		context = c;
		dbHelper = new DBHelper(context, DBConstants.DATABASE_NAME, null,
				DBConstants.DATABASE_VERSION);
	}
	

	
	private void fechaConexao(){
		if (db.inTransaction()) {
			db.endTransaction();
		}
		db.close();
		dbHelper.close();
	}
	
	private void trataException(SQLiteException e) throws DBException{
		e.printStackTrace();
		Log.e(DefaultVideoListDAO.class.getCanonicalName(),
				e.getLocalizedMessage());
		throw new DBException(null);
	}
	
	/**
	 * 
	 * @return the quantity of audio have in bank
	 * @throws DBException
	 */
	public int countListVideoBanco() throws DBException {
		
		int count = 0;
		try {
			db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(DBConstants.SELECT_SIMPLE_ALL_VIDEOS, null);

			if (cursor.moveToFirst()) {
				do {
					count++;
					
				} while (cursor.moveToNext());
			}
			cursor.close();
			return count;
		} catch (SQLiteException e) {
			trataException(e);
		} finally {
			fechaConexao();
		}
		return 0;
	}
	
	public List<Video> getVideosBanco() throws DBException, UnsupportedEncodingException, URISyntaxException {
		
		videoList = new ArrayList<Video>();
		Video video;
		
		String title;
		String url;
		String id;
		URI uri;
		int pos;
		int newId;
		
		try {
			db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(DBConstants.SELECT_SIMPLE_ALL_VIDEOS, null);

			if (cursor.moveToFirst()) {
				do {
					
					title = cursor.getString( cursor.getColumnIndex("TITLE") );
					url   = cursor.getString( cursor.getColumnIndex("URL") );
					id    = cursor.getString( cursor.getColumnIndex("PK") );
					newId = Integer.parseInt(id);
					
					
					pos = url.lastIndexOf('/') + 1;
					uri = new URI(url.substring(0, pos) + URLEncoder.encode(url.substring(pos), "utf-8"));
					
					video = new Video(newId, title, uri);
					videoList.add(video);
					
				} while (cursor.moveToNext());
			}
			cursor.close();
			
			return videoList;
			
		} catch (SQLiteException e) {
			trataException(e);
		} finally {
			fechaConexao();
		}
		return null;
	}


    /**
     * 
     * @param title
     * @param album
     * @return
     * @throws DBException
     */
	public int adicionaVideo(String title, String url) throws DBException {

		try {
			db = dbHelper.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			
			values.put(DBConstants.VIDEO_TITLE_COLUMN, title);
			values.put(DBConstants.VIDEO_URL_COLUMN, url);
			
			db.beginTransaction();
			db.insert(DBConstants.VIDEO_TABLE, null, values);
			db.setTransactionSuccessful();
				
		} catch (SQLiteException e) {
			
			e.printStackTrace();
			throw new DBException(null);
			
		} finally {
		
			if (db.inTransaction()) {
				db.endTransaction();
			}
			db.close();
			dbHelper.close();
			
		}
		
		return 1;
	}
	
	
	



}
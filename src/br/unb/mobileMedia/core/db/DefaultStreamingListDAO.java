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

/**
 * Default implementation of PlaylistDAO.
 * 
 * @author michael
 */


public class DefaultStreamingListDAO {
	
	private Context context;
	private SQLiteDatabase db;
	private DBHelper dbHelper;
	
	public List<Video> videoList;

	/**
	 * Constructor of DefaultPlaylistDAO.
	 * 
	 * @param c the application context.
	 */
	
	public DefaultStreamingListDAO(Context c) {
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
		Log.e(DefaultStreamingListDAO.class.getCanonicalName(),
				e.getLocalizedMessage());
		throw new DBException(null);
	}
	
	
	public String[] getStreamingsBanco() throws DBException, UnsupportedEncodingException, URISyntaxException {
		
		
		List<String> list = new ArrayList<String>();
		
		
		int i = 0;
		
		
		try {
			db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(DBConstants.SELECT_SIMPLE_ALL_STREAMING, null);

			if (cursor.moveToFirst()) {
				do {
					
					list.add(cursor.getString( cursor.getColumnIndex("NAME"))+"00000"+cursor.getString( cursor.getColumnIndex("ADRESS")));

				} while (cursor.moveToNext());
			}
			cursor.close();
 			
			String[] radios = new String[list.size()];			
			
			for (String c : list) { 

				radios[i] = c;
				
				i++;
				
			}  
			
			return radios;
			
		} catch (SQLiteException e) {
			trataException(e);
		} finally {
			fechaConexao();
		}
		return null;
	}
	
	
	public String getAdressStreamingBanco(String name) throws DBException, UnsupportedEncodingException, URISyntaxException {
				
				
		
		try {
			db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(DBConstants.SELECT_SIMPLE_ALL_STREAMING_BY_NAME, new String[] {name});

			if (cursor.moveToFirst()) {
				do {
					
					return cursor.getString( cursor.getColumnIndex("NAME"));
					
				} while (cursor.moveToNext());
			}
			cursor.close();
			
						
		} catch (SQLiteException e) {
			trataException(e);
		} finally {
			fechaConexao();
		}
		return null;
	}

	
	public long addRadio(String name, String adress) throws DBException { 

		try {
			db = dbHelper.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			
			values.put(DBConstants.STREAMING_NAME_COLUMN, name);
			values.put(DBConstants.STREAMING_ADRESS_COLUMN, adress);
			
			db.beginTransaction();
			long pk = db.insert(DBConstants.STREAMING_TABLE, null, values);
			db.setTransactionSuccessful();
			
			return pk;
				
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
		
	}
	

}
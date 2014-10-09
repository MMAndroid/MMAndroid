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

public class DefaultAudioListDAO {
	
	private Context context;
	private SQLiteDatabase db;
	private DBHelper dbHelper;

	/**
	 * Constructor of DefaultPlaylistDAO.
	 * 
	 * @param c the application context.
	 */
	
	public DefaultAudioListDAO(Context c) {
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
		Log.e(DefaultAudioListDAO.class.getCanonicalName(),
				e.getLocalizedMessage());
		throw new DBException();
	}
	
	
	public int countListAudioBanco() throws DBException {
		
		int count = 0;
		try {
			db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(DBConstants.SELECT_SIMPLE_ALL_AUDIOS, null);

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
	
	
	



}
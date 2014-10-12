package br.unb.mobileMedia.core.db;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.domain.AudioOld;

public class DefaultAudioDAO implements AudioDAOOld {

	private Context context;
	private SQLiteDatabase db;
	private DBHelper dbHelper;
	
	/**
	 * Constructor of DefaultPlaylistDAO.
	 * 
	 * @param c the application context.
	 */
	public DefaultAudioDAO(Context c) {
		context = c;
		dbHelper = new DBHelper(context, DBConstants.DATABASE_NAME, null,
				DBConstants.DATABASE_VERSION);
	}
	
	public void saveAudio(AudioOld audio) throws DBException {

		try {
			
			db = dbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery(DBConstants.SELECT_AUDIO_BY_PATH,
					new String[] { audio.getURI().toString() });
				
				if (cursor.getCount() == 0) {

					ContentValues values = new ContentValues();
					
					values.put(DBConstants.AUDIO_URL_COLUMN, audio.getURI().toString());
					values.put(DBConstants.AUDIO_TITLE_COLUMN, audio.getTitle());

					db.beginTransaction();
					db.insert(DBConstants.AUDIO_TABLE, null, values);
					db.setTransactionSuccessful();
					
				}
			
		}catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAudioDAO.class.getCanonicalName(),
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

	public List<AudioOld> listAudio() throws DBException {
		
		try{
		
			db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(DBConstants.SELECT_AUDIOS, null);
		
			List<AudioOld> audios = new ArrayList<AudioOld>();
			
			if(cursor.moveToFirst()){
				do{
					AudioOld audio = cursorToAudio(cursor);
					audios.add(audio);
				}while(cursor.moveToNext());
				
				cursor.close();
				
				return audios;
			}
			
		}catch(SQLiteException e){
			Log.e(DefaultAudioDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		}finally{
			db.close();
			dbHelper.close();
		}
			
		return null;
	}
	
	
	public static AudioOld cursorToAudio(Cursor cursor) {
		Integer pk = cursor.getInt(cursor
				.getColumnIndex(DBConstants.AUDIO_KEY_COLUMN));
		Integer id = cursor.getInt(cursor
				.getColumnIndex(DBConstants.AUDIO_ID_COLUMN));
		String title = cursor.getString(cursor
				.getColumnIndex(DBConstants.AUDIO_TITLE_COLUMN));
		String album = cursor.getString(cursor
				.getColumnIndex(DBConstants.AUDIO_ALBUM_COLUMN));
		String url = cursor.getString(cursor
				.getColumnIndex(DBConstants.AUDIO_URL_COLUMN));

		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			Log.e(DefaultAuthorDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
			uri = null;
		}

		AudioOld audio = new AudioOld(pk, id, title, uri, album);

		return audio;
	}


}

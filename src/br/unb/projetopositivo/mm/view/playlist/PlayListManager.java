package br.unb.projetopositivo.mm.view.playlist;

import java.util.ArrayList;
import java.util.List;

import br.unb.projetopositivo.mm.util.MMConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PlayListManager {

	private Context context;
	private SQLiteDatabase db;
	private PlayListDBhelper dBhelper;
	
	private static final String QUERY = "SELECT NAME, DESCRIPTION FROM PLAYLIST;";
	
	public void open() {
		try {
			db = dBhelper.getWritableDatabase();
		}
		catch(SQLiteException e) {
			Log.w("Open database exception failed", e.getMessage());
			db = dBhelper.getReadableDatabase();
		}
	}
	
	public PlayListManager(Context c){
		context = c;
		dBhelper = new PlayListDBhelper(c, MMConstants.DATABASE_NAME, null, MMConstants.DATABASE_VERSION);
	}

	public void addPlayList(PlayList playList) {
		ContentValues values = new ContentValues();
		
		values.put("NAME", playList.getName());
		values.put("DESCRIPTION", playList.getDescription());
		
		db.beginTransaction();
		try {
			db.insert("PLAYLIST", null, values);
			db.setTransactionSuccessful();
		}
		catch(SQLiteException e) {
			Log.v("Fail to insert a playlist", e.getMessage());
		}
		finally {
			db.endTransaction();
	
		}
	}
	
	public List<PlayList> listAll() {
		Cursor cursor = db.rawQuery(QUERY, null);
		cursor.moveToFirst();
		List<PlayList> playLists = new ArrayList<PlayList>();
		
		while(cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex("NAME"));
			String description = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
		
			playLists.add(new PlayList(name, description));
		}
		return playLists;
	}
}

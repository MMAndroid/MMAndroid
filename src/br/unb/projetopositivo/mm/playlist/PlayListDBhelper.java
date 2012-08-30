package br.unb.projetopositivo.mm.view.playlist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PlayListDBhelper extends SQLiteOpenHelper {

	public static final String CREATE_TABLE = "CREATE TABLE PLAYLIST (" +
												"ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
												"NAME VARCHAR(50) NOT NULL," +
												"DESCRIPTION TEXT);";
	private static final String DROP_SCRIPT = "DROP TABLE IF EXISTS PLAYLIST;DROP TABLE IF EXISTS PLAYLIST;";
			
	public PlayListDBhelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			
			db.execSQL(CREATE_TABLE);
		}
		catch (SQLiteException e) {Log.v("create table exception!!!", e.getLocalizedMessage());}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(PlayListDBhelper.class.getName(),
				"Upgrading schema");
		db.execSQL(DROP_SCRIPT);
		onCreate(db);
	}

}

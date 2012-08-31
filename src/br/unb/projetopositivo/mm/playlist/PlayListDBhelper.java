package br.unb.projetopositivo.mm.playlist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A database helper class to access a Play List database.
 * 
 * @author positivo unb team - playlist group
 */

public class PlayListDBhelper extends SQLiteOpenHelper {

	public static final String CREATE_TABLE = "CREATE TABLE PLAYLIST (" +
												"ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
												"NAME VARCHAR(50) NOT NULL," +
												"DESCRIPTION TEXT);";
	private static final String DROP_SCRIPT = 
			"DROP TABLE IF EXISTS PLAYLIST;DROP TABLE IF EXISTS PLAYLIST;";
	
	/**
	 * Creates a helper object to open, create, and/or manage a database.
	 * This method always returns very quickly.
	 * The database is not actually created or opened until one of getWritableDatabase() or 
	 * getReadableDatabase() is called.
	 * 
	 * 	@param context	to use to open or create the database
	 * 	@param name	of the database file, or null for an in-memory database
	 * 	@param factory	to use for creating cursor objects, or null for the default
	 * 	@param version	number of the database (starting at 1); 
	 * 	If the database is older,onUpgrade(SQLiteDatabase, int, int) 
	 * 		will be used to upgrade the database; 
	 *  If the database is newer, onDowngrade(SQLiteDatabase, int, int) 
	 *  	will be used to downgrade the database;
	 */		
	public PlayListDBhelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		
	}


	/**
	 * Called when the database is created for the first time. 
	 * This is where the creation of tables and the initial population 
	 * 	of the tables should happen.
	 */
	public void onCreate(SQLiteDatabase db) {
		try{
			
			db.execSQL(CREATE_TABLE);
		}
		catch (SQLiteException e) {Log.v("create table exception!!!", e.getLocalizedMessage());}
	}

	/**
	 * Called when the database needs to be upgraded. 
	 * Deletes all content and table definitions from the database.
	 * Then it creates a new database.
	 * 
	 *  @param db The database
	 *  @param oldVersion The old database version
	 *  @param newVersion The new database version
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(PlayListDBhelper.class.getName(),
				"Upgrading schema");
		//Delete tables
		db.execSQL(DROP_SCRIPT);
		//Create new tables
		onCreate(db);
	}

}

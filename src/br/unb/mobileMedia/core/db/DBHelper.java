package br.unb.mobileMedia.core.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			for(String sql: DBConstants.CREATE_TABLE_STATEMENTS) {
				Log.i(DBHelper.class.getCanonicalName(), sql);
				db.execSQL(sql);
			}
		}
		catch(SQLiteException e) {
			Log.v(DBHelper.class.getName(), e.getMessage());
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			for(String sql : DBConstants.DROP_TABLE_STATEMENTS) {
				db.execSQL(sql);
			}
			onCreate(db);
		}
		catch(SQLiteException e) {
			Log.v(DBHelper.class.getName(), e.getMessage());
		}
	}

}

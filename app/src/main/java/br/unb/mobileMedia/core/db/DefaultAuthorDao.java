package br.unb.mobileMedia.core.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.domain.Album;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;

public class DefaultAuthorDao implements IAuthorDao {

	private Context context;
	private SQLiteDatabase db;
	private DBHelper dbHelper;

	public DefaultAuthorDao(Context c) {
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

	public void saveAuthor(Author author) throws DBException {

		try {

			this.db = this.dbHelper.getWritableDatabase();

			Cursor cursor = db.rawQuery(DBConstants.SELECT_AUTHORS_BY_NAME,
					new String[] { author.getName() });

			if (cursor.getCount() == 0) {

				// here we must save the author, since it does not exist.
				ContentValues values = new ContentValues();

				values.put(DBConstants.AUTHOR_NAME_COLUMN, author.getName());

				this.db.beginTransaction();
				this.db.insert(DBConstants.AUTHOR_TABLE, null, values);
				this.db.setTransactionSuccessful();
			}

		} catch (SQLiteException e) {

			Log.e("DefaultAthorDao", e.getLocalizedMessage());

			throw new DBException("DefaultAthorDao-SaveAuthor: "
					+ e.getLocalizedMessage());

		} finally {

			this.endDb();

		}

	}

	public List<Author> listAuthors() throws DBException {

		try {

			this.db = this.dbHelper.getReadableDatabase();

			Cursor cursor = db.rawQuery(DBConstants.SELECT_AUTHORS, null);
			List<Author> authors = new ArrayList<Author>();

			if (cursor.moveToFirst()) {
				do {
					authors.add(cursorToAuthor(cursor));
				} while (cursor.moveToNext());
			}

			cursor.close();
			return authors;

		} catch (SQLiteException e) {

			Log.e("DefaultAthorDao", e.getLocalizedMessage());

			throw new DBException("DefaultAthorDao-ListAuthor: "
					+ e.getLocalizedMessage());

		} finally {

			endDb();

		}

	}

	
	public Integer countAuthors() throws DBException{
		try{
			this.db = this.dbHelper.getReadableDatabase();
			
			Cursor cursor = this.db.rawQuery(DBConstants.COUNT_AUTHORS, null);
			
			int count = 0;
			if(cursor.moveToFirst())
				count = cursor.getInt(0);
			
			cursor.close();
			
			return count;
			
			
		}catch (SQLiteException e) {

			Log.e("DefaultAthorDao", e.getLocalizedMessage());

			throw new DBException("DefaultAthorDao-countAuthors: "
					+ e.getLocalizedMessage());

		} finally {

			endDb();

		}
		
	}
	
	
	public Author findByName(String name) throws DBException {
		
		try{
			
			this.db = this.dbHelper.getReadableDatabase();

			Cursor cursor = db.rawQuery(DBConstants.SELECT_AUTHORS_BY_NAME,
					new String[] { name });

			if(cursor.getCount() == 0){
				return null;
			}else if(cursor.getCount() == 1){
				cursor.moveToFirst();
				cursor.close();
			}else{
				throw new DBException("Inconsistency in author table, exists more than an author with the same name!");
			}
			
			return cursorToAuthor(cursor);

		}catch(SQLiteException e){
			throw new DBException("DefaultAthorDao-findByName: "
					+ e.getLocalizedMessage());
		}finally{
			endDb();
		}
	}

	
	  
    public List<Album> getAlbumByAuthor(Long authorId){
    	
    	List<Album> albums = new ArrayList<Album>();
    	
    	
    	Cursor cursorAuthor = this.db.rawQuery(DBConstants.SELECT_AUTHORS_BY_ID,
				new String[] { authorId.toString() });

		if (cursorAuthor.getCount() == 1 && cursorAuthor.moveToFirst()) {
			Author author = this.cursorToAuthor(cursorAuthor);
			
			Cursor cursorAlbum = this.db.rawQuery(DBConstants.SELECT_ALBUMS_BY_AUTHOR, new String[]{author.getId().toString()});
			
			if(cursorAlbum.getCount() > 0 && cursorAlbum.moveToFirst()){
				do{
					albums.add(cursorToAlbum(cursorAlbum));
				}while(cursorAlbum.moveToNext());
			}
			
			cursorAlbum.close();
			
		}
		
		cursorAuthor.close();
		
		return albums;
    }
	
	
	public void saveAuthorProduction(List<Author> author,
			List<Audio> listOfMedia) throws DBException {

		try {

			// Author dbAuthor = findByName(author.getName());

			// if (dbAuthor == null) {
			// saveAuthor(author);
			// dbAuthor = findByName(author.getName());
			// }

			// for (MultimediaContent media : listOfMedia) {
			//
			//
			//
			//
			// }

		} catch (SQLiteException e) {
			Log.e("DefaultAthorDao",e.getLocalizedMessage());
			throw new DBException("DefaultAthorDao-saveAuthorProduction: "+ e.getLocalizedMessage());
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			endDb();
		}

	}

	public List<Audio> findAudioProductionByAuthorKey(Integer key)
			throws DBException {

		return null;
	}

	public List<Audio> listAllProduction() throws DBException {

//		try {
//
//			initRead();
//			this.audioDao = this.daoSession.getAudioDao();
//
//			List<Audio> audios = audioDao.loadAll();
//
//			return audios;
//
//		} catch (SQLiteException e) {
//			Log.i("Load ALl Author", "-Exception-");
//			e.printStackTrace();
//			Log.e(DefaultAuthorDao.class.getCanonicalName(),
//					e.getLocalizedMessage());
//
//			throw new DBException();
//
//		} finally {
//
//			endDb();
//
//		}
		
		return null;

	}

	public Map<Author, Map<Audio, List<Date>>> executionHistory(Date start,
			Date end) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveExecutionHistory(Audio audio, Date time) throws DBException {
		// TODO Auto-generated method stub

	}


	
	
	
	
	/*
	 * Converts a cursor into an Author.
	 */
	private Author cursorToAuthor(Cursor cursor) {
		
		Integer id = cursor.getInt(cursor.getColumnIndex(DBConstants.AUTHOR_ID_COLUMN));
		String name = cursor.getString(cursor.getColumnIndex(DBConstants.AUTHOR_NAME_COLUMN));
		Author author = new Author(id, name);
		return author;
	}
	
	
	/*
	 * Converts a cursor into an Album.
	 */
	private Album cursorToAlbum(Cursor cursor){
		
		Integer id = cursor.getInt(cursor.getColumnIndex(DBConstants.ALBUM_ID_COLUMN));
		String name = cursor.getString(cursor.getColumnIndex(DBConstants.ALBUM_NAME_COLUMN));
		byte [] image = cursor.getBlob(cursor.getColumnIndex(DBConstants.ALBUM_IMAGE_COLUNM));
		Integer authorId = cursor.getInt(cursor.getColumnIndex(DBConstants.ALBUM_FK_AUTHOR_ID));
		
		Album album = new Album(id, name, image, authorId);
		return album;
	}

}

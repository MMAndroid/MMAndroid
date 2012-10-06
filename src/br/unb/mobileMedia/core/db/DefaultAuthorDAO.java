package br.unb.mobileMedia.core.db;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.format.DateFormat;
import android.util.Log;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;
import br.unb.mobileMedia.core.manager.Manager;
import br.unb.mobileMedia.util.MMConstants;

/**
 * Default implementation of AuthorDAO.
 * 
 * @author rbonifacio
 */
public class DefaultAuthorDAO implements AuthorDAO {

	private Context context;
	private SQLiteDatabase db;
	private DBHelper dbHelper;

	/**
	 * Constructor of DefaultAuthorDAO.
	 * 
	 * @param c
	 *            the application context.
	 */
	public DefaultAuthorDAO(Context c) {
		context = c;
		dbHelper = new DBHelper(context, DBConstants.DATABASE_NAME, null,
				DBConstants.DATABASE_VERSION);
	}

	/**
	 * @see AuthorDAO#saveAuthor(Author author)
	 */
	public void saveAuthor(Author author) throws DBException {
		try {
			db = dbHelper.getWritableDatabase();

			Cursor cursor = db.rawQuery(DBConstants.SELECT_AUTHORS_BY_NAME,
					new String[] { author.getName() });

			if (cursor.getCount() == 0) {
				// here we must save the author, since it does not exist.
				ContentValues values = new ContentValues();

				values.put(DBConstants.AUTHOR_ID_COLUMN, author.getId());
				values.put(DBConstants.AUTHOR_NAME_COLUMN, author.getName());

				db.beginTransaction();
				db.insert(DBConstants.AUTHOR_TABLE, null, values);
				db.setTransactionSuccessful();
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAuthorDAO.class.getCanonicalName(),
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

	/**
	 * @see AuthorDAO#listAuthors()
	 */
	public List<Author> listAuthors() throws DBException {
		try {
			db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(DBConstants.SELECT_AUTHORS, null);

			List<Author> authors = new ArrayList<Author>();

			if (cursor.moveToFirst()) {
				do {
					Author author = cursorToAuthor(cursor);
					authors.add(author);
				} while (cursor.moveToNext());
			}
			cursor.close();
			return authors;
		} catch (SQLiteException e) {
			Log.e(DefaultAuthorDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		} finally {
			db.close();
			dbHelper.close();
		}
	}

	/**
	 * @see AuthorDAO#findByName(String)
	 */
	public Author findByName(String name) {
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(DBConstants.SELECT_AUTHORS_BY_NAME,
				new String[] { name });

		if (cursor.getCount() == 0) {
			return null;
		}

		cursor.moveToFirst();
		db.close();
		dbHelper.close();
		return cursorToAuthor(cursor);
	}

	/**
	 * @see AuthorDAO#saveAuthorProduction(Author, List<Audio>)
	 */
	public void saveAuthorProduction(Author author, List<Audio> listOfAudio)
			throws DBException {
		try {
			Author dbAuthor = findByName(author.getName());

			// if the author does not exist, we must first save
			// his data on the database.

			if (dbAuthor == null) {
				saveAuthor(author);
				dbAuthor = findByName(author.getName());
			}

			db = dbHelper.getWritableDatabase();

			db.beginTransaction();
			for (Audio audio : listOfAudio) {
				Cursor cursor = db.rawQuery(
						DBConstants.SELECT_AUDIO_BY_AUTHOR_KEY_TITLE,
						new String[] { dbAuthor.getKey().toString(),
								audio.getTitle() });

				// here we check whether the audio exists or not. if it already
				// exists,
				// we do not import it again. we are assuming that no author
				// will produce
				// different musics with the same name.
				if (cursor.getCount() == 0) {
					ContentValues values = new ContentValues();

					values.put(DBConstants.AUDIO_ID_COLUMN, audio.getId());
					values.put(DBConstants.AUDIO_TITLE_COLUMN, audio.getTitle());
					values.put(DBConstants.AUDIO_ALBUM_COLUMN, audio.getAlbum());
					values.put(DBConstants.AUDIO_URL_COLUMN, audio.getURI()
							.toASCIIString());
					values.put(DBConstants.AUDIO_FK_AUTHOR_COLUMN,
							author.getId());

					db.insert(DBConstants.AUDIO_TABLE, null, values);

				}
				cursor.close();
			}
			db.setTransactionSuccessful();
		} catch (SQLiteException e) {
			Log.e(DefaultAuthorDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (db.inTransaction()) {
				db.endTransaction();
			}
			db.close();
			dbHelper.close();
		}
	}

	/**
	 * @see AuthorDAO#findAudioProductionByAuthorKey(Integer)
	 */
	public List<Audio> findAudioProductionByAuthorKey(Integer key)
			throws DBException {
		try {
			db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(
					DBConstants.SELECT_AUDIO_PRODUCTION_BY_AUTHOR_KEY,
					new String[] { key.toString() });

			List<Audio> production = iterateOverAudioCursor(cursor);
			cursor.close();
			return production;
		} catch (SQLiteException e) {
			Log.e(DefaultAuthorDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		} finally {
			db.close();
			dbHelper.close();
		}
	}

	/**
	 * @see DefaultAuthorDAO#listAllProduction()
	 */
	public List<Audio> listAllProduction() throws DBException {
		try {
			db = dbHelper.getReadableDatabase();

			Cursor cursor = db
					.rawQuery(DBConstants.LIST_AUDIO_PRODUCTION, null);
			List<Audio> production = iterateOverAudioCursor(cursor);
			cursor.close();
			return production;
		} catch (SQLiteException e) {
			Log.e(DefaultAuthorDAO.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		} finally {
			db.close();
			dbHelper.close();
		}
	}

	/**
	 * @see DefaultAuthorDAO#executionHistory(Date, Date)
	 */
	public Map<Author, Map<Audio, List<Date>>> executionHistory(Date start, Date end) throws DBException {
		db = dbHelper.getReadableDatabase();

		String startStr = DateFormat.format(MMConstants.DATA_FORMAT, start).toString();
		String endStr = DateFormat.format(MMConstants.DATA_FORMAT, end).toString();

		Cursor cursor = db.rawQuery(DBConstants.SELECT_EXECUTION_HISTORY, new String[] { startStr, endStr });

		Map<Author, Map<Audio, List<Date>>> groupByAuthor = new HashMap<Author, Map<Audio, List<Date>>>();

		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			do {
				Author author = cursorToAuthor(cursor);
				Audio audio = cursorToAudio(cursor);
				Date dateExecution = createDateExecution(cursor.getString(cursor.getColumnIndex(DBConstants.EH_DATE_TIME_EXECUTION_COLUMN)));

				Map<Audio, List<Date>> groupByAudio = createGoupByAudio(groupByAuthor, author);
				
				List<Date> executionDates = createExecutionDates(groupByAudio, audio);
				
				executionDates.add(dateExecution);

				groupByAudio.put(audio, executionDates);
				groupByAuthor.put(author, groupByAudio);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return groupByAuthor;
	}

	private Date createDateExecution(String dateAsString) {
		SimpleDateFormat formater = new SimpleDateFormat(MMConstants.DATA_FORMAT);
		try {
			return formater.parse(dateAsString);
		}
		catch(ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private List<Date> createExecutionDates(Map<Audio, List<Date>> groupByAudio, Audio audio) {
		if(groupByAudio.containsKey(audio)) {
			return groupByAudio.get(audio);
		}
		else {
			return new ArrayList<Date>();
		}
	}

	private Map<Audio, List<Date>> createGoupByAudio(Map<Author, Map<Audio, List<Date>>> groupByAuthor, Author author) {
		if (groupByAuthor.containsKey(author)) {
			return groupByAuthor.get(author);
		} else {
			return new HashMap<Audio, List<Date>>();
		}
	}

	/**
	 * @see AuthorDAO#saveExecutionHistory(Audio, Date)
	 */
	public void saveExecutionHistory(Audio audio, Date time) throws DBException {
		try {
			Log.v(Manager.class.getCanonicalName(), "PK: "  + audio.getPrimaryKey());

			db = dbHelper.getWritableDatabase();

			db.beginTransaction();

			ContentValues values = new ContentValues();

			values.put(DBConstants.EH_FK_AUDIO_COLUMN, audio.getPrimaryKey());
			values.put(DBConstants.EH_DATE_TIME_EXECUTION_COLUMN, DateFormat.format(MMConstants.DATA_FORMAT, time).toString());

			db.insert(DBConstants.EH_TABLE, null, values);
			db.setTransactionSuccessful();
		} catch (SQLiteException e) {
			Log.e(DefaultAuthorDAO.class.getCanonicalName(),
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

	private List<Audio> iterateOverAudioCursor(Cursor cursor) {
		List<Audio> production = new ArrayList<Audio>();

		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			do {
				Audio audio = cursorToAudio(cursor);
				production.add(audio);
			} while (cursor.moveToNext());
		}
		return production;
	}

	/*
	 * Converts a cursor into an Audio.
	 * Its public and static because DefaultPlaylist uses this method! Needs Refactoring!!!!!!
	 */
	public static Audio cursorToAudio(Cursor cursor) {
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

		Audio audio = new Audio(pk, id, title, uri, album);

		return audio;
	}

	/*
	 * Converts a cursor into an Author.
	 */
	private Author cursorToAuthor(Cursor cursor) {
		Integer key = cursor.getInt(cursor
				.getColumnIndex(DBConstants.AUTHOR_KEY_COLUMN));
		Integer id = cursor.getInt(cursor
				.getColumnIndex(DBConstants.AUTHOR_ID_COLUMN));
		String name = cursor.getString(cursor
				.getColumnIndex(DBConstants.AUTHOR_NAME_COLUMN));
		Author author = new Author(id, name);
		author.setKey(key);
		return author;
	}

}

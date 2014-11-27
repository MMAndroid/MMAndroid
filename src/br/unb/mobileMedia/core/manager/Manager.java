package br.unb.mobileMedia.core.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.db.AlbumDao;
import br.unb.mobileMedia.core.db.AudioDao;
import br.unb.mobileMedia.core.db.AuthorDao;
import br.unb.mobileMedia.core.db.DBConstants;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.db.DBFactory;
import br.unb.mobileMedia.core.db.DaoMaster;
import br.unb.mobileMedia.core.db.DaoMaster.DevOpenHelper;
import br.unb.mobileMedia.core.db.DaoSession;
import br.unb.mobileMedia.core.db.IAudioDao;
import br.unb.mobileMedia.core.db.IAuthorDao;
import br.unb.mobileMedia.core.db.IPlayListDao;
import br.unb.mobileMedia.core.db.IPlaylistMediaDao;
import br.unb.mobileMedia.core.db.PlaylistDao;
import br.unb.mobileMedia.core.domain.Album;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.domain.PlaylistMedia;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.core.extractor.MediaExtractor;
import br.unb.mobileMedia.util.ListAllFiles;

/**
 * A facade class for the MMUnB application.
 * 
 * @author rbonifacio
 */
public class Manager extends Observable {

	/* singleton instance of the manager class */
	private static Manager instance;

	private DevOpenHelper openHelper;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private AuthorDao authorDao;
	private AlbumDao albumDao;
	private AudioDao audioDao;
	private PlaylistDao playlistDao;

	/*
	 * Private constructor according to the single design pattern
	 */
	private Manager() {
		setChanged();
		notifyObservers();
	};

	private void openDbWrite(Context context) {
		try {
			openHelper = new DaoMaster.DevOpenHelper(context,
					DBConstants.DATABASE_NAME, null);
			db = openHelper.getWritableDatabase();
			daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
		} catch (Exception e) {
			Log.e("openDbWrite", e.getMessage());
		}
	}

	private void openDbRead(Context context) {
		try {
			openHelper = new DaoMaster.DevOpenHelper(context,
					DBConstants.DATABASE_NAME, null);
			db = openHelper.getReadableDatabase();
			daoMaster = new DaoMaster(db);
			daoSession = daoMaster.newSession();
		} catch (Exception e) {
			Log.e("openDbRead", e.getMessage());
		}
	}

	private void endTx() {
		if (db.inTransaction()) {
			db.endTransaction();
		}

		if (daoSession != null)
			daoSession.clear();

		if (db.isOpen())
			db.close();
	}

	/**
	 * Single method to obtain an instance of the Manager class.
	 * 
	 * @return the singleton instance of the Manager class.
	 */
	public static final Manager instance() {
		if (instance == null) {
			instance = new Manager();
		}
		return instance;
	}

	/**
	 * Updates the MMUnB database with the existing MP3s files in the SDCARD.
	 * 
	 * TODO: a lot must be generalized here. it only works for MP3 and M4A file.
	 * 
	 * @param context
	 *            the application context.
	 */
	public void synchronizeMedia(Context context) throws DBException {
		sync_audio(context);

		Preferences preferences = new Preferences(context);

		preferences.SetSyncPreference(true, this.countAllAudio(context)
				.intValue(), this.listAlbums(context).size(),
				this.listAuthors(context).size(), this.listPlaylists(context)
						.size());

		setChanged();
		notifyObservers();
	}

	// private void sync_video(Context context) throws DBException {
	// final List<File> allVideos = new ArrayList<File>();
	//
	// for (VideoFormats format : VideoFormats.values()) {
	// allVideos.addAll(FileUtility.listFiles(new File(Environment
	// .getExternalStorageDirectory().getPath() + "/Movies/"),
	// format.getFormatAsString()));
	// }
	//
	// }

	private void sync_audio(Context context) throws DBException {

		MediaExtractor extractor = new DefaultAudioExtractor(context);

		ThreadListFiles TListFiles = new ThreadListFiles();
		TListFiles.start();

		synchronized (TListFiles) {

			try {
				Log.i("TListFiles", "wait");
				TListFiles.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// extract all authors of audio in device
			List<String> authors = extractor
					.extractAllAuthors(TListFiles.files);

			// save authors in database
			saveAuthors(context, authors);

			// Get all authors put in database to get id of all author
			List<Author> list_authors = listAuthors(context);

			// extract all albums of audios in device
			List<Album> albums = extractor.extractAllAlbum(TListFiles.files,
					list_authors);

			// save all album with respective authorId in db
			saveAlbum(context, albums);

			// Get all albums put in database to get id of all album
			albums = this.listAlbums(context);

			// prepare all audio to persist in database
			List<Audio> audios = extractor.processAudio(TListFiles.files,
					albums);
			saveAudios(context, audios);

		}
	}

	private class ThreadListFiles extends Thread {
		List<File> files = new ArrayList<File>();

		@Override
		public void run() {
			synchronized (this) {
				ListAllFiles f = new ListAllFiles();
				files.addAll(f.getAllMusic());

				notify();
			}

		}

	}

	public List<Audio> listAllAudioByAlbum(Context context, Long albumId) {
		IAudioDao dao = DBFactory.factory(context).createAudioDAO();

		try {
			return dao.listAllAudioByAlbum(albumId);
		} catch (DBException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void saveAuthors(Context context, List<String> nameAuthors) {
		try {

			for (String nameAuthor : nameAuthors) {
				openDbWrite(context);
				authorDao = daoSession.getAuthorDao();

				// Select to check if exists an Author with the same namer
				Cursor cursor = db.rawQuery(DBConstants.SELECT_AUTHORS_BY_NAME,
						new String[] { nameAuthor });
				if (cursor.getCount() == 0) {
					db.beginTransaction();
					authorDao.insert(new Author(null, nameAuthor));
					db.setTransactionSuccessful();
				}

				cursor.close();

				endTx();
			}

		} catch (Exception e) {
			Log.e("saveAuthors", e.getMessage().toString());
			endTx();
		}

	}

	public void saveAlbum(Context context, List<Album> albums) {
		try {
			for (Album album : albums) {

				openDbWrite(context);
				albumDao = daoSession.getAlbumDao();

				Cursor cursor = db.rawQuery(DBConstants.SELECT_ALBUMS_BY_NAME,
						new String[] { album.getName() });
				db.beginTransaction();

				if (cursor.getCount() == 0)
					albumDao.insert(album);

				db.setTransactionSuccessful();
				endTx();
			}

		} catch (Exception e) {
			Log.e("saveAlbum", e.getLocalizedMessage());
			endTx();
		}

	}

	private void saveAudios(Context context, List<Audio> audios)
			throws DBException {
		try {

			for (Audio audio : audios) {
				openDbWrite(context);
				audioDao = daoSession.getAudioDao();

				db.beginTransaction();

				if (audioDao
						.queryBuilder()
						.where(br.unb.mobileMedia.core.db.AudioDao.Properties.Url
								.eq(audio.getUrl())).list().size() == 0) {

					audioDao.insert(audio);

				}
				db.setTransactionSuccessful();

				endTx();
			}

		} catch (Exception e) {
			Log.e("saveAudios", e.getLocalizedMessage());
			endTx();
		}

	}

	/**
	 * List the production of a specific author
	 * 
	 * @param authorPK
	 *            the primary key of the author
	 * @return the author production.
	 */
	public List<Audio> listProductionByAuthorPK(Context context,
			Integer authorId) throws DBException {

		List<Audio> audioByAuthor = new ArrayList<Audio>();

		try {
			openDbRead(context);

			albumDao = daoSession.getAlbumDao();
			audioDao = daoSession.getAudioDao();

			List<Album> albums = this.albumDao._queryAuthor_Albuns(authorId);

			for (Album album : albums) {
				audioByAuthor.addAll(this.audioDao._queryAlbum_AudioAlbum(album
						.getId()));
			}

			endTx();
		} catch (Exception e) {
			e.getStackTrace();
		}

		return audioByAuthor;
	}

	/**
	 * List all authors in the MMUnB database
	 * 
	 * @param context
	 *            the application context
	 * @return all authors in the database
	 * @throws DBException
	 */
	public List<Author> listAuthors(Context context) throws DBException {
		List<Author> authors = new ArrayList<Author>();
		try {
			openDbRead(context);
			authorDao = daoSession.getAuthorDao();
			authors = authorDao.loadAll();
			endTx();
		} catch (Exception e) {
			e.getStackTrace();
		}

		return authors;
	}

	public List<Album> listAlbums(Context context) throws DBException {

		openDbRead(context);
		albumDao = daoSession.getAlbumDao();
		List<Album> albums = albumDao.loadAll();
		endTx();

		return albums;

	}

	/**
	 * List all production within the database
	 * 
	 * @param context
	 *            the application context
	 * @return a list with all production synchronized at the database.
	 */
	public List<Audio> listAllProduction(Context context) throws DBException {
		IAuthorDao dao = DBFactory.factory(context).createAuthorDao();
		return dao.listAllProduction();
	}

	/**
	 * 
	 * @param context
	 * @param init
	 * @param limit
	 * @return
	 * @throws DBException
	 */
	public List<Audio> listAllAudioPaginated(Context context, int init,
			int limit) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IAudioDao audioDao = factory.createAudioDAO();

		return audioDao.listAllAudioPaginated(init, limit);
	}

	/**
	 * 
	 * @param context
	 * @return
	 * @throws DBException
	 */
	public List<Audio> listAllAudio(Context context) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IAudioDao audioDao = factory.createAudioDAO();
		return audioDao.listAllAudio();
		// DBFactory factory = DBFactory.factory(context);
		// final IPlayListDao playlistDao = factory.createPlaylistDao();
		// playlistDao.deletePlaylist(namePlaylist);
	}

	public Long countAllAuthor(Context context) {
		Long size = (long) 0;

		openDbRead(context);
		this.authorDao = this.daoSession.getAuthorDao();
		size = this.authorDao.count();
		endTx();
		return size;
	}

	public Long countAllAlbum(Context context) {

		Long size = (long) 0;

		openDbRead(context);
		this.albumDao = this.daoSession.getAlbumDao();
		size = this.albumDao.count();
		endTx();
		return size;
	}

	public Long countAllAudio(Context context) {

		Long size = null;
		openDbRead(context);
		this.audioDao = this.daoSession.getAudioDao();
		size = this.audioDao.count();
		endTx();

		return size;
	}

	public Long countAllPlaylist(Context context) {
		Long size = (long) 0;

		openDbRead(context);
		this.playlistDao = this.daoSession.getPlaylistDao();
		size = this.playlistDao.count();
		endTx();
		return size;
	}

	public List<Album> getAlbumByAuthor(Context context, Long authorId) {
		
		List<Album> albums = new ArrayList<Album>();

		openDbRead(context);
		this.authorDao = this.daoSession.getAuthorDao();
		albums = this.authorDao.getAlbumByAuthor(authorId);
		endTx();

		return albums;

	}
	
	
	public List<Audio> getAudioByAlbum(Context context, Long albumId){
		
		List<Audio> audios = new ArrayList<Audio>();
		
		openDbRead(context);
		this.albumDao = this.daoSession.getAlbumDao();
		audios = this.albumDao.getAudioByAlbum(albumId);
		endTx();
		
		return audios;
		
	}

	/**
	 * List the recently played musics, considering the period from <i>start</i>
	 * until today, gouped by Author and Music.
	 * 
	 * It is a map of maps, where the first map key is the author, and the
	 * second map key is an audio. The values entry of the second map is a list
	 * of dates, representing the number in which the audio was played in a
	 * given period.
	 * 
	 * @param context
	 *            the application context
	 * @param start
	 *            the start date of the period
	 * 
	 * @return a list with the most recently played musics.
	 */
	public Map<Author, Map<Audio, List<Date>>> recently(Context context,
			Date start) throws DBException {
		Date today = Calendar.getInstance().getTime();
		if (start.before(today)) {
			Map<Author, Map<Audio, List<Date>>> executionHistory = DBFactory
					.factory(context).createAuthorDao()
					.executionHistory(start, today);

			return executionHistory;
		}
		return null;
	}

	/**
	 * Registers the execution of a specific audio.
	 * 
	 * @param context
	 *            the application context.
	 * @param audio
	 *            the audio that has just been executed
	 */
	public void registerExecution(Context context, Audio audio)
			throws DBException {
		Log.v(Manager.class.getCanonicalName(), "PK: " + audio.getId());
		DBFactory.factory(context).createAuthorDao()
				.saveExecutionHistory(audio, Calendar.getInstance().getTime());
	}

	/**
	 * Returns the <i>size</i>most frequently played artists
	 * 
	 * @param context
	 *            the application context
	 * @param start
	 *            the start date considered
	 * @param end
	 *            the end date considered
	 * @param size
	 *            the size of the returned list is constrained to size
	 * @return the list of the top <i>size</i> artists
	 * @throws DBException
	 */
	public List<Author> topArtistsFromPeriod(Context context, Date start,
			Date end, int size) throws DBException {
		Map<Author, Map<Audio, List<Date>>> executionHistory = DBFactory
				.factory(context).createAuthorDao()
				.executionHistory(start, end);

		Map<Author, Integer> summary = summarize(executionHistory);

		Map<Author, Integer> sortedMap = new TreeMap<Author, Integer>(
				new SummaryOfExecutionHistoryComparator(summary));

		sortedMap.putAll(summary);

		Iterator<Author> it = sortedMap.keySet().iterator();

		List<Author> result = new ArrayList<Author>();
		for (int i = 0; i < sortedMap.keySet().size() && i < 5; i++) {
			result.add(it.next());
		}
		return result;
	}

	/**
	 * Create a new playlist
	 * 
	 * @param context
	 *            the application context
	 * @param playlist
	 *            to be created
	 * @throws DBException
	 */
	public void newPlaylist(Context context, Playlist playlist)
			throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IPlayListDao playlistDao = factory.createPlaylistDao();
		playlistDao.newPlaylist(playlist);
		// final PlaylistDAOOld playlistDAO = factory.createPlaylistDAO();
		// playlistDAO.newPlaylist(playlist);
	}

	/**
	 * Edit an existing playlist. The edited playlist will be found by the
	 * editedPlaylist id parameter. All fields from editedPlaylist (except id)
	 * will overwrite all fields from the found playlist currently persisted.
	 * 
	 * @param context
	 *            the application context
	 * @param playlist
	 *            to be edited
	 * @throws DBException
	 */
	public void editPlaylist(Context context, Playlist editedPlaylist)
			throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IPlayListDao playlistDao = factory.createPlaylistDao();
		playlistDao.editPlaylist(editedPlaylist);
	}

	/**
	 * Remove a playlist identified by id and Automatic remove all media from
	 * this playlist
	 * 
	 * @param context
	 *            the application context
	 * @param name
	 *            from the playlist to be removed
	 * @throws DBException
	 */
	public void removePlaylist(Context context, Long idPlaylist)
			throws DBException {

		DBFactory factory = DBFactory.factory(context);

		final IPlayListDao playlistDao = factory.createPlaylistDao();
		final IPlaylistMediaDao playlistMediaDao = factory
				.createPlaylistMediaDao();

		try {
			// Remove all medias from playlist
			removeAllMediaFromPlaylist(context, idPlaylist);

			// delete playlist
			playlistDao.deletePlaylist(idPlaylist);

		} catch (DBException e) {
			Log.e("Erro", e.getCause().toString());
		}

	}

	/**
	 * Add a geographical position to playlist
	 * 
	 * @param context
	 *            the application context
	 * @param playlist
	 * @param latitude
	 * @param longitude
	 * @throws DBException
	 */
	public void addPositionPlaylist(Context context, Playlist playlist,
			double latitude, double longitude) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IPlayListDao playlistDao = factory.createPlaylistDao();
		playlistDao.addPositionPlaylist(playlist, latitude, longitude);
		// final PlaylistDAOOld playlistDAO = factory.createPlaylistDAO();
		// playlistDAO.addPositionPlaylist(playlist,latitude,longitude);

	}

	/**
	 * Return a playlists identified by its id. The playlist returned do not
	 * have any media
	 * 
	 * @param context
	 *            the application context
	 * @param id
	 *            from the playlist to be found
	 * @return a playlist object
	 * @throws DBException
	 */
	public Playlist getPlaylistById(Context context, int id) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IPlayListDao playlistDao = factory.createPlaylistDao();
		return playlistDao.getPlaylistById(id);
	}

	/**
	 * Return a playlists identified by its name. The playlist returned do not
	 * have any media
	 * 
	 * @param context
	 *            the application context
	 * @param name
	 *            from the playlist to be found
	 * @return a playlist object
	 * @throws DBException
	 */
	public Playlist getPlaylistByName(Context context, String name)
			throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IPlayListDao playlistDao = factory.createPlaylistDao();
		return playlistDao.getPlaylistByName(name);
		// final PlaylistDAOOld playlistDAO = factory.createPlaylistDAO();
		// return playlistDAO.getSimplePlaylist(name);
	}

	/**
	 * Return a playlists identified by its id
	 * 
	 * @param context
	 *            the application context
	 * @param id
	 *            from the playlist to be found
	 * @return a playlist object
	 * @throws DBException
	 */
	public Playlist getPlaylist(Context context, int id) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IPlayListDao playlistDao = factory.createPlaylistDao();
		return playlistDao.getPlaylist(id);
		// final PlaylistDAOOld playlistDAO = factory.createPlaylistDAO();
		// return playlistDAO.getPlaylist(id);
	}

	/**
	 * Return a playlists identified by its name
	 * 
	 * @param context
	 *            the application context
	 * @param name
	 *            from the playlist to be found
	 * @return a playlist object
	 * @throws DBException
	 */
	public Playlist getPlaylist(Context context, String name)
			throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IPlayListDao playlistDao = factory.createPlaylistDao();
		return playlistDao.getPlaylistByName(name);
		// final PlaylistDAOOld playlistDAO = factory.createPlaylistDAO();
		// return playlistDAO.getPlaylist(name);
	}

	/**
	 * Return all playlists persisted. The playlist returned do not have any
	 * media
	 * 
	 * @param context
	 *            the application context
	 * @return the list of all playlists
	 * @throws DBException
	 */
	public List<Playlist> listSimplePlaylists(Context context)
			throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IPlayListDao playlistDao = factory.createPlaylistDao();
		return playlistDao.listSimplePlaylists();

		// final PlaylistDAOOld playlistDAO = factory.createPlaylistDAO();
		// return playlistDAO.listSimplePlaylists();
	}

	/**
	 * Return all playlists persisted
	 * 
	 * @param context
	 *            the application context
	 * @return the list of all playlists
	 * @throws DBException
	 */
	public List<Playlist> listPlaylists(Context context) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IPlayListDao playlistDao = factory.createPlaylistDao();
		return playlistDao.listPlaylists();
	}

	/**
	 * Add a media to a playlist
	 * 
	 * @param context
	 *            the application context
	 * @param id
	 *            from the playlist
	 * @param list
	 *            of ids of medias
	 * @throws DBException
	 */
	public void addMediaToPlaylist(Context context, int idPlaylist,
			List<Long> mediaList) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IPlaylistMediaDao playlistMediaDao = factory
				.createPlaylistMediaDao();

		Playlist platlist = new Playlist((long) idPlaylist);

		for (Long idAudio : mediaList) {
			playlistMediaDao.addAudioToPlaylist(idAudio, platlist);
		}
	}

	/**
	 * Remove all medias from a playlist
	 * 
	 * @param context
	 *            the application context
	 * @param id
	 *            from the playlist
	 * @param list
	 *            of ids of medias
	 * @throws DBException
	 */
	public void removeAllMediaFromPlaylist(Context context, Long idPlaylist)
			throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IPlaylistMediaDao playlistMediaDao = factory
				.createPlaylistMediaDao();

		List<PlaylistMedia> pl;

		try {
			pl = playlistMediaDao.getMusicFromPlaylist(idPlaylist);

			if (pl != null) {
				for (int i = 0; i < pl.size(); i++) {
					playlistMediaDao.removeMediaFromPlaylist(pl.get(i).getId());
				}

			}

		} catch (DBException e) {
			e.getStackTrace();
			Log.i("PLMedia", "null");
		}

	}

	/**
	 * Remove a list of medias from a playlist
	 * 
	 * @param context
	 *            the application context
	 * @param id
	 *            from the playlist
	 * @param list
	 *            of ids of medias
	 * @throws DBException
	 */
	public void removeMediaFromPlaylist(Context context, int idPlaylist,
			List<Audio> mediaList) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IPlaylistMediaDao playlistMediaDao = factory
				.createPlaylistMediaDao();
		PlaylistMedia pl;

		for (Audio audio : mediaList) {

			try {
				pl = playlistMediaDao.getPlaylistByMediaInPlaylist(audio,
						new Playlist((long) idPlaylist));

				if (pl != null)
					playlistMediaDao.removeMediaFromPlaylist(pl.getId());

			} catch (DBException e) {
				e.getStackTrace();
				Log.i("PLMedia", "null");
			}

		}

	}

	/**
	 * List all the musics from a a playlist
	 * 
	 * @param context
	 *            the application context
	 * @param id
	 *            from the playlist
	 * @throws DBException
	 */
	public List<Audio> getMusicFromPlaylist(Context context, int idPlaylist)
			throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IPlaylistMediaDao playlistMediaDao = factory
				.createPlaylistMediaDao();
		final IAudioDao audioDao = factory.createAudioDAO();

		List<PlaylistMedia> playlist = playlistMediaDao
				.getMusicFromPlaylist((long) idPlaylist);

		List<Audio> audiosInPlaylist = new ArrayList<Audio>();

		for (PlaylistMedia media : playlist) {
			audiosInPlaylist.add(audioDao.listAudioById(new Audio(media
					.getAudioPlaylistMediaId())));
		}

		return audiosInPlaylist;
	}

	public List<Audio> getMusicFromPlaylistPaginate(Context context,
			int idPlaylist, int init, int limit) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final IPlaylistMediaDao playlistMediaDao = factory
				.createPlaylistMediaDao();
		final IAudioDao audioDao = factory.createAudioDAO();

		List<PlaylistMedia> playlist = playlistMediaDao
				.getMusicFromPlaylistPaginate(new Playlist((long) idPlaylist),
						init, limit);
		//
		List<Audio> audiosInPlaylist = new ArrayList<Audio>();
		//
		for (PlaylistMedia media : playlist) {
			audiosInPlaylist.add(audioDao.listAudioById(new Audio(media
					.getAudioPlaylistMediaId())));
		}
		//
		Log.i("init " + init, "limit " + limit);

		// return null;
		return audiosInPlaylist;
	}

	private Map<Author, Integer> summarize(
			Map<Author, Map<Audio, List<Date>>> executionHistory) {
		Map<Author, Integer> result = new HashMap<Author, Integer>();
		for (Author author : executionHistory.keySet()) {
			int totalOfExecution = 0;
			for (Audio audio : executionHistory.get(author).keySet()) {
				totalOfExecution += executionHistory.get(author).get(audio)
						.size();
			}
			result.put(author, totalOfExecution);
		}
		return result;
	}

	/*
	 * A comparator for ordering the execution history of an author (a
	 * map<Author, Integer).
	 */
	class SummaryOfExecutionHistoryComparator implements Comparator<Author> {
		private Map<Author, Integer> map;

		public SummaryOfExecutionHistoryComparator(Map<Author, Integer> map) {
			this.map = map;
		}

		public int compare(Author key1, Author key2) {
			if (map.containsKey(key1) && map.containsKey(key2)) {
				return map.get(key1).compareTo(map.get(key2));
			}
			return 0;
		}
	}

}

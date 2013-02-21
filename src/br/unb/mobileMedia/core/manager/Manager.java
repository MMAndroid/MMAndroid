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
import java.util.TreeMap;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import br.unb.mobileMedia.core.db.AuthorDAO;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.db.DBFactory;
import br.unb.mobileMedia.core.db.PlaylistDAO;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.AudioFormats;
import br.unb.mobileMedia.core.domain.Author;
import br.unb.mobileMedia.core.domain.MultimediaContent;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.core.extractor.MediaExtractor;
import br.unb.mobileMedia.util.FileUtility;

/**
 * A facade class for the MMUnB application.
 * 
 * @author rbonifacio
 */
public class Manager {

	/* singleton instance of the manager class */
	private static Manager instance;

	/*
	 * Private constructor according to the single 
	 * design pattern
	 */
	private Manager() {}; 

	/**
	 * Single method to obtain an instance of the Manager class. 
	 * 
	 * @return the singleton instance of the Manager class. 
	 */
	public static final Manager instance() {
		if(instance == null) {
			instance = new Manager();
		}
		return instance;
	}
	/**
	 * Updates the MMUnB database with the existing MP3s files 
	 * in the SDCARD.
	 * 
	 * TODO: a lot must be generalized here. it only works for MP3 and M4A file.
	 * 
	 * @param context the application context.
	 */
	public void synchronizeMedia(Context context) throws DBException {
		final List<File> allMusics = new ArrayList<File>();

		for(AudioFormats format : AudioFormats.values()) {
			allMusics.addAll(FileUtility.listFiles(new File(Environment
					.getExternalStorageDirectory().getPath() + "/Music/"),
					format.getFormatAsString()));
		}

		MediaExtractor extractor = new DefaultAudioExtractor(context);

		List<Author> authors = (List<Author>) extractor.processFiles(allMusics);

		for(Author author: authors) {
			AuthorDAO dao = DBFactory.factory(context).createAuthorDAO();

			dao.saveAuthor(author);

			// TODO: we must generalize this code, since it only works for 
			// MP3 and M4A files.

			List<Audio> production = new ArrayList<Audio>();

			for(int i = 0; i < author.sizeOfProduction(); i++) {
				MultimediaContent c = author.getContentAt(i);

				if (c instanceof Audio) {
					production.add((Audio) c);
				}
			}
			dao.saveAuthorProduction(author, production);
		}
	}

	/**
	 * List the production of a specific author
	 * @param authorPK the primary key of the author
	 * @return the author production.
	 */
	public List<Audio> listProductionByAuthorPK(Context context, Integer authorPK) throws DBException {
		AuthorDAO dao = DBFactory.factory(context).createAuthorDAO();

		return dao.findAudioProductionByAuthorKey(authorPK);
	}

	/**
	 * List all authors in the MMUnB database 
	 * @param context the application context
	 * @return all authors in the database
	 * @throws DBException
	 */
	public List<Author> listAuthors(Context context) throws DBException {
		AuthorDAO dao = DBFactory.factory(context).createAuthorDAO();

		return dao.listAuthors();
	}

	/**
	 * List all production within the database
	 * @param context the application context
	 * @return a list with all production synchronized at the database.
	 */
	public List<Audio> listAllProduction(Context context) throws DBException {
		AuthorDAO dao = DBFactory.factory(context).createAuthorDAO();
		return dao.listAllProduction();
	}

	/**
	 * List the recently played musics, considering the period 
	 * from <i>start</i> until today, gouped by Author and Music. 
	 * 
	 * It is a map of maps, where the first map key is the 
	 * author, and the second map key is an audio. The values entry 
	 * of the second map is a list of dates, representing the number 
	 * in which the audio was played in a given period. 
	 * 
	 * @param context the application context
	 * @param start the start date of the period
	 * 
	 * @return a list with the most recently played musics. 
	 */
	public Map<Author, Map<Audio, List<Date>>> recently(Context context, Date start) throws DBException {
		Date today = Calendar.getInstance().getTime();
		if(start.before(today)) {
			Map<Author, Map<Audio, List<Date>>> executionHistory = DBFactory.factory(context).createAuthorDAO().executionHistory(start, today);


			return executionHistory;
		}
		return null;
	}

	/**
	 * Registers the execution of a specific audio. 
	 * 
	 * @param context the application context.
	 * @param audio the audio that has just been executed
	 */
	public void registerExecution(Context context, Audio audio) throws DBException {
		Log.v(Manager.class.getCanonicalName(), "PK: "  + audio.getPrimaryKey());
		DBFactory.factory(context).createAuthorDAO().saveExecutionHistory(audio, Calendar.getInstance().getTime());
	}

	/**
	 * Returns the <i>size</i>most frequently played artists
	 * 
	 * @param context the application context
	 * @param start the start date considered
	 * @param end the end date considered
	 * @param size the size of the returned list is constrained to size 
	 * @return the list of the top <i>size</i> artists
	 * @throws DBException
	 */
	public List<Author> topArtistsFromPeriod(Context context, Date start, Date end, int size) throws DBException {
		Map<Author, Map<Audio, List<Date>>> executionHistory = DBFactory.factory(context).createAuthorDAO().executionHistory(start, end);

		Map<Author, Integer> summary = summarize(executionHistory);

		Map<Author, Integer> sortedMap = new TreeMap<Author, Integer>(new SummaryOfExecutionHistoryComparator(summary));

		sortedMap.putAll(summary);

		Iterator<Author> it = sortedMap.keySet().iterator();

		List<Author> result = new ArrayList<Author>();
		for(int i = 0; i < sortedMap.keySet().size() && i < 5 ; i++) {
			result.add(it.next());
		}
		return result;
	}

	/**
	 * Create a new playlist
	 * 
	 * @param context the application context
	 * @param playlist to be created
	 * @throws DBException
	 */
	public void newPlaylist(Context context, Playlist playlist) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final PlaylistDAO playlistDAO = factory.createPlaylistDAO();
		playlistDAO.newPlaylist(playlist);
	}

	/**
	 * Edit an existing playlist. The edited playlist will 
	 * be found by the editedPlaylist id parameter. All fields
	 * from editedPlaylist (except id) will overwrite all 
	 * fields from the found playlist currently persisted.
	 * 
	 * @param context the application context
	 * @param playlist to be edited
	 * @throws DBException
	 */
	public void editPlaylist(Context context, Playlist editedPlaylist) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final PlaylistDAO playlistDAO = factory.createPlaylistDAO();
		playlistDAO.editPlaylist(editedPlaylist);
	}

	/**
	 * Remove a playlist identified by namePlaylist
	 * 
	 * @param context the application context
	 * @param name from the playlist to be removed
	 * @throws DBException
	 */
	public void removePlaylist(Context context, String namePlaylist) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final PlaylistDAO playlistDAO = factory.createPlaylistDAO();
		playlistDAO.deletePlaylist(namePlaylist);
	}
	
	/**
	 * Add a geographical position to playlist
	 * 
	 * @param context the application context
	 * @param playlist
	 * @param latitude
	 * @param longitude
	 * @throws DBException
	 */
	public void addPositionPlaylist(Context context, Playlist playlist, double latitude, double longitude) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final PlaylistDAO playlistDAO = factory.createPlaylistDAO();
		playlistDAO.addPositionPlaylist(playlist,latitude,longitude);
	}
	
	/**
	 * Return a playlists identified by its id. The playlist returned do not have any media
	 * 
	 * @param context the application context
	 * @param id from the playlist to be found
	 * @return a playlist object
	 * @throws DBException
	 */
	public Playlist getSimplePlaylist(Context context, int id) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final PlaylistDAO playlistDAO = factory.createPlaylistDAO();
		return playlistDAO.getSimplePlaylist(id);
	}
	
	/**
	 * Return a playlists identified by its name. The playlist returned do not have any media
	 * 
	 * @param context the application context
	 * @param name from the playlist to be found
	 * @return a playlist object
	 * @throws DBException
	 */
	public Playlist getSimplePlaylist(Context context, String name) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final PlaylistDAO playlistDAO = factory.createPlaylistDAO();
		return playlistDAO.getSimplePlaylist(name);
	}
	
	/**
	 * Return a playlists identified by its id 
	 * 
	 * @param context the application context
	 * @param id from the playlist to be found
	 * @return a playlist object
	 * @throws DBException
	 */
	public Playlist getPlaylist(Context context, int id) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final PlaylistDAO playlistDAO = factory.createPlaylistDAO();
		return playlistDAO.getPlaylist(id);
	}
	
	/**
	 * Return a playlists identified by its name 
	 * 
	 * @param context the application context
	 * @param name from the playlist to be found
	 * @return a playlist object
	 * @throws DBException
	 */
	public Playlist getPlaylist(Context context, String name) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final PlaylistDAO playlistDAO = factory.createPlaylistDAO();
		return playlistDAO.getPlaylist(name);
	}
	
	/**
	 * Return all playlists persisted. The playlist returned do not have any media
	 * 
	 * @param context the application context
	 * @return the list of all playlists
	 * @throws DBException
	 */
	public List<Playlist> listSimplePlaylists(Context context) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final PlaylistDAO playlistDAO = factory.createPlaylistDAO();
		return playlistDAO.listSimplePlaylists();
	}
	
	/**
	 * Return all playlists persisted
	 * 
	 * @param context the application context
	 * @return the list of all playlists
	 * @throws DBException
	 */
	public List<Playlist> listPlaylists(Context context) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final PlaylistDAO playlistDAO = factory.createPlaylistDAO();
		return playlistDAO.listPlaylists();
	}
	
	/**
	 * Add a media to a playlist
	 * 
	 * @param context the application context
	 * @param id from the playlist
	 * @param list of ids of medias 
	 * @throws DBException
	 */
	public void addMediaToPlaylist(Context context, int idPlaylist, List<Integer> mediaList) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final PlaylistDAO playlistDAO = factory.createPlaylistDAO();
		playlistDAO.addToPlaylist(idPlaylist, mediaList);
	}
	
	/**
	 * Remove a list of medias from a playlist
	 * 
	 * @param context the application context
	 * @param id from the playlist
	 * @param list of ids of medias 
	 * @throws DBException
	 */
	public void removeMediaFromPlaylist(Context context, int idPlaylist, List<Integer> mediaList) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final PlaylistDAO playlistDAO = factory.createPlaylistDAO();
		playlistDAO.removeMedias(idPlaylist, mediaList);
	}
	
	/**
	 *  List all the musics from a a playlist
	 * 
	 * @param context the application context
	 * @param id from the playlist
	 * @throws DBException
	 */
	public List<Audio> getMusicFromPlaylist(Context context, int idPlaylist) throws DBException {
		DBFactory factory = DBFactory.factory(context);
		final PlaylistDAO playlistDAO = factory.createPlaylistDAO();
		return playlistDAO.getMusicFromPlaylist(idPlaylist);
	}
	
	
	private Map<Author, Integer> summarize(Map<Author, Map<Audio, List<Date>>> executionHistory) {
		Map<Author, Integer> result = new HashMap<Author, Integer>();
		for(Author author : executionHistory.keySet()) {
			int totalOfExecution = 0;
			for(Audio audio : executionHistory.get(author).keySet()) {
				totalOfExecution += executionHistory.get(author).get(audio).size();
			}
			result.put(author, totalOfExecution);
		}
		return result;
	}

	/*
	 * A comparator for ordering the execution history of an author 
	 * (a map<Author, Integer).
	 */
	class SummaryOfExecutionHistoryComparator implements Comparator<Author> {
		private Map<Author, Integer> map;

		public SummaryOfExecutionHistoryComparator(Map<Author, Integer> map) {
			this.map = map;
		}

		public int compare(Author key1, Author key2) {
			if(map.containsKey(key1) && map.containsKey(key2)) {
				return map.get(key1).compareTo(map.get(key2));
			}
			return 0;
		}
	}
} 

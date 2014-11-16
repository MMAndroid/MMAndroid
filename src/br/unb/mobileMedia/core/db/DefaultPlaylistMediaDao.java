package br.unb.mobileMedia.core.db;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.db.DaoMaster.DevOpenHelper;
import br.unb.mobileMedia.core.db.PlaylistMediaDao.Properties;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.domain.PlaylistMedia;
import de.greenrobot.dao.query.QueryBuilder;

public class DefaultPlaylistMediaDao implements IPlaylistMediaDao {

	private Context context;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private DevOpenHelper openHelper;
	private PlaylistMediaDao playlistMediaDao;

	public DefaultPlaylistMediaDao(Context context) {
		this.context = context;
		this.openHelper = new DaoMaster.DevOpenHelper(this.context,	DBConstants.DATABASE_NAME, null);
		this.db = openHelper.getWritableDatabase();
		this.daoMaster = new DaoMaster(db);
		this.daoSession = daoMaster.newSession();
		this.playlistMediaDao = daoSession.getPlaylistMediaDao();

	}

	public void addAudioToPlaylist(Long idAudio, Playlist playlist) throws DBException {
		try {

			// Log.i("Audio Title", ""+ audio.getTitle());
			// Log.i("Audio Path", ""+audio.getUrl());
			// Log.i("listAudioByPath()", ""+listAudioByPath(audio).size());

			if (!listAudioInPlaylistMedia(idAudio, playlist)){
				//PlaylistMedia(Long id, long videoPlaylistMediaId, long audioPlaylistMediaId, long playlistId) 
				this.playlistMediaDao.insert(new PlaylistMedia(null, (Long)null, idAudio,  playlist.getId()));
				
			}
			// else
			// Log.i(audio.getTitle(), "In Database");

		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();

		}

	}

	
	public List<PlaylistMedia> getMusicFromPlaylist(Long idPlaylist) throws DBException{
		try{
			
			QueryBuilder<PlaylistMedia> qb = this.playlistMediaDao.queryBuilder();
			qb.where(Properties.PlaylistId.eq(idPlaylist));
			
			return qb.list();
			
		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		}
	}
	
	private boolean listAudioInPlaylistMedia(Long idAudio, Playlist playlist) {

		QueryBuilder<PlaylistMedia> qb = playlistMediaDao.queryBuilder();

		qb.where(Properties.AudioPlaylistMediaId.eq(idAudio),
				Properties.Id.eq(playlist.getId()));

		if (qb.list().size() > 0) {
			return true;
		}

		return false;

	}
	
	
	public PlaylistMedia getPlaylistByMediaInPlaylist(Audio audio, Playlist playlist) throws DBException{
		QueryBuilder<PlaylistMedia> qb = playlistMediaDao.queryBuilder();

		qb.where(Properties.AudioPlaylistMediaId.eq(audio.getId()),
				Properties.PlaylistId.eq(playlist.getId()) );

		if (qb.list().size() > 0) {
			return qb.list().get(0);
		}

		throw new DBException();

	}

	
	
	
	public List<PlaylistMedia> getMusicFromPlaylistPaginate(Playlist playlist, int init, int limit) throws DBException{
		
		QueryBuilder<PlaylistMedia> qb = playlistMediaDao.queryBuilder();

		qb.where(Properties.PlaylistId.eq(playlist.getId()) );
		qb.offset(init);
		qb.limit(limit);

		if (qb.list().size() > 0) {
			return qb.list();
		}

		

		throw new DBException();

	}
	
	public void removeMediaFromPlaylist(Long idMediaPlaylist) throws DBException{
		
		playlistMediaDao.deleteByKey(idMediaPlaylist);

	}

	public void addToPlaylist(int idPlaylist, List<Integer> mediaList) {
		
	}
}

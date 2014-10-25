package br.unb.mobileMedia.core.db;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.db.DaoMaster.DevOpenHelper;
import br.unb.mobileMedia.core.db.PlaylistMediaDao.Properties;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.domain.PlaylistMedia;

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

	public void addAudioToPlaylist(Audio audio, Playlist playlist) throws DBException {
		try {

			// Log.i("Audio Title", ""+ audio.getTitle());
			// Log.i("Audio Path", ""+audio.getUrl());
			// Log.i("listAudioByPath()", ""+listAudioByPath(audio).size());

			if (!listAudioInPlaylistMedia(audio, playlist)){
				//PlaylistMedia(Long id, long videoPlaylistMediaId, long audioPlaylistMediaId, long playlistId) 
				this.playlistMediaDao.insert(new PlaylistMedia(null, null, audio.getId(),  playlist.getId()));
				
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

	
	public List<PlaylistMedia> getMusicFromPlaylist(Playlist playlist) throws DBException{
		try{
			
			QueryBuilder<PlaylistMedia> qb = this.playlistMediaDao.queryBuilder();
			qb.where(Properties.PlaylistId.eq(playlist.getId()));
			
			return qb.list();
			
		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		}
	}
	
	private boolean listAudioInPlaylistMedia(Audio audio, Playlist playlist) {

		QueryBuilder<PlaylistMedia> qb = playlistMediaDao.queryBuilder();

		qb.where(Properties.AudioId.eq(audio.getId()),
				Properties.Id.eq(playlist.getId()));

		if (qb.list().size() > 0) {
			return true;
		}

		return false;

	}
	
	
	public PlaylistMedia getPlaylistByMediaInPlaylist(Audio audio, Playlist playlist) throws DBException{
		QueryBuilder<PlaylistMedia> qb = playlistMediaDao.queryBuilder();

		qb.where(Properties.AudioId.eq(audio.getId()),
				Properties.Id.eq(playlist.getId()) );

		if (qb.list().size() > 0) {
			return qb.list().get(0);
		}

		return null;

	}
	
	public void removeMediaFromPlaylist(PlaylistMedia playlistMedia) throws DBException{
		
		playlistMediaDao.delete(playlistMedia);
		
	}

	public void addToPlaylist(int idPlaylist, List<Integer> mediaList) {
		
	}
}

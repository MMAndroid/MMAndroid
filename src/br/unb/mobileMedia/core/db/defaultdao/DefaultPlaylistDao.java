package br.unb.mobileMedia.core.db.defaultdao;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.db.DBConstants;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.db.dao.AudioDao.Properties;
import br.unb.mobileMedia.core.db.dao.DaoMaster;
import br.unb.mobileMedia.core.db.dao.PlaylistDao;
import br.unb.mobileMedia.core.db.idao.IPlayListDao;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Playlist;
import de.greenrobot.dao.query.QueryBuilder;

public class DefaultPlaylistDao extends DefaultDao implements IPlayListDao {

	private PlaylistDao playlistDao;
	
	public DefaultPlaylistDao(Context c){
		this.context = c;
		this.openHelper = new DaoMaster.DevOpenHelper(this.context, DBConstants.DATABASE_NAME, null);
		this.db = this.openHelper.getWritableDatabase();
		this.daoMaster = new DaoMaster(this.db);
		this.daoSession = this.daoMaster.newSession();
		this.playlistDao = this.daoSession.getPlaylistDao();
	}
	
	public void newPlaylist(Playlist playlist) throws DBException {
		try {
			QueryBuilder<Playlist> qb = playlistDao.queryBuilder();
			qb.where(Properties.Title.eq(playlist.getTitle()));
			//check is playlist not exists
			
			if(qb.list().size() == 0)
				this.playlistDao.insert(playlist);
			else
				Log.i("Playlist: "+playlist.getTitle(), "In Database");
			
		
		}catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(), e.getLocalizedMessage());
			throw new DBException();

		} 
		
	}

	public void addToPlaylist(int playlistId, List<Integer> mediaList)	throws DBException {
		try {
							// here we need a for to save each music into the table
				for (Integer musicID : mediaList) {  
				    
				
//				ContentValues values = new ContentValues();
//
//				values.put(DBConstants.MEDIA_FROM_PLAYLIST_FK_PLAYLIST, idPlaylist);
//				values.put(DBConstants.MEDIA_FROM_PLAYLIST_FK_MEDIA, musicID);
//
//				db.beginTransaction();
//				db.insert(DBConstants.MEDIA_FROM_PLAYLIST_TABLE, null, values);
//				db.setTransactionSuccessful();
//				
				
				}
				

		} catch (SQLiteException e) {
//			trataException(e);
		} 
	}

	public List<Playlist> listPlaylists() throws DBException {
		return playlistDao.loadAll();
	}
	
	
	public List<Playlist> listSimplePlaylists() throws DBException {
		return playlistDao.loadAll();
	}

	public Playlist getPlaylistById(int idPlaylist) throws DBException {
	
		QueryBuilder<Playlist> qb = playlistDao.queryBuilder();
		qb.where(Properties.Id.eq(idPlaylist));
		
		return qb.list().get(0);
	}
	
	public Playlist getPlaylistByName(String name) throws DBException {
		QueryBuilder<Playlist> qb = playlistDao.queryBuilder();
		qb.where(Properties.Title.eq(name));
		
		return qb.list().get(0);
	}
	
	public Playlist getPlaylist(int idPlaylist) throws DBException {
		
		Playlist p = this.playlistDao.load((long)idPlaylist);
		
		if(p == null)
			throw new DBException();
		
		return p;
	}



	
	public void deletePlaylist(Long id) throws DBException {
		
		try{
			Playlist playlist = getPlaylist(id.intValue());
			
			if(playlist != null)
				playlistDao.delete(playlist);
				
		}catch(DBException e){
			throw e;
		}
		
	}
	
	
	public void editPlaylist(Playlist editedPlaylist) throws DBException {
		
		this.playlistDao.update(editedPlaylist);
			
	}
	
	public void addPositionPlaylist(Playlist playlist, double latitude,
			double longitude) throws DBException {
		// TODO Auto-generated method stub
		
	}


	public List<Audio> getMusicFromPlaylist(int idPlaylist) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

}

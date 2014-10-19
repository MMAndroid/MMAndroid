package br.unb.mobileMedia.core.db;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.db.AudioDao.Properties;
import br.unb.mobileMedia.core.db.DaoMaster.DevOpenHelper;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Playlist;

public class DefaultPlaylistDao implements IPlayListDao {

	private Context context;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private PlaylistDao playlistDao;
	private DevOpenHelper openHelper;
	
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
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public List<Playlist> listSimplePlaylists() throws DBException {
		return playlistDao.loadAll();
	}

	public Playlist getSimplePlaylist(int idPlaylist) throws DBException {
	
		QueryBuilder<Playlist> qb = playlistDao.queryBuilder();
		qb.where(Properties.Id.eq(idPlaylist));
		
		return qb.list().get(0);
	}
	
	public Playlist getSimplePlaylist(String name) throws DBException {
		QueryBuilder<Playlist> qb = playlistDao.queryBuilder();
		qb.where(Properties.Title.eq(name));
		
		return qb.list().get(0);
	}
	
	public Playlist getPlaylist(int idPlaylist) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	public Playlist getPlaylist(String name) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void deletePlaylist(String namePlaylist) throws DBException {
		
		try{
			Playlist playlist = getSimplePlaylist(namePlaylist);
			
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

	public void removeMedias(int idPlaylist, List<Integer> mediaList)
			throws DBException {
		// TODO Auto-generated method stub
		
	}

	public List<Audio> getMusicFromPlaylist(int idPlaylist) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

}

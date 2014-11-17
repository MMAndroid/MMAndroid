package br.unb.mobileMedia.core.db;

import br.unb.mobileMedia.core.db.idao.IAlbumDao;
import br.unb.mobileMedia.core.db.idao.IAudioDao;
import br.unb.mobileMedia.core.db.idao.IAuthorDao;
import br.unb.mobileMedia.core.db.idao.IPlayListDao;
import br.unb.mobileMedia.core.db.idao.IPlaylistMediaDao;
import android.content.Context;

/**
 * An abstract factory class to instantiate DAOs.
 * 
 * @author rbonifacio
 */

//DBFactory.factory(context).createAudioDAO();

public abstract class DBFactory{
	protected Context context;
	
	public static DBFactory factory(Context context) {
		return new DefaultDBFactory(context);
	}
	
	/**
	 * DBFactory constructor 
	 * @param context the application context.
	 */
	public DBFactory(Context context) {
		this.context = context;
	}
	

	
	/**
	 * Returns an instance of AudioDao.
	 * @return an instance of AudioDao
	 */
	public abstract IAudioDao createAudioDAO();
	
	
	/**
	 * Returns an instance of AuthorDao.
	 * @return an instance of AuthorDao
	 */
	public abstract IAuthorDao createAuthorDao();
	
	/**
	 * Returns an instance of PlaylistDao.
	 * @return an instance of PlaylistDao
	 */
	public abstract IPlayListDao createPlaylistDao();

	
	
	
	public abstract IPlaylistMediaDao createPlaylistMediaDao();

	
	
	public abstract IAlbumDao createAlbumDao();
	
	
	
	
	
}

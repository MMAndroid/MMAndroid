package br.unb.mobileMedia.core.db;

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
	 * Returns an instance of AuthorDAO.
	 * @return an instance of AuthorDAO.
	 */
	public abstract AuthorDAOOld createAuthorDAO();
	
	/**
	 * Returns an instance of PlaylistDAO.
	 * @return an instance of PlaylistDAO.
	 */
	public abstract PlaylistDAOOld createPlaylistDAO();

	
	
	
}

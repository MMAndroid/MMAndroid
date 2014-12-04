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
	 * @return an instance of AuthorDao
	 */
	public abstract IAuthorDao createAuthorDao();
	

	/**
	 * 
	 * @return an instance of AlbumDao
	 */
	public abstract IAlbumDao createAlbumDao();
	
	
	/**
	 * @return an instance of AudioDao
	 */
	public abstract IMediaDao createAudioDao();
	
	
	/**
	 * @return an instance of PlaylistDao
	 */
	public abstract IPlaylistDao createPlaylistDao();

	
	
	/**
	 * @return an instance of PlailistMediaDao
	 */
	public abstract IPlaylistMediaDao createPlaylistMediaDao();

	
	
	
}

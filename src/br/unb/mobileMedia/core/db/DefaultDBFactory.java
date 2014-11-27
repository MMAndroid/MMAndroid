package br.unb.mobileMedia.core.db;

import android.content.Context;

/**
 * A default implementation of the abstract class DBFactory.
 * @author rbonifacio
 */
public class DefaultDBFactory extends DBFactory {
	
	public DefaultDBFactory(Context context) {
		super(context);
	}
	
	
	@Override
	public IAudioDao createAudioDAO() {
		return new DefaultAudioDao(context);
	}
	
	@Override
	public IPlayListDao createPlaylistDao() {
		return new DefaultPlaylistDao(context);
	}
	
	
	@Override
	public IAuthorDao createAuthorDao() {
		return new DefaultAuthorDao(context);
	}
	
	
	@Override
	public IAlbumDao createAlbumDao(){
		return new DefaultAlbumDao(context);
	}



	@Override
	public IPlaylistMediaDao createPlaylistMediaDao(){
		return new DefaultPlaylistMediaDao(context);
	}

	
}

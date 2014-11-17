package br.unb.mobileMedia.core.db;

import br.unb.mobileMedia.core.db.defaultdao.DefaultAlbumDao;
import br.unb.mobileMedia.core.db.defaultdao.DefaultAudioDao;
import br.unb.mobileMedia.core.db.defaultdao.DefaultAuthorDao;
import br.unb.mobileMedia.core.db.defaultdao.DefaultPlaylistDao;
import br.unb.mobileMedia.core.db.defaultdao.DefaultPlaylistMediaDao;
import br.unb.mobileMedia.core.db.idao.IAlbumDao;
import br.unb.mobileMedia.core.db.idao.IAudioDao;
import br.unb.mobileMedia.core.db.idao.IAuthorDao;
import br.unb.mobileMedia.core.db.idao.IPlayListDao;
import br.unb.mobileMedia.core.db.idao.IPlaylistMediaDao;
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

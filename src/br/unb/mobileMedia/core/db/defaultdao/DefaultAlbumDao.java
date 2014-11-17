package br.unb.mobileMedia.core.db.defaultdao;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.db.DBConstants;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.db.dao.AlbumDao;
import br.unb.mobileMedia.core.db.dao.DaoMaster;
import br.unb.mobileMedia.core.db.dao.DaoSession;
import br.unb.mobileMedia.core.db.dao.AuthorDao.Properties;
import br.unb.mobileMedia.core.db.dao.DaoMaster.DevOpenHelper;
import br.unb.mobileMedia.core.db.idao.IAlbumDao;
import br.unb.mobileMedia.core.domain.Album;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;

public class DefaultAlbumDao implements IAlbumDao {

	private Context context;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private AlbumDao albumDao;
	private DevOpenHelper openHelper;
	
	
	public DefaultAlbumDao(Context c){
		this.context = c;
		
		this.openHelper = new DaoMaster.DevOpenHelper(this.context, DBConstants.DATABASE_NAME, null);
		this.db = openHelper.getWritableDatabase();
		this.daoMaster = new DaoMaster(db);
		this.daoSession = daoMaster.newSession();
		this.albumDao = daoSession.getAlbumDao();
	}

	
	
	public void saveAlbum(Album album) throws DBException {
		try {

			if(findByName(album.getName()) == null){
				this.albumDao.insert(album);
			}
			
		}catch (SQLiteException e) {
			Log.i("Exception","Save Album "+album.getName() + " of authorId: "+album.getAutorId());
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();

		}
		
	}

	public List<Album> listAlbums() throws DBException {
		return this.albumDao.loadAll();
	}

	
	public Album findByName(String name) throws DBException {
		QueryBuilder<Album> qb = this.albumDao.queryBuilder();
		qb.where(Properties.Name.eq(name));
		
		List<Album> list = qb.list();
		
		if(list.size()>0){
			return list.get(0);
		}
		
		return null;
	}

	public List<Audio> listAllProductionOfAlbum(long albumId)
			throws DBException {
		// TODO Auto-generated method stub
		return null;
	}



	public Long countAlbum(){
		return this.albumDao.count();
	}

}

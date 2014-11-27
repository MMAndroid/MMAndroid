package br.unb.mobileMedia.core.db;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.db.DaoMaster.DevOpenHelper;
import br.unb.mobileMedia.core.domain.Album;
import br.unb.mobileMedia.core.domain.Audio;

public class DefaultAlbumDao implements IAlbumDao {

	private Context context;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private AlbumDao albumDao;
	private DevOpenHelper openHelper;

	public DefaultAlbumDao(Context c) {
		this.context = c;
	}

	private void initWrite(){
		this.openHelper = new DaoMaster.DevOpenHelper(this.context, DBConstants.DATABASE_NAME, null);
		this.db = this.openHelper.getWritableDatabase();
		this.daoMaster = new DaoMaster(this.db);
		this.daoSession = this.daoMaster.newSession();
	}
	
	private void initRead(){
		this.openHelper = new DaoMaster.DevOpenHelper(this.context, DBConstants.DATABASE_NAME, null);
		this.db = this.openHelper.getReadableDatabase();
		this.daoMaster = new DaoMaster(this.db);
		this.daoSession = this.daoMaster.newSession();
	}
	
	
	private void endTx(){
		if (db.inTransaction()) {
			db.endTransaction();
		}
		
		if(this.daoSession != null)
			this.daoSession.clear();
		
		if(db.isOpen())
			db.close();
		
		this.openHelper.close();
	}
	
	public void saveAlbum(Album album) throws DBException {
		try {

			initWrite();
			 Cursor cursor = db.rawQuery(DBConstants.SELECT_ALBUMS_BY_NAME, new String[]{album.getName()});
			
			if (cursor.getCount() == 0) {
				this.albumDao = this.daoSession.getAlbumDao();
				this.db.beginTransaction();
				this.albumDao.insert(album);
				this.db.setTransactionSuccessful();
			}
			
			cursor.close();

		} catch (SQLiteException e) {
			Log.e(DefaultAudioDao.class.getCanonicalName(), 
					e.getLocalizedMessage() + "DefaultAlbumDao");
			throw new DBException();

		} finally {
			endTx();
		}

	}

	public List<Album> listAlbums() throws DBException {
		try {

			initRead();
			this.albumDao = this.daoSession.getAlbumDao();
			List<Album> albums = albumDao.loadAll();
			
			return albums;
			
		}catch (SQLiteException e) {
			Log.i("Load ALl Author","-Exception-");
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();

		}  finally {
			endTx();
		}
		
	}

	public Album findByName(String name) throws DBException {
//		QueryBuilder<Album> qb = this.albumDao.queryBuilder();
//		qb.where(Properties.Name.eq(name));
//
//		List<Album> list = qb.list();
//
//		if (list.size() > 0) {
//			return list.get(0);
//		}

		return null;
	}

	public List<Audio> listAllProductionOfAlbum(long albumId)
			throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	public Long countAlbum() throws DBException{

		try {

			initRead();
			this.albumDao = this.daoSession.getAlbumDao();
			
			return this.albumDao.count();
			
		}catch (SQLiteException e) {
			Log.i("Load ALl Author","-Exception-");
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
				throw new DBException();

		}  finally {
			endTx();
		}
		
	}

}

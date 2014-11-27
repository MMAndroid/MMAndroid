package br.unb.mobileMedia.core.db;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.db.AudioDao.Properties;
import br.unb.mobileMedia.core.db.DaoMaster.DevOpenHelper;
import br.unb.mobileMedia.core.domain.Audio;
import de.greenrobot.dao.query.QueryBuilder;

public class DefaultAudioDao implements IAudioDao {

	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private AudioDao audioDao;
	private DevOpenHelper openHelper;
	private Context context;

	public DefaultAudioDao(Context c) {
		this.context = c;
		
	}
	
	
	private void initWrite(){
		this.openHelper = new DaoMaster.DevOpenHelper(this.context,
				DBConstants.DATABASE_NAME, null);
		this.db = this.openHelper.getWritableDatabase();
		this.daoMaster = new DaoMaster(this.db);
		this.daoSession = this.daoMaster.newSession();
	}
	
	private void initRead(){
		this.openHelper = new DaoMaster.DevOpenHelper(this.context,
				DBConstants.DATABASE_NAME, null);
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

	public void saveAudio(Audio audio) throws DBException {
		try {
			
			initWrite();
			
			Cursor cursor = db.rawQuery(DBConstants.SELECT_AUDIO_BY_PATH, new String[]{audio.getUrl()});
			this.audioDao = this.daoSession.getAudioDao();
		
			if (cursor.getCount() == 0){
				this.db.beginTransaction();
				this.audioDao.insert(audio);
				this.db.setTransactionSuccessful();
			}

			
		} catch (SQLiteException e) {
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage() + "DefaultAudioDao");
			throw new DBException();

		} finally {
			endTx();
		}

	}
	

	public Audio listAudioById(Audio audio) throws DBException {

		try {
			initRead();
			this.audioDao = this.daoSession.getAudioDao();

			QueryBuilder<Audio> qb = audioDao.queryBuilder();
			qb.where(Properties.Id.eq(audio.getId()));

			if (qb.list().size() > 0) {
				return qb.list().get(0);
			}

			return null;

		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();

		} finally {
			endTx();
		}

	}

	public List<Audio> listAudioByTitle(Audio audio) throws DBException {

		try {
			initRead();
			this.audioDao = this.daoSession.getAudioDao();

			QueryBuilder<Audio> qb = audioDao.queryBuilder();
			qb.where(Properties.Title.eq(audio.getTitle()));

			return qb.list();

		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();

		} finally {
			endTx();
		}
	}

	public List<Audio> listAudioByPath(Audio audio) throws DBException {

		initRead();
		this.audioDao = this.daoSession.getAudioDao();

		QueryBuilder<Audio> qb = audioDao.queryBuilder();
		qb.where(Properties.Url.eq(audio.getUrl()));

		return qb.list();
	}

	public List<Audio> listAudioByAlbum(Long albumId) throws DBException {
		try {
			
			initRead();
			this.audioDao = this.daoSession.getAudioDao();
			Cursor cursor = this.db.rawQuery(DBConstants.SELECT_AUDIO_BY_ALBUM, new String[]{albumId.toString()});
			
//			List<Audio> audios = new ArrayList<Audio>();
			
			List<Audio> audios = this.audioDao.loadAll();
			
//			if(cursor.moveToFirst()){
//				do{
//					Audio audio = cursorToAudio(cursor);
//					
//					audios.add(audio);
//					
//				}while(cursor.moveToNext());
//				
//				cursor.close();
//			}
			
			return audios;

		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();

		} finally {
			endTx();
		}

	}

	public List<Audio> listAllAudioByAlbum(Long albumId) throws DBException {

		try {
			
			initRead();
			this.audioDao = this.daoSession.getAudioDao();

			return audioDao._queryAlbum_AudioAlbum(albumId);

		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();

		} finally {
			endTx();
		}

	}

	public List<Audio> listAllAudio() throws DBException {

		try {
			
			initRead();
			this.audioDao = this.daoSession.getAudioDao();

			return audioDao.loadAll();

		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();

		} finally {
			endTx();
		}

	}

	public Long countAllAudio() throws DBException {
		try {
			
			initRead();
			this.audioDao = this.daoSession.getAudioDao();

			return this.audioDao.count();

		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();

		} finally {
			endTx();
		}
	}

	public List<Audio> listAllAudioPaginated(int ofset, int limit)
			throws DBException {

		initRead();
		this.audioDao = this.daoSession.getAudioDao();

		QueryBuilder<Audio> qb = audioDao.queryBuilder();
		qb.limit(limit);
		qb.offset(ofset);
		qb.orderAsc(Properties.AlbumId);

		Log.e("Limit:", "" + limit + " - " + ofset);

		if (qb.list().size() > 0) {
			Log.e("AudioPaginated", "" + qb.list().size());
			return qb.list();
		}

		throw new DBException();

	}
	
	

	public boolean updateAudio(Audio audio) throws DBException {

		try {
			initWrite();
			this.audioDao = this.daoSession.getAudioDao();

			// make update
			audioDao.update(audio);

			// check if update is ok
			if (audio.equals(listAudioById(audio)))
				return true;
			
			return false;
			
		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();

		} finally {
			endTx();
		}
	
	
	}

	public boolean deleteAudio(Audio audio) throws DBException {

		try {
			
			initWrite();
			this.audioDao = this.daoSession.getAudioDao();
			this.db.beginTransaction();
			audioDao.delete(audio);
			this.db.setTransactionSuccessful();
			
			// check if delete is ok
			if (listAudioById(audio) != null)
				return true;
			
			return false;
			
		} catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();

		} finally {
			endTx();
		}
		
		
	}

	
	
	private Audio cursorToAudio(Cursor cursor){
//		Audio(Long id, String title, String url, long albumId) 
		Long id = cursor.getLong(cursor.getColumnIndex(DBConstants.AUDIO_ID_COLUMN));
		String title = cursor.getString(cursor.getColumnIndex(DBConstants.AUDIO_TABLE));
		String url   = cursor.getString(cursor.getColumnIndex(DBConstants.AUDIO_URL_COLUMN));
		Long albumId = cursor.getLong(cursor.getColumnIndex(DBConstants.AUDIO_ALBUM_ID_COLUMN));
		
		
		return new Audio(id, title, url, albumId);
	}
	
}

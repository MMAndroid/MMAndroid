package br.unb.mobileMedia.core.db;

import java.util.List;
import de.greenrobot.dao.query.QueryBuilder;
import br.unb.mobileMedia.core.db.AudioDao.Properties;
import br.unb.mobileMedia.core.db.DaoMaster.DevOpenHelper;
import br.unb.mobileMedia.core.domain.Audio;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DefaultAudioDao implements IAudioDao {

	private Context context;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private AudioDao audioDao;
//	private DevOpenHelper openHelper;
	
	public DefaultAudioDao(Context c){
		this.context = c;
		
//		this.openHelper = new DaoMaster.DevOpenHelper(this.context, DBConstants.DATABASE_NAME, null);
		DatabaseOpenHelper openHelper = new DatabaseOpenHelper(context, DBConstants.DATABASE_NAME, null);
		this.db = openHelper.getWritableDatabase();
		this.daoMaster = new DaoMaster(db);
		this.daoSession = daoMaster.newSession();
		this.audioDao = daoSession.getAudioDao();
	}

	
	public void createDb() throws DBException {
		throw new DBException();
	}

	
	public void saveAudio(Audio audio) throws DBException {
		try {
			
//			Log.i("Audio Title", ""+ audio.getTitle());
//			Log.i("Audio Path", ""+audio.getUrl());	
//			Log.i("listAudioByPath()", ""+listAudioByPath(audio).size());
			
			if(listAudioByPath(audio).size() == 0)
				this.audioDao.insert(audio);
//			else
//				Log.i(audio.getTitle(), "In Database");
			
		
		}catch (SQLiteException e) {
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();

		} 
			
	}
	
	
	public List<Audio> listAudioById(Audio audio){
		
		QueryBuilder<Audio>qb = audioDao.queryBuilder();
		qb.where(Properties.Id.eq(audio.getId()));
		
		return qb.list();
	}
	

	public List<Audio> listAudioByTitle(Audio audio){
		
		QueryBuilder<Audio> qb = audioDao.queryBuilder();
		qb.where(Properties.Title.eq(audio.getTitle()));
		
		return qb.list();
	}
	
	
	public List<Audio> listAudioByPath(Audio audio){
		
		QueryBuilder<Audio> qb = audioDao.queryBuilder();
		qb.where(Properties.Url.eq(audio.getUrl()));
		
		return qb.list();
	}
	
	public List<Audio> listAudioByAlbum(Audio audio){
		
		QueryBuilder<Audio> qb = audioDao.queryBuilder();
		qb.where(Properties.AlbumId.eq(audio.getAlbumId()));
		
		return qb.list();
	}
	
	
	public List<Audio> listAllAudio(){
		
		return audioDao.loadAll();
		
	}
	
	
	public boolean updateAudio(Audio audio) throws DBException{
		
		//make update
		audioDao.update(audio);
		
		//check if update is ok
		if(audio.equals(listAudioById(audio).get(0)))
			return true;
		
		throw new DBException();
		
	}
	
	
	public boolean deleteAudio(Audio audio) throws DBException{
		
		//make update
		audioDao.delete(audio);
		
		//check if delete is ok
		if(listAudioById(audio).isEmpty())
			return true;
		
		throw new DBException();
		
	}

	
	
}

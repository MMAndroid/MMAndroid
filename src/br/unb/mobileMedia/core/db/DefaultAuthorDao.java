package br.unb.mobileMedia.core.db;

import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.db.AuthorDao.Properties;
import br.unb.mobileMedia.core.db.DaoMaster.DevOpenHelper;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;
import de.greenrobot.dao.query.QueryBuilder;

public class DefaultAuthorDao implements IAuthorDao {

	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private AuthorDao authorDao;
	private AudioDao audioDao;
	private DevOpenHelper openHelper;
	private Context context;
	
	public DefaultAuthorDao(Context c){
		this.context    = c;
	}

	private void initWrite(){
		this.openHelper = new DaoMaster.DevOpenHelper(this.context, DBConstants.DATABASE_NAME, null);
		this.db = this.openHelper.getWritableDatabase();
		this.daoMaster = new DaoMaster(this.db);
		this.daoSession = this.daoMaster.newSession();
		this.authorDao = this.daoSession.getAuthorDao();

	}
	
	private void initRead(){
		this.openHelper = new DaoMaster.DevOpenHelper(this.context, DBConstants.DATABASE_NAME, null);
		this.db = this.openHelper.getReadableDatabase();
		this.daoMaster = new DaoMaster(this.db);
		this.daoSession = this.daoMaster.newSession();
		this.authorDao = this.daoSession.getAuthorDao();

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
	
	public void saveAuthor(String author) throws DBException {
		try {
			
			initWrite();
			
			Cursor cursor = db.rawQuery(DBConstants.SELECT_AUTHORS_BY_NAME, new String[]{author});
	
			if(cursor.getCount() == 0){
				this.db.beginTransaction();
				this.authorDao.insert(new Author(null, author));	
				this.db.setTransactionSuccessful();
			}

			
		}catch (SQLiteException e) {

			Log.e("DefaultAthorDao", e.getLocalizedMessage());
			
			throw new DBException();

		} finally {
		
			endTx();
		}
		
	}

	public List<Author> listAuthors() throws DBException {
		
		try {

			initRead();
			
			this.authorDao = this.daoSession.getAuthorDao();
			
			Cursor cursor = db.rawQuery(DBConstants.SELECT_AUTHORS, null);
			
			List<Author> authors = authorDao.loadAll();
			
			Log.i("Total Author", "" + cursor.getCount());
			
			
			return authors;
			
		}catch (SQLiteException e) {
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();

		}  finally {
			
			endTx();
			
		}
		
		
	}

	public Author findByName(String name) throws DBException {

		this.initRead();
		this.authorDao = this.daoSession.getAuthorDao();
		
		QueryBuilder<Author> qb = authorDao.queryBuilder();
		qb.where(Properties.Name.eq(name));
		List<Author> list = qb.list();
		
		if(list.size()>0){
			Log.i("Author", list.get(0).getName()+" Exists.");
			return list.get(0);
		}else{
			Log.i("Author", name +" Not Exists.");
		}
		
		return null;
			
	}

	
	public void saveAuthorProduction(List<Author> author, List<Audio> listOfMedia) throws DBException {
		
		try{
			
//			Author dbAuthor = findByName(author.getName());
			
//			if (dbAuthor == null) {
//				saveAuthor(author);
//				dbAuthor = findByName(author.getName());
//			}
			
//			for (MultimediaContent media : listOfMedia) {
//				
//				
//				
//				
//			}
			
		}catch (SQLiteException e) {
			Log.e(DefaultAuthorDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			endTx();
		}
		
	}

	
	
	public List<Audio> findAudioProductionByAuthorKey(Integer key)throws DBException {
		
		return null;
	}

	public List<Audio> listAllProduction() throws DBException {
		
		try {

			initRead();
			this.audioDao = this.daoSession.getAudioDao();

			List<Audio> audios = audioDao.loadAll();
			
			return audios;
			
		}catch (SQLiteException e) {
			Log.i("Load ALl Author","-Exception-");
			e.printStackTrace();
			Log.e(DefaultAuthorDao.class.getCanonicalName(), e.getLocalizedMessage());
			
			throw new DBException();

		}  finally {
			
			endTx();
			
		}
		
	}

	public Map<Author, Map<Audio, List<Date>>> executionHistory(Date start,
			Date end) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveExecutionHistory(Audio audio, Date time) throws DBException {
		// TODO Auto-generated method stub
		
	}

}

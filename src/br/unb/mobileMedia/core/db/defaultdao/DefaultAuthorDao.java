package br.unb.mobileMedia.core.db.defaultdao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import br.unb.mobileMedia.core.db.DBConstants;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.db.dao.AudioDao;
import br.unb.mobileMedia.core.db.dao.AuthorDao;
import br.unb.mobileMedia.core.db.dao.DaoMaster;
import br.unb.mobileMedia.core.db.dao.DaoSession;
import br.unb.mobileMedia.core.db.dao.AuthorDao.Properties;
import br.unb.mobileMedia.core.db.dao.DaoMaster.DevOpenHelper;
import br.unb.mobileMedia.core.db.idao.IAuthorDao;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;

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
		this.openHelper = new DaoMaster.DevOpenHelper(this.context, DBConstants.DATABASE_NAME, null);
		this.db         = openHelper.getWritableDatabase();
		this.daoMaster  = new DaoMaster(db);
		this.daoSession = daoMaster.newSession();
		this.authorDao  = daoSession.getAuthorDao();
		this.audioDao   = daoSession.getAudioDao();
	}
	
	public void saveAuthor(String author) throws DBException {

		try {

			if(findByName(author) == null){
				this.authorDao.insert(new Author(null, author));
			}
			
		}catch (SQLiteException e) {
			Log.i("Save Author","-Exception-");
			e.printStackTrace();
			Log.e(DefaultAudioDao.class.getCanonicalName(),
					e.getLocalizedMessage());
			throw new DBException();

		} 
		
	}


	public List<Author> listAuthors() throws DBException {
		return authorDao.loadAll();
	}

	public Author findByName(String name) throws DBException {

		QueryBuilder<Author> qb = authorDao.queryBuilder();
		qb.where(Properties.Name.eq(name));
		
		List<Author> list = qb.list();
		
		if(list.size()>0){
//			Log.i("Author encontrado no banco", "");
			return list.get(0);
		}
		
//		Log.i("Nao Author encontrado no banco", "");
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
		}
		
	}

	
	
	public List<Audio> findAudioProductionByAuthorKey(Integer key)throws DBException {
		
		return null;
	}

	public List<Audio> listAllProduction() throws DBException {
		return audioDao.loadAll();
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

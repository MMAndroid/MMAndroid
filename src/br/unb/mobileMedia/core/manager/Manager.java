package br.unb.mobileMedia.core.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import br.unb.mobileMedia.core.db.AuthorDAO;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.db.DBFactory;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.AudioFormats;
import br.unb.mobileMedia.core.domain.Author;
import br.unb.mobileMedia.core.domain.MultimediaContent;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.core.extractor.MediaExtractor;
import br.unb.mobileMedia.util.FileUtility;

/**
 * A facade class for the MMUnB application.
 * 
 * @author rbonifacio
 */
public class Manager {

	/* singleton instance of the manager class */
	private static Manager instance;
	
	/*
	 * Private constructor according to the single 
	 * design pattern
	 */
	private Manager() {}; 
	
	/**
	 * Single method to obtain an instance of the Manager class. 
	 * 
	 * @return the singleton instance of the Manager class. 
	 */
	public static final Manager instance() {
		if(instance == null) {
			instance = new Manager();
		}
		return instance;
	}
	/**
	 * Updates the MMUnB database with the existing MP3s files 
	 * in the SDCARD.
	 * 
	 * TODO: a lot must be generalized here. it only works for MP3 and M4A file.
	 * 
	 * @param context the application context.
	 */
	public void synchronizeMedia(Context context) throws DBException {
		final List<File> allMusics = new ArrayList<File>();
		
		for(AudioFormats format : AudioFormats.values()) {
			allMusics.addAll(FileUtility.listFiles(new File(Environment
					.getExternalStorageDirectory().getPath() + "/Music/"),
					format.getFormatAsString()));
		}
			
		MediaExtractor extractor = new DefaultAudioExtractor(context);
		
		List<Author> authors = extractor.processFiles(allMusics);
		
		for(Author author: authors) {
			AuthorDAO dao = DBFactory.factory(context).createAuthorDAO();

			dao.saveAuthor(author);
			
			// TODO: we must generalize this code, since it only works for 
			// MP3 and M4A files.
			
			List<Audio> production = new ArrayList<Audio>();
			
			for(int i = 0; i < author.sizeOfProduction(); i++) {
				MultimediaContent c = author.getContentAt(i);
				
				if (c instanceof Audio) {
					production.add((Audio) c);
				}
			}
			dao.saveAuthorProduction(author, production);
		}
	}
	
	/**
	 * List the production of a specific author
	 * @param authorPK the primary key of the author
	 * @return the author production.
	 */
	public List<Audio> listProductionByAuthorPK(Context context, Integer authorPK) throws DBException {
		AuthorDAO dao = DBFactory.factory(context).createAuthorDAO();
		
		return dao.findAudioProductionByAuthorKey(authorPK);
	}
	
	/**
	 * List all authors in the MMUnB database 
	 * @param context the application context
	 * @return all authors in the database
	 * @throws DBException
	 */
	public List<Author> listAuthors(Context context) throws DBException {
		AuthorDAO dao = DBFactory.factory(context).createAuthorDAO();

		return dao.listAuthors();
	}
	
	/**
	 * List all production within the database
	 * @param context the application context
	 * @return a list with all production synchronized at the database.
	 */
	public List<Audio> listAllProduction(Context context) throws DBException {
		AuthorDAO dao = DBFactory.factory(context).createAuthorDAO();
		return dao.listAllProduction();
	}
	
	/**
	 * List the recently played musics, considering the period 
	 * from <i>start</i> until today. 
	 * 
	 * @param context the application context
	 * @param start the start date of the period
	 * 
	 * @return a list with the most recently played musics. 
	 */
	public Map<Audio, List<Date>> recently(Context context, Date start) throws DBException {
		Date today = Calendar.getInstance().getTime();
		if(start.before(today)) {
			Map<Audio, List<Date>> executionHistory = DBFactory.factory(context).createAuthorDAO().executionHistory(start, today);

			
			return executionHistory;
		}
		return null;
	}
	
	/**
	 * Registers the execution of a specific audio. 
	 * 
	 * @param context the application context.
	 * @param audio the audio that has just been executed
	 */
	public void registerExecution(Context context, Audio audio) throws DBException {
		 DBFactory.factory(context).createAuthorDAO().saveExecutionHistory(audio, Calendar.getInstance().getTime());
	}
} 

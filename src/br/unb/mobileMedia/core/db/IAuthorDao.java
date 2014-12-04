package br.unb.mobileMedia.core.db;

import java.util.Date;
import java.util.List;
import java.util.Map;

import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;

public interface IAuthorDao {
	/**
	 * Save an author in the author's database.
	 * 
	 * @param author - the author that will be persisted. 
	 */
	public void saveAuthor(Author author) throws DBException;
	
	/**
	 * Persist an media and relates it to author in the database
	 * @param author the author
	 * @param audio the media that will be saved
	 */
	public void saveAuthorProduction(List<Author> author, List<Audio> listOfMedia) throws DBException;
	
	/**
	 * List all authors of the MMUnB database.
	 * @return list of registered authors.
	 * @throws DBException if anything goes wrong with the database query
	 */
	public List<Author> listAuthors() throws DBException;
	
	
	/**
	 * 
	 * @return number of authors in database
	 * @throws DBException if anything goes wrong with the database query
	 */
	public Integer countAuthors() throws DBException;
	
	
	/**
	 * Query an author by name
	 * @param name the name used to filter the query.
	 * @return an author from the database whose name is <i>name</i>
	 * @throws DBException if anything goes wrong with the database query
	 */
	public Author findByName(String name) throws DBException;
	
	/**
	 * Return the audio production of an author.
	 * 
	 * @param key the primary key of the author.
	 * @return the audio production of the author whose primary key equals <i>keys</i>
	 * @throws DBException if anything goes wrong with the database query
	 */
	public List<Audio> findAudioProductionByAuthorKey(Integer key) throws DBException;

	/**
	 * Return the production of the whole database
	 * 
	 * @return A list with all audio data within the database
	 */
	public List<Audio> listAllProduction() throws DBException;
	
	/**
	 * Returns the execution history starting from <i>start</i> until 
	 * <i>end</i>.
	 * @param start the start date of the considered period
	 * @param today the end date of the considered period
	 *
	 * @return a map with the execution history, where the key entry is a
	 * specific audio and the values entry is a list with the date time of 
	 * in which the audio was executed
	 */
	public abstract Map<Author, Map<Audio, List<Date>>> executionHistory(Date start, Date end) throws DBException;

	/**
	 * Register (or save) the execution of an audio
	 * 
	 * @param audio the audio that been executed
	 * @param time the time of the execution
	 */
	public void saveExecutionHistory(Audio audio, Date time) throws DBException;
}

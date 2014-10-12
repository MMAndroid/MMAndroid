package br.unb.mobileMedia.core.db;

import java.util.List;

import br.unb.mobileMedia.core.domain.AudioOld;

/**
 * A Data Access Object for handling the persistence 
 * mechanism of authors. 
 *  
 * @author Thiago Cavalcanti
 */
public interface AudioDAOOld {

	/**
	 * Save an author in the author's database.
	 * 
	 * @param author - the author that will be persisted. 
	 */
	public void saveAudio(AudioOld audio) throws DBException;

	/**
	 * List all audio of the MMUnB database.
	 * @return list of registered audios.
	 * @throws DBException if anything goes wrong with the database query
	 */
	public List<AudioOld> listAudio() throws DBException;
	

}

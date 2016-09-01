package br.unb.mobileMedia.core.db;

import java.util.List;

import br.unb.mobileMedia.core.domain.Audio;

public interface IMediaDao {
		
	/**
	 * Save an audio in the audio's database.
	 * 
	 * @param audio - the audio that will be persisted. 
	 */
	public void saveAudio(Audio audio) throws DBException;
	
	
	/**
	 * List all audio of the MMUAndroid database.
	 * @return list of registered audios.
	 */
	public Audio listAudioById(Integer audioId) throws DBException;
	
	
	/**
	 * List all audio of the MMUAndroid database.
	 * @return list of registered audios.
	 */
	public List<Audio> listAudioByTitle(Audio audio) throws DBException;
	
	
	/**
	 * List all audio of the MMUAndroid database.
	 * @return list of registered audios.
	 */
	public List<Audio> listAudioByAlbum(Integer albumId) throws DBException;
	
	/**
	 * List all audio of the MMUAndroid database.
	 * @return list of registered audios.
	 */
	public List<Audio> listAudioByPath(Audio audio) throws DBException;
	
	
	
	
	public List<Audio> listAllAudio() throws DBException;
	
		
	
	/**
	 * 
	 * @return number of audios in database
	 * @throws DBException if anything goes wrong with the database query
	 */
	public Integer countAudios() throws DBException;
	
	/**
	 * Update audio of the MMUAndroid database.
	 * @return true
	 * @throws DBException if not update 
	 */
	public boolean updateAudio(Audio audio) throws DBException;
	
	
	/**
	 * Delete audio of the MMUAndroid database.
	 * @return true
	 * @throws DBException if not delete 
	 */
	public boolean deleteAudio(Audio audio) throws DBException;
	
	
	public List<Audio> listAllAudioPaginated(Integer ofset, Integer limit) throws DBException;

	
}

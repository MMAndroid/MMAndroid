package br.unb.mobileMedia.core.db;

import java.util.List;

import br.unb.mobileMedia.core.domain.Audio;

public interface IAudioDao {
		
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
	public Audio listAudioById(Audio audio) throws DBException;
	
	
	/**
	 * List all audio of the MMUAndroid database.
	 * @return list of registered audios.
	 */
	public List<Audio> listAudioByTitle(Audio audio) throws DBException;
	
	
	/**
	 * List all audio of the MMUAndroid database.
	 * @return list of registered audios.
	 */
	public List<Audio> listAudioByAlbum(Long albumId) throws DBException;
	
	/**
	 * List all audio of the MMUAndroid database.
	 * @return list of registered audios.
	 */
	public List<Audio> listAudioByPath(Audio audio) throws DBException;
	
	
	
	
	public List<Audio> listAllAudio() throws DBException;
	
	
	/**
	 * @param albumId
	 * @return list content all audio of an album
	 */
	public List<Audio> listAllAudioByAlbum(Long albumId) throws DBException;
	
	
	public Long countAllAudio() throws DBException;
	
	
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
	
	
	List<Audio> listAllAudioPaginated( int ofset, int limit) throws DBException;

	
}

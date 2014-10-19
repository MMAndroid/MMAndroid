package br.unb.mobileMedia.core.db;

import java.util.List;

import br.unb.mobileMedia.core.domain.Audio;

public interface IAudioDao {
	
	public void createDb() throws DBException;
	
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
	public List<Audio> listAudioById(Audio audio);
	
	
	/**
	 * List all audio of the MMUAndroid database.
	 * @return list of registered audios.
	 */
	public List<Audio> listAudioByTitle(Audio audio);
	
	
	/**
	 * List all audio of the MMUAndroid database.
	 * @return list of registered audios.
	 */
	public List<Audio> listAudioByAlbum(Audio audio);
	
	/**
	 * List all audio of the MMUAndroid database.
	 * @return list of registered audios.
	 */
	public List<Audio> listAudioByPath(Audio audio);
	
	
	
	
	public List<Audio> listAllAudio();
	
	
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
	
}

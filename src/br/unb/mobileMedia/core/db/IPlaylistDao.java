package br.unb.mobileMedia.core.db;

import java.util.List;

import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Playlist;

public interface IPlaylistDao {

	/**
	 * Create a new empty playlist on the database. 
	 * 
	 * @param playlist - the playlist that will be persisted. 
	 * @throws DBException if anything goes wrong with the database query
	 */
	public void newPlaylist(Playlist playlist) throws DBException;
	
	/**
	 * Add a list of medias to a playlist
	 * @param playlistId - id from the playlist which will get new medias  
	 * @param mediaList - list of id's from the medias that will be added to the playlist
	 * @throws DBException if anything goes wrong with the database query
	 */
	void addToPlaylist(int playlistId, List<Integer> mediaList) throws DBException; 
	
	/**
	 * List all playlists from the MMUnB database.
	 * @return list of playlists.
	 * @throws DBException if anything goes wrong with the database query
	 */
	List<Playlist> listPlaylists() throws DBException;
	
	
	
	/**
	 * Count number of all playlists from the MMUnB database.
	 * @return number total of playlists.
	 * @throws DBException if anything goes wrong with the database query
	 */
	public Integer countPlaylists() throws DBException;
	

	
	/**
	 * Get a playlist by id. The playlist returned do not have any media 
	 * @param idPlaylist - the id from the playlist which will be returned 
	 * @return a playlist.
	 * @throws DBException if anything goes wrong with the database query
	 */
	public Playlist getPlaylistById(Integer playlistId) throws DBException;
	
	/**
	 * Get a playlist by name. The playlist returned do not have any media
	 * @param namePlaylist - the name from the playlist which will be returned 
	 * @return a playlist.
	 * @throws DBException if anything goes wrong with the database query
	 */
	public Playlist getPlaylistByName(String name) throws DBException;
	
	
	
	/**
	 * Delete a playlist by name.
	 * @param id - the name from the playlist which will be deleted 
	 * @throws DBException if anything goes wrong with the database query
	 */
	public void deletePlaylist(Long id) throws DBException;
	
	/**
	 * Edit a playlist by id.
	 * @param editedPlaylist - a Playlist object containing the id from the playlist
	 * to be edited and the other fields with the new values
	 * @throws DBException if anything goes wrong with the database query
	 */
	public void editPlaylist(Playlist editedPlaylist) throws DBException;
	
	/**
	 * Add a geographical position a plalist.
	 * @param PlaylistOld  - a Playlist object containing the id from the playlist
	 * @param latitude
	 * @param longitude
	 * @throws DBException if anything goes wrong with the database query
	 */
	public void addPositionPlaylist(Playlist playlist, double latitude, double longitude) throws DBException;
	

	
	
	/**
	 * Get all the musics from a playlist
	 * @param idPlaylist - the id from the playlist which contains the list of musics
	 * @throws DBException if anything goes wrong with the database query
	 */
	public List<Audio> getMusicFromPlaylist(int idPlaylist) throws DBException;
	
}

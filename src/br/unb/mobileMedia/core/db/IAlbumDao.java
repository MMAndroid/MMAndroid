package br.unb.mobileMedia.core.db;

import java.util.List;

import br.unb.mobileMedia.core.domain.Album;
import br.unb.mobileMedia.core.domain.Audio;


public interface IAlbumDao {
	/**
	 * Save an album in  database.
	 * 
	 * @param author - the author that will be persisted. 
	 */
	public void saveAlbum(Album albums) throws DBException;
	
	

	/**
	 * Return the albums of an author in database
	 * 
	 * @return A list with all album an specific author within the database
	 */
	public List<Album> listAlbumsByAuthor(Integer authorId) throws DBException;
	
	
	
	/**
	 * Return the production of the whole database
	 * 
	 * @return A list with all audio data within the database
	 */
	public List<Audio> listAllProductionOfAlbum(Integer albumId) throws DBException;
	
	
	/**
	 * List all albums of the MMAndroid database.
	 * @return list of registered album.
	 * @throws DBException if anything goes wrong with the database query
	 */
	public List<Album> listAlbums() throws DBException;
	
	
	
	/**
	 * Query an album by name
	 * @param name the name used to filter the query.
	 * @return an album from the database whose name is <i>name</i>
	 * @throws DBException if anything goes wrong with the database query
	 */
	public Album findByName(String name) throws DBException;
	

	
	
	/**
	 * Return the number of album in database
	 * 
	 * @return A list with all audio data within the database
	 */
	public Integer countAlbum() throws DBException;
	
	
}

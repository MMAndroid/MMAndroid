package br.unb.mobileMedia.core.domain;

import java.util.List;

/**
 * Represents a Playlist of medias. 
 * 
 * @author willian
 */
public class Playlist {
	
	private int id;
	private String name;
	private List<Audio> mediaList;

	public Playlist() {
		mediaList = null;
	}
	
	public Playlist(String name) {
		this.name = name;
		mediaList = null;
	}

	/* getters and setters */

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<Audio> getMediaList() {
		return mediaList;
	}

	public void setMediaList(List<Audio> mediaList) {
		this.mediaList = mediaList;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof Playlist) && ((Playlist)o).getName().equals(name);
	}
}

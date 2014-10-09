package br.unb.mobileMedia.core.domain;

import java.util.List;

/**
 * Represents a Playlist of medias. 
 * 
 * @author willian
 */
public class PlaylistOld {
	
	private int id;
	private String name;
	private List<AudioOld> mediaList;

	public PlaylistOld() {
		mediaList = null;
	}
	
	public PlaylistOld(String name) {
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
	
	public List<AudioOld> getMediaList() {
		return mediaList;
	}

	public void setMediaList(List<AudioOld> mediaList) {
		this.mediaList = mediaList;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof PlaylistOld) && ((PlaylistOld)o).getName().equals(name);
	}
}

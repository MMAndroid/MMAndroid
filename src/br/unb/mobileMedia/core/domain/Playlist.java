package br.unb.mobileMedia.core.domain;

public class Playlist {
	private Integer id;
	private String name;
	
	public Playlist() {
	}

	public Playlist(Integer id) {
		this.id = id;
	}

	public Playlist(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
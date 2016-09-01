package br.unb.mobileMedia.core.domain;

import java.net.URI;

/**
 * Represents a multimedia content, which could be a photo, a picture, a video, or
 * an audio.
 * 
 * @author rbonifacio
 */
public abstract class MultimediaContent implements MultimediaRelated {

	protected Integer primaryKey; //this is the database id
	protected Integer id;  //this is the <i>business</i> id 
	protected String title;
	protected URI uri;
	
	public MultimediaContent(Integer id, String title, URI uri) {
		this(title, uri);
		this.id = id;
		this.uri = uri;
	}
	
	public MultimediaContent(String title, URI uri) {
		this.title = title;
		this.uri = uri;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public URI getURI() {
		return uri;
	}

	public void setURL(URI uri) {
		this.uri = uri;
	}

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public void setKey(Integer primaryKey) {
		this.primaryKey = primaryKey;
	}

}

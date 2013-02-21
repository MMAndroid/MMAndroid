package br.unb.mobileMedia.core.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the author of a multimedia content (such as a photo, a picture, a
 * video, or an audio.
 * 
 * @author rbonifacio
 */
public class Author implements MultimediaRelated {

	private Integer key; // this will be the database id
	private Integer id;  // this is a <i>business</i> author id
	private String name;
	private List<MultimediaContent> production;

	public Author(Integer id, String name) {
		this(name);
		this.id = id;
	}
	
	public Author(String name) {
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
	
	/**
	 * Add a multimedia content to the author list of production 
	 * 
	 * @param content a multimedia content.
	 */
	public void addProduction(MultimediaContent content) {
		if(production == null) {
			production = new ArrayList<MultimediaContent>();
		}
		production.add(content);
	}
	
	/**
	 * Return a specific multimedia content from the 
	 * author list of production. 
	 * 
	 * @param id index of the multimedia content
	 * @return A multimedia content from the author list of production
	 * @throws ArrayIndexOutOfBoundsException if id < 0 or id >= sizeOfProduction()
	 */
	public MultimediaContent getContentAt(int id) throws ArrayIndexOutOfBoundsException  {
		if(production == null) {
			production = new ArrayList<MultimediaContent>();
		}
		
		if(id < 0 || id >= sizeOfProduction()) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		return production.get(id);
	}

	public int sizeOfProduction() {
		if(production == null) {
			return 0;
		}
		
		return production.size();
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof Author) && ((Author)o).getKey().equals(this.getKey());
	}
	
	
}

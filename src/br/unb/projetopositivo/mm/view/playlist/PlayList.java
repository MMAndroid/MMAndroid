package br.unb.projetopositivo.mm.view.playlist;

public class PlayList {
	
	private String name;
	private String description;
	
	public PlayList(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	

}

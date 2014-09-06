package br.unb.mobileMedia.core.FileChooser;

public class FileDetail implements Comparable<FileDetail> {

	private String name;
	private String data;
	private String path;

	public FileDetail(String name, String data, String path) {
		this.name = name;
		this.data = data;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int compareTo(FileDetail f) {
		if (this.name != null) {
			return this.name.toLowerCase().compareTo(f.getName().toLowerCase());
		} else {
			throw new IllegalArgumentException();
		}
	}
	
}

package br.unb.mobileMedia.core.FileChooser;

public class FileDetail implements Comparable<FileDetail> {

	private int    icon;
	private String name;
	private String data;
	private String path;

	public FileDetail(int icon, String name, String data, String path) {
		this.icon = icon;
		this.name = name;
		this.data = data;
		this.path = path;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
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

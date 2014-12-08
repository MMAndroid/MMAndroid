package br.unb.mobileMedia.core.domain;

/**
 * An enumeration for the supported video formats.
 * 
 * @author michael
 */
public enum VideoFormats {
	
	MP4("mp4"), _3GP("3gp");
	
	private String format;
	
	private VideoFormats(String format) {
		this.format = format.replace("_", "");
	}
	
	public String getFormatAsString() {
		return format.replace("_", "");
	}
}

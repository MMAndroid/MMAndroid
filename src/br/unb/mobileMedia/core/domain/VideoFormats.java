package br.unb.mobileMedia.core.domain;

/**
 * An enumeration for the supported video formats.
 * 
 * @author WillianJunior
 */
public enum VideoFormats {
	
	MP3("mp4");
	
	private String format;
	
	private VideoFormats(String format) {
		this.format = format;
	}
	
	public String getFormatAsString() {
		return format;
	}
}

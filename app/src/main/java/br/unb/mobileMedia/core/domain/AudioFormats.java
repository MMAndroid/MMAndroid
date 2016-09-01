package br.unb.mobileMedia.core.domain;

/**
 * An enumeration for the supported audio formats.
 * 
 * @author rbonifacio
 */
public enum AudioFormats {
	
	MP3("mp3"),
	M4A("m4a");
	
	private String format;
	
	private AudioFormats(String format) {
		this.format = format;
	}
	
	public String getFormatAsString() {
		return format;
	}
}

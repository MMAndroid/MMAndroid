package br.unb.mobileMedia.playlist;


public class AudioViewItem {

	public final Integer   id;
	public final String titleAudio;
	public final String albumAudio;
	public final String artistaAudio;
	public final String bitRate;
	
	public AudioViewItem(Integer id, String titleAudio, String albumAudio, String artistaAudio, String bitRate){
		this.id       = id;
		this.titleAudio = titleAudio;
		this.albumAudio = albumAudio;
		this.artistaAudio = artistaAudio;
		this.bitRate = bitRate;
	}
	
}    



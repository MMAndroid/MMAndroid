package br.unb.mobileMedia.playlist;

import android.graphics.Bitmap;

public class AudioViewItem {

	public final Long   id;
	public final Bitmap imageArt;
	public final String titleAudio;
	public final String albumAudio;
	public final String artistaAudio;
	public final String bitRate;
	
	public AudioViewItem(Long id, Bitmap imageArt, String titleAudio, String albumAudio, String artistaAudio, String bitRate){
		this.id       = id;
		this.imageArt = imageArt;
		this.titleAudio = titleAudio;
		this.albumAudio = albumAudio;
		this.artistaAudio = artistaAudio;
		this.bitRate = bitRate;
	}
	
}    



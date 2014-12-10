package br.unb.mobileMedia.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import br.unb.mobileMedia.core.domain.Audio;

public class AudioToParcelable implements Parcelable {

	private Audio audio;

	public Audio getAudio(){
		return audio;
	}
	
	public AudioToParcelable(Audio audio){
		super();
		this.audio = audio;
	}
	
	
	private AudioToParcelable(Parcel in){
		audio = new Audio();
		audio.setId(in.readInt());
		audio.setUrl(in.readString());
		audio.setAlbumId(in.readInt());
	}
	
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		Log.v("writeToParcel", ""+flags);
		dest.writeInt(audio.getId());
		dest.writeString(audio.getUrl());
		dest.writeInt(audio.getAlbumId());
	}
	
	
	public static final Parcelable.Creator<AudioToParcelable> CREATOR =
			new Creator<AudioToParcelable>(){
		
		public AudioToParcelable createFromParcel(Parcel in){
			return new AudioToParcelable(in);
		}
		
		public AudioToParcelable[] newArray(int size){
			return new AudioToParcelable[size];
		}
		
	};
	
	
	
	
	
    
    
    
   
}

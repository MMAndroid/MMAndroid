package br.unb.mobileMedia.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AlbumToParcelable implements Parcelable {

	private Long id;
	private String name;
	private byte[] imageByte;
	private Long authorId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getImageByte() {
		return imageByte;
	}

	public void setImageByte(byte[] imageByte) {
		this.imageByte = imageByte;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}	
	
	
	public static final Parcelable.Creator<AlbumToParcelable> CREATOR =
			new Creator<AlbumToParcelable>(){
		
		public AlbumToParcelable createFromParcel(Parcel source){
			AlbumToParcelable album = new AlbumToParcelable();
			
			album.id = source.readLong();
			album.name = source.readString();
			source.readByteArray(album.imageByte);
			album.authorId = source.readLong();
			
			return album;
		}

		public AlbumToParcelable[] newArray(int size) {
			return new AlbumToParcelable[size];
		}
		
	};
	
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		Log.v("writeToParcel", ""+flags);
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeByteArray(imageByte);
		dest.writeLong(authorId);
	}
	
	
    
    
    
   
}

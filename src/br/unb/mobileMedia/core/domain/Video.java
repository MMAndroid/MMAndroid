package br.unb.mobileMedia.core.domain;

import java.net.URI;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents an Video Multimedia content.
 *
 * Note, its necessary to implement serializable, because it would 
 * be necessary to share video between activities. Probably, we should 
 * turn MediaContent serializable either. 
 * 
 * @author WillianJunior
 */
public class Video extends MultimediaContent implements Parcelable {

	public static final long serialVersionUID = 1L;

	public Video(Integer pk, Integer id, String title, URI uri) {
		this(id, title, uri);
		this.primaryKey = pk;
	}
	
	/**
	 * Video constructor.
	 * @param id video id
	 * @param title video title 
	 * @param url video url
	 */
	
	public Video(Integer id, String title, URI uri) {
		super(id, title, uri);
	}
	
	/**
	 * Video constructor. 
	 * @param title video title 
	 * @param url video url
	 */
	public Video(String title, URI uri) {
		super(title, uri);
	}

	/* getters and setters. there is not much to explain. */
	
	public String toString() {
		return title;
	}

	public int describeContents() {
		return 0;
	}

	
	@Override
	public boolean equals(Object o) {
		return (o instanceof Video) && ((Video)o).getPrimaryKey().equals(primaryKey);
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(primaryKey);
		dest.writeInt(id);
		dest.writeString(title);
		dest.writeSerializable(uri);
	}
	
	 public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {

		public Video createFromParcel(Parcel source) {
			int pk = source.readInt();
			int id = source.readInt(); 
			String title = source.readString();
			URI uri = (URI)source.readSerializable();
			
			Video video = new Video(pk, id, title, uri);
			
			return video;
		}

		public Video[] newArray(int size) {
			return new Video[size];
		}
	
	 }; 
}

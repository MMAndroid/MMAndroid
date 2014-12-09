package br.unb.mobileMedia.adapters;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.domain.Audio;

/**
 * Maps audios inside ArrayLists into TextView fields.
 */
public class AdapterAudio extends BaseAdapter {

	private List<Audio> songs;
	private LayoutInflater songInflater;
	
	public AdapterAudio(Context c, List<Audio> songs) {
		this.songs = songs;
		songInflater = LayoutInflater.from(c);
	}
	
	public int getCount() {
		return songs.size();
	}

	public Object getItem(int position) {
		return songs.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LinearLayout songLayout = (LinearLayout)songInflater.inflate(R.layout.menu_item_song, parent, false);
		
		TextView titleView  = (TextView)songLayout.findViewById(R.id.menu_item_song_title);
		TextView artistView = (TextView)songLayout.findViewById(R.id.menu_item_song_artist);
		TextView albumView  = (TextView)songLayout.findViewById(R.id.menu_item_song_album);
		
		Audio currentSong = songs.get(position);
		
		String title = currentSong.getTitle();
		if (title.isEmpty()) {
			titleView.setText("<unknown>");
		} else {
			titleView.setText(currentSong.getTitle());
		}
		
		String artist = currentSong.getArtist();
		if (artist.isEmpty()) {
			artistView.setText("<unknown>");
		} else { 
			artistView.setText(currentSong.getArtist());
		}
		
		String album = currentSong.getAlbum();
		if (album.isEmpty()) { 
			albumView.setText("<unknown>");
		} else { 
			albumView.setText(currentSong.getAlbum());
		}
		
		songLayout.setTag(position);
		return songLayout;
	}
	
}

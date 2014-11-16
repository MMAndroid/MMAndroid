package br.unb.mobileMedia.core.view;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.domain.Album;
import br.unb.mobileMedia.core.domain.Author;

public class AlbumArrayAdapter extends ArrayAdapter<Album> {

	private Context context;
	private LayoutInflater mInflater;
	
	public AlbumArrayAdapter(Context context, List<Album> albums) {
		super(context, R.layout.author_row);
		this.context = context;
		
		this.mInflater = LayoutInflater.from(context);
		
		Iterator<Album> iterator = albums.iterator();
		while (iterator.hasNext()) {
			this.add(iterator.next());
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder v;
		
		if(convertView == null){
			
			convertView = mInflater.inflate(R.layout.album_row, parent, false);
			v = new ViewHolder();
			v.imageArt = (ImageView) convertView.findViewById(R.id.albumArt);
			v.nameAlbum = (TextView) convertView.findViewById(R.id.nameAlbum);
			v.numAudioInAldum = (TextView) convertView.findViewById(R.id.numAudioInAldum);
		
			convertView.setTag(v);
		}else{
        	v = (ViewHolder) convertView.getTag();
        }
		
		Album item = getItem(position);
		
		v.imageArt.setBackgroundResource(R.drawable.adele);
		v.nameAlbum.setText(item.getName());
		v.numAudioInAldum.setText("Audios: "+item.getAudioAlbum().size()+".");

		
		return convertView;
	}
	
	private static class ViewHolder{
		ImageView imageArt;
		TextView nameAlbum;
		TextView numAudioInAldum;
	}
}

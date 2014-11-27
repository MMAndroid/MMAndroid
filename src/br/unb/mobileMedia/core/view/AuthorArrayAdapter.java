package br.unb.mobileMedia.core.view;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.domain.Album;
import br.unb.mobileMedia.core.domain.Author;
import br.unb.mobileMedia.core.manager.Manager;

public class AuthorArrayAdapter extends ArrayAdapter<Author> {

	private Context context;
	private LayoutInflater mInflater;
	
	public AuthorArrayAdapter(Context context, List<Author> authors) {
		super(context, R.layout.author_row);
		this.context = context;
		
		this.mInflater = LayoutInflater.from(context);
		
		Iterator<Author> iterator = authors.iterator();
		while (iterator.hasNext()) {
			this.add(iterator.next());
		}
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder v;
		
		if(convertView == null){
			
			convertView = mInflater.inflate(R.layout.author_row, parent, false);
			v = new ViewHolder();
			v.imageArt = (ImageView) convertView.findViewById(R.id.AuthorArt);
			v.nameAuthor = (TextView) convertView.findViewById(R.id.NameAuthor);
			v.numAlbumAuthor = (TextView) convertView.findViewById(R.id.numAlbumAuthor);
		
			convertView.setTag(v);
		}else{
        	v = (ViewHolder) convertView.getTag();
        }
		
		Author item = getItem(position);
		
		v.imageArt.setBackgroundResource(R.drawable.adele);
		v.nameAuthor.setText(item.getName());

		List<Album>AlbumAuthor = Manager.instance().getAlbumByAuthor(context, item.getId()) ;

		if(AlbumAuthor == null)
			v.numAlbumAuthor.setText("Albums: "+ 0	+".");
		else
			v.numAlbumAuthor.setText("Albums: "+ AlbumAuthor.size()	+".");
		


		
		return convertView;
	}
	
	private static class ViewHolder{
		ImageView imageArt;
		TextView nameAuthor;
		TextView numAlbumAuthor;
	}
}

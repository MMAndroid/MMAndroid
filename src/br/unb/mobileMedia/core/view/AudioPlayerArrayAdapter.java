package br.unb.mobileMedia.core.view;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.domain.Audio;

public class AudioPlayerArrayAdapter extends ArrayAdapter<Audio> {

	private Context context;
	
	public AudioPlayerArrayAdapter(Context context, Audio[] objects) {
		super(context, R.layout.author_row);
		this.context = context;
		List<Audio> objectsVector = Arrays.asList(objects);
		Iterator<Audio> iterator = objectsVector.iterator(); 
		while (iterator.hasNext()) {
			add(iterator.next());
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.author_row, parent,false);
		TextView textView = (TextView) rowView.findViewById(R.id.NameAuthor);
		
		textView.setText("Position: "+getItem(position)+" getTitle with mediaExtractor");
		
		return rowView;
	}

	
}

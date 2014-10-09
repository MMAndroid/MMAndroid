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
import br.unb.mobileMedia.core.domain.AudioOld;

public class AudioPlayerArrayAdapter extends ArrayAdapter<AudioOld> {

	private Context context;
	
	public AudioPlayerArrayAdapter(Context context, AudioOld[] objects) {
		super(context, R.layout.video_row_layout);
		this.context = context;
		List<AudioOld> objectsVector = Arrays.asList(objects);
		Iterator<AudioOld> iterator = objectsVector.iterator(); 
		while (iterator.hasNext()) {
			add(iterator.next());
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.video_row_layout, parent,false);
		TextView textView = (TextView) rowView.findViewById(R.id.txt_name);
		
		textView.setText(getItem(position).getTitle());
		
		return rowView;
	}

	
}

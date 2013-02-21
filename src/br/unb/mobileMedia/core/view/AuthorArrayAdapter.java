package br.unb.mobileMedia.core.view;

import java.util.Iterator;
import java.util.List;

import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.domain.Author;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AuthorArrayAdapter extends ArrayAdapter<Author> {

	private Context context;
	
	public AuthorArrayAdapter(Context context, List<Author> authors) {
		super(context, R.layout.video_row_layout);
		this.context = context;
		Iterator<Author> iterator = authors.iterator();
		while (iterator.hasNext()) {
			this.add(iterator.next());
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.video_row_layout, parent,false);
		TextView textView = (TextView) rowView.findViewById(R.id.txt_name);
		
		textView.setText(getItem(position).getName());
		
		return rowView;
	}
}

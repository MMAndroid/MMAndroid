package br.unb.projetopositivo.mm.view.video;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.unb.projetopositivo.mm.R;

public class VideoFileArrayAdapter extends ArrayAdapter<File> {
	private final Context context;

	public VideoFileArrayAdapter(Context context, List<File> values) {
		super(context, R.layout.video_row_layout);
		this.context = context;
//		this.addAll(values);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.video_row_layout, parent,false);
		TextView textView = (TextView) rowView.findViewById(R.id.txt_name);
		TextView sizeView = (TextView) rowView.findViewById(R.id.txt_size);

		File file = getItem(position);
		textView.setText(file.getName() + "");
//		sizeView.setText(file.getTotalSpace() + " bytes");

		return rowView;
	}
	
	

}
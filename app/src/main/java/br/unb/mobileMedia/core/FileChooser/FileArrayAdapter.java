package br.unb.mobileMedia.core.FileChooser;

import java.util.List;

import br.unb.mobileMedia.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileArrayAdapter extends ArrayAdapter<FileDetail> {

	private Context c;
	private List<FileDetail> items;
	private int id;

	public FileArrayAdapter(Context context, int textViewResourceId, List<FileDetail> objects) {
		
        super(context, textViewResourceId, objects);
        
		c = context;
		id = textViewResourceId;
		items = objects;
		
		Log.i("FileArrayAdapter", "Ok");
		Log.i("FileEnviada", items.get(0).getPath());
		
	}

	
	public FileDetail getItem(int i) {
		return items.get(i);
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
    
		if (rowView == null) {
           LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           rowView = vi.inflate(id, null);
		}
		
		final FileDetail f = items.get(position);

		if (f != null) {
			
			ImageView icon = (ImageView) rowView.findViewById(R.id.item_icon);
			TextView t1 = (TextView) rowView.findViewById(R.id.nameFile);//Adiciona o nome do arquivo ou diretorio
//			TextView t2 = (TextView) rowView.findViewById(R.id.sizeFile); // adiciona o tamnaho do arquivo			

			if (t1 != null){
				icon.setImageResource(f.getIcon());
				t1.setText(f.getName().toString());
			}
			
//			if (t2 != null)
//				t2.setText(f.getData());

		}
		return rowView;
	}

}


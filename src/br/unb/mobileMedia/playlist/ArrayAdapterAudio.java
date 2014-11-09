package br.unb.mobileMedia.playlist;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import br.unb.mobileMedia.R;



@SuppressLint("InflateParams")
public class ArrayAdapterAudio extends ArrayAdapter<AudioViewItem> {

	private SparseBooleanArray mSelectedItemsIds;
	private List<AudioViewItem> items;
	private ListView listView;
	private Context context;
	private LayoutInflater mInflater;

	public ArrayAdapterAudio(Context context, int textViewResourceId,  List<AudioViewItem> items, ListView listView) {
		super(context, textViewResourceId, items);
		mSelectedItemsIds = new SparseBooleanArray();
		this.items = items;
		this.listView = listView;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
    
		View row = convertView;
		
		ViewHolder h;
		
        if(row == null){
        	
            row = mInflater.inflate(R.layout.audio_row, parent, false);
            
            h = new ViewHolder();
            
            h.artAlbum = (ImageView) row.findViewById(R.id.AlbumArt);
    		h.titleAudio = (TextView) row.findViewById(R.id.TitleItem);
    		h.artistaAlbum = (TextView) row.findViewById(R.id.ArtistaItem);
    		h.albumAudio = (TextView) row.findViewById(R.id.AlbumItem);
    		h.bitRate = (TextView) row.findViewById(R.id.bitRate);
    		
    		row.setTag(h);
        	
        }else{
        	h = (ViewHolder) row.getTag();
        }
        
		
		
        AudioViewItem item = items.get(position);
        
        
        
		h.artAlbum.setImageBitmap(item.imageArt);
		h.titleAudio.setText(item.titleAudio);
		h.artistaAlbum.setText(item.artistaAudio);
		h.albumAudio.setText(item.albumAudio);
	
		int br = Integer.parseInt(item.bitRate);
		br = (br == 0) ? 0 : (br/1000);
		h.bitRate.setText(br + " Kbps"); 
		
		return row;
	}
	
	

 
	
	 private static class ViewHolder {
		 ImageView artAlbum;
		 TextView titleAudio;
		 TextView albumAudio;
		 TextView artistaAlbum;
		 TextView bitRate;
	 }
	

	 public void toggleSelection(int position) {
			selectView(position, !mSelectedItemsIds.get(position));
		}

	public void removeSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	public void selectView(int position, boolean value) {
		if (value)
			mSelectedItemsIds.put(position, value);
		else
			mSelectedItemsIds.delete(position);
		notifyDataSetChanged();
	}

	public int getSelectedCount() {
		return mSelectedItemsIds.size();
	}

	public SparseBooleanArray getSelectedIds() {
		return mSelectedItemsIds;
	}
	
	
}

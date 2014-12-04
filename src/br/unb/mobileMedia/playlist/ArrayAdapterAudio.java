package br.unb.mobileMedia.playlist;

import java.util.List;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import br.unb.mobileMedia.R;



public class ArrayAdapterAudio extends ArrayAdapter<AudioViewItem> {

	private SparseBooleanArray mSelectedItemsIds;
	private List<AudioViewItem> items;
	private Context context;
	private LayoutInflater mInflater;
	
	
	public ArrayAdapterAudio(Context context, int textViewResourceId,  List<AudioViewItem> items, ListView listView) {
		super(context, textViewResourceId, items);
		mSelectedItemsIds = new SparseBooleanArray();
		this.items = items;
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
        	h.titleAudio = (TextView) row.findViewById(R.id.TitleItem);
    		h.artistaAlbum = (TextView) row.findViewById(R.id.ArtistaItem);
    		h.albumAudio = (TextView) row.findViewById(R.id.AlbumItem);
    		h.bitRate = (TextView) row.findViewById(R.id.bitRate);
    		
    		row.setTag(h);
        	
        }else{
        	h = (ViewHolder) row.getTag();
        }

        AudioViewItem item = items.get(position);
        
		h.titleAudio.setText(item.titleAudio);
		h.artistaAlbum.setText(item.artistaAudio);
		h.albumAudio.setText(item.albumAudio);
	
		Integer br = Integer.valueOf(item.bitRate);
		br = br == null || br == 0 ? br : (br/1000);
		h.bitRate.setText(br + " kbps"); 
		
		return row;
	}
	

	private static class ViewHolder {
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
	

	public void swapItem(List<AudioViewItem> items) {
    	this.items.addAll(items);
    	notifyDataSetChanged();
	}
	
}

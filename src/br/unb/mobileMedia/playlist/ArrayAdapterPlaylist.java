package br.unb.mobileMedia.playlist;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import br.unb.mobileMedia.R;



@SuppressLint("InflateParams")
public class ArrayAdapterPlaylist extends ArrayAdapter<PlaylistViewItem> {

	private List<PlaylistViewItem> items;
	private ListView listView;
	private Context context;
	private LayoutInflater mInflater;

	public ArrayAdapterPlaylist(Context context, int textViewResourceId,  List<PlaylistViewItem> items, ListView listView) {
		super(context, textViewResourceId, items);
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
        	
            row = mInflater.inflate(R.layout.playlist_row, parent, false);
            
            h = new ViewHolder();
            
            h.titlePlaylist = (TextView) row.findViewById(R.id.title);
    		h.totalAudioInPlaylist = (TextView) row.findViewById(R.id.counter);
    	
    		row.setTag(h);
        	
        }else{
        	h = (ViewHolder) row.getTag();
        }
        
		
		
        PlaylistViewItem item = items.get(position);
        
        h.titlePlaylist.setText(item.name);
		h.totalAudioInPlaylist.setText(item.counter);
		
		return row;
	}
	

	
	 private static class ViewHolder {
		 TextView titlePlaylist;
		 TextView totalAudioInPlaylist;
	 }
	

	
}

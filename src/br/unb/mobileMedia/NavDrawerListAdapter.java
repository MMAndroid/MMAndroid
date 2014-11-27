package br.unb.mobileMedia;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavDrawerListAdapter extends  BaseAdapter{
	
	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;
	
	public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> items){
		this.context = context;
		this.navDrawerItems = items;
	}

	public int getCount() {
		return navDrawerItems.size();
	}

	public NavDrawerItem getItem(int position) {		
		return navDrawerItems.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	
	private static class ViewHolder{
		public  ImageView imgIcon;
		public  TextView txtTitle;
		public  TextView txtCount;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder v;
		
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, parent, false);
            
            v = new ViewHolder();
            v.imgIcon = (ImageView) convertView.findViewById(R.id.icon);
            v.txtTitle = (TextView) convertView.findViewById(R.id.title);
            v.txtCount = (TextView) convertView.findViewById(R.id.counter);
            
            convertView.setTag(v);
            
        }else{
        	v = (ViewHolder) convertView.getTag();
        }
		
	
		NavDrawerItem item = navDrawerItems.get(position);
         
		v.imgIcon.setImageResource(item.getIcon());        
		v.txtTitle.setText(item.getTitle());
        
        // displaying count
        // check whether it set visible or not
        if(item.getCounterVisibility()){
        	v.txtCount.setText(item.getCount());
        }else{
        	v.txtCount.setVisibility(View.GONE);
        }
        
        return convertView;
	}
	

	
	public void clear(){
		navDrawerItems.clear();
	}
	
	public void swapItems(ArrayList<NavDrawerItem> items){
		this.navDrawerItems.addAll(items);
		notifyDataSetChanged();
    }


}

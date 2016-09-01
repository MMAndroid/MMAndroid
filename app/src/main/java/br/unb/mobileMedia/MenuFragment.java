package br.unb.mobileMedia;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class MenuFragment extends Fragment {
	
	OnItemClickedCallBack callBack;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.menu, container, false);
		
		ImageButton button;
		
		button = ((ImageButton) view.findViewById(R.id.btn_list_authors));
		button.setOnClickListener(new OnClickMenuItemListener());
		
		button = ((ImageButton) view.findViewById(R.id.btn_open_music_player));
		button.setOnClickListener(new OnClickMenuItemListener());
				
//		button = ((ImageButton) view.findViewById(R.id.btn_synchronize));
//		button.setOnClickListener(new OnClickMenuItemListener());

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			callBack = (OnItemClickedCallBack) activity;
		} catch (ClassCastException e) {
			Log.e("MenuFragment", activity.toString() + " must implement OnItemClickedCallBack");
		}
	}
	
	protected class OnClickMenuItemListener implements View.OnClickListener {
		
		public void onClick(View v) {
			/** Call the callback method by pass the clicked button id */
			callBack.onItemClicked(v.getId());
		}
	}
}
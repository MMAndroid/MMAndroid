package br.unb.mobileMedia.core.view;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Album;
import br.unb.mobileMedia.core.domain.Author;
import br.unb.mobileMedia.core.manager.Manager;

/**
 * The fragment that displays the list of authors.
 * @author rbonifacio
 */
public class AlbumListFragment extends ListFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle("All Album");
		try {
			List<Album> albums = Manager.instance().listAlbums(getActivity().getApplicationContext());
			
			if (albums == null || albums.size() == 0) {
				String[] values = new String[] { "No album found." };
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);
				setListAdapter(adapter);
			} else {
				
//				List<Audio> audios = Manager.instance().listAllAudio(getActivity().getApplicationContext());
				
				AlbumArrayAdapter adapter = new AlbumArrayAdapter(getActivity(), albums);
				setListAdapter(adapter);
			}
		} catch (DBException e) {
			Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}

	/*
	 * This method is called whenever the user selects 
	 * one of the items of a list. In this case, whenever 
	 * the user selects on artist. 
	 * @see android.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Author item = (Author) getListAdapter().getItem(position);
		
		Bundle args = new Bundle();
		args.putInt(AudioExpandableListFragment.SELECTED_ARTIST_ID, item.getId().intValue());
		args.putString(AudioExpandableListFragment.SELECTED_ARTIST_NAME, item.getName());
		
		// TODO Extract this to a method (repeated in MMUnBActivity too)
		Fragment newFragment = new AudioExpandableListFragment();
		newFragment.setArguments(args);
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		if(getActivity().findViewById(R.id.main) != null){
			transaction.replace(R.id.main, newFragment);
			transaction.addToBackStack(null);
		}else{
			transaction.replace(R.id.content, newFragment);
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}
}

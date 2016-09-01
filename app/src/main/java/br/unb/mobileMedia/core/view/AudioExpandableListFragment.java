package br.unb.mobileMedia.core.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.domain.Album;
import br.unb.mobileMedia.core.domain.Audio;

public class AudioExpandableListFragment extends ListFragment {

	public static final String SELECTED_ARTIST_ID = "SELECTED_AUTHOR_ID";
	public static final String SELECTED_ARTIST_NAME = "SELECTED_ARTIST_NAME";

	private static final String ALBUM = "ALBUM";
	private static final String TITLE = "TITLE";
	private List<Map<String, String>> groupList;
	private List<List<Map<String, String>>> childList;
	private Map<String, List<Audio>> albuns;
	
	
	private List<Album> albumsByAuthor;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle args = getArguments();
    	
    	if(args!=null && args.containsKey(SELECTED_ARTIST_ID) && args.getLong(SELECTED_ARTIST_ID, -1) != -1) {
//    		albumsByAuthor = Manager.instance().getAlbumByAuthor(getActivity().getApplicationContext(), args.getLong(SELECTED_ARTIST_ID, -1));
        }
    	
        
    	
		getActivity().getActionBar().setTitle(
							getActivity().getResources().getString(R.string.title_author_expandable_list) +" "+
							args.getString(SELECTED_ARTIST_NAME)
							);

    }
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		List<Audio> production = new ArrayList<Audio>();
        try {
        	
       		Bundle args = getArguments();
        	
        	if(args!=null && args.containsKey(SELECTED_ARTIST_ID) && args.getLong(SELECTED_ARTIST_ID, -1) != -1) {
//        		albumsByAuthor = Manager.instance().getAlbumByAuthor(getActivity().getApplicationContext(), args.getLong(SELECTED_ARTIST_ID, -1));
            }
        	
        	
        	
        	albuns = groupByAlbum(production);
        	
        	String[] values =  new String[]{"Cheese", "Pepperoni", "Black Olives"};
//        	for(Album album : albumsByAuthor){
//        		values[i] = album.getName();
//        		i++;
//        	}
        	
        	
        	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);
			setListAdapter(adapter);
        	
//        	getActivity().setTitle("Albuns from " + args.getString(SELECTED_ARTIST_NAME));
//        	
//        	groupList = createGroupList(albuns);
//        	childList = createChildList(albuns);
        	 
//        	SimpleExpandableListAdapter expListAdapter =
//        			new SimpleExpandableListAdapter(
//        				getActivity(),
//        				groupList,								// groupData describes the first-level entries
//        				R.layout.child_row,						// Layout for the first-level entries
//        				new String[] { ALBUM },					// Key in the groupData maps to display
//        				new int[] { R.id.childname },			// Data under "colorName" key goes into this TextView
//        				childList,								// childData describes second-level entries
//        				R.layout.child_row,						// Layout for second-level entries
//        				new String[] { TITLE },					// Keys in childData maps to display
//        				new int[] { R.id.childname }			// Data under the keys above go into these TextViews
//        			);
//        	setListAdapter( expListAdapter );
//        	getExpandableListView().setOnChildClickListener(this);
        }
        catch(Exception e) {
        	e.printStackTrace();
        	Toast.makeText(getActivity().getApplicationContext(), "Exception: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        
		
	}

//	@Override
//	public boolean onChildClick(ExpandableListView parent, View v,
//			int groupPosition, int childPosition, long id) {
//	
//		Toast.makeText(getActivity().getApplicationContext(), "Abrindo player...", Toast.LENGTH_SHORT).show();
//		try {
//			String albumName = groupList.get(groupPosition).get(ALBUM);
//			List<Audio> album = albuns.get(albumName);
//			List<Audio> listTmp = album.subList(childPosition, album.size());
//			Audio[] executionList = new Audio[listTmp.size()]; 
//			
//			listTmp.toArray(executionList);
//		
//			Bundle args = new Bundle();
////			args.putParcelableArray(AudioPlayerFragment.EXECUTION_LIST, executionList);
//			
//			// TODO Extract this to a method (repeated in MMUnBActivity too)
//			Fragment newFragment = new AudioPlayerFragment();
//			newFragment.setArguments(args);
//			
//			FragmentManager manager = getActivity().getSupportFragmentManager();
//			FragmentTransaction transaction = manager.beginTransaction();
//			
//			if(getActivity().findViewById(R.id.main) != null){
//				transaction.replace(R.id.main, newFragment);
//				transaction.addToBackStack(null);
//			}else{
//				transaction.replace(R.id.content, newFragment);
//				transaction.addToBackStack(null);
//			}
//			transaction.commit();
//			
//			return true;
//		}
//		catch(Throwable e) {
//        	Toast.makeText(getActivity().getApplicationContext(), "Error... could not play the selected audio.", Toast.LENGTH_SHORT).show();
//        	return false;
//		}
//	}

	/**
	 * Creates the grop list out of the albuns map according to structure 
	 * required by the SimpleExpandableListAdapter (I know that this could 
	 * be simple, and I should try to understand the rationale for this design. at first, 
	 * a map could resolve a SimpleExpandableListAdapter). The resulting list contains 
	 * maps of String into String. Each map contains one entry with key "albm" and 
	 * value the name of the album.  
	 * @param albuns
	 * @return
	 */
	private List<Map<String, String>> createGroupList(Map<String, List<Audio>> albuns) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		for (String album : albuns.keySet()) {
			Map<String, String> m = new HashMap<String, String>();
			m.put(ALBUM, album);
			result.add(m);
		}
		return result;
	}
	
	
	private List<List<Map<String, String>>> createChildList(Map<String, List<Audio>> albuns) {
		List<List<Map<String, String>>> result = new ArrayList<List<Map<String,String>>>();
		for(String album : albuns.keySet()) {
			List<Map<String, String>> children = new ArrayList<Map<String,String>>();
			
			for(Audio audio : albuns.get(album)) {
				Map<String, String> musics = new HashMap<String, String>();
				musics.put(TITLE, "getTitle with mediaExtractor");
				children.add(musics);
			}
			result.add(children);
		}
		return result;
	}

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.activity_audio_table, menu);
	}

	/*
	 * Return all production (musics) grouped by album (a map <String, List<Audio>>, where 
	 * the string is the name of the album).
	 */
    private Map<String, List<Audio>> groupByAlbum(List<Audio> production) {
		Map<String, List<Audio>> albuns = new HashMap<String, List<Audio>>(); 
    	for(Audio a : production) {
			List<Audio> musics = new ArrayList<Audio>();
			
//    		if(albuns.containsKey(a.getAlbum())) {
//    			musics = albuns.get(a.getAlbum());
//			}
    		musics.add(a);
    		
//    		albuns.put(a.getAlbum(), musics);
		}
    	return albuns;
	}

}

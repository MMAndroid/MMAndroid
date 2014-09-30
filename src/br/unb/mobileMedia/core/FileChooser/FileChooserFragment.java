package br.unb.mobileMedia.core.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.view.AudioPlayerFragment;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class FileChooserFragment extends ListFragment {
	
	private File sdcard, extSdcard;
	private List<FileDetail> result = new ArrayList<FileDetail>();
	private FileArrayAdapter adapter;
	private String[] extensionsAccepteds;
	private ArrayList<FileDetail> fileSelecteds = new ArrayList<FileDetail>();
	private int playListId;
	

	
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
//        //get from intent the playlist`s id.
//        Bundle extras = getArguments();
//        playListId = extras.getInt(MainPlaylistListFragment.SELECTED_PLAYLIST_ID);
        
        Log.i("FileChooser PlayList", ""+playListId);
        
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		setHasOptionsMenu(true);
		
		getActivity().setTitle("File Chooser Fragment");
	
		extensionsAccepteds = getResources().getStringArray(R.array.extensions_accepteds);

		sdcard = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		extSdcard = new File("/mnt/extSdCard/");

		CreateUI();
		

		return inflater.inflate(R.layout.activity_list_file_chooser, container, false);
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.activity_file_chooser_action_bar, menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		
		case R.id.ExecuteMusicSelected:
			//To do insert animate rotate in this item of action bar
			executePlayListSelected ();
			break;
//	
//		case R.id.AddPlayListActionBar:
//			createPlayList();
//			break;
			
		default:
			Log.i("Message PL", "nao Implementado");
		}
	
		return super.onOptionsItemSelected(item);
	}
	
	
	/**
	 * Envia as musicas adicionada no arrayList File Detail para a
	 * quem chamou o file chooser. Ate o memento é o AudioPlayerFragment
	 */
	private void executePlayListSelected(){
		
//		AudioPlayerFragment.getInstance(getActivity().getApplicationContext()).receiveFileChooser(fileSelecteds);
//		
		Fragment target = getTargetFragment();
		if(target instanceof AudioPlayerFragment) {
			((AudioPlayerFragment) target).receiveFileChooser(fileSelecteds);
		}
		
		getActivity().getSupportFragmentManager().popBackStack();
	}
	
	private void CreateUI() {

		adapter = new FileArrayAdapter(getActivity().getApplicationContext(), R.layout.activity_list_file_chooser, getAllMusic());
		this.setListAdapter(adapter);
		
	}

	
	
	private void listAllDirs(File file) throws IOException{
		
		if(!file.exists()){
			throw new IOException("File "+ file.getAbsolutePath()+ " not exists.");
		}else if(!file.canRead()){
			throw new IOException("Permission denied in: "+ file.getAbsolutePath());
		}else{
			for(File temp: file.listFiles()){
				if(temp.isDirectory()){
					listAllDirs(temp);
				}else{
					for(String extensionAccepted : extensionsAccepteds){
						if(temp.getAbsolutePath().endsWith(extensionAccepted)  && !temp.isHidden()){
							Log.i("", temp.getAbsolutePath());
							result.add(new FileDetail(R.drawable.icon_audio, temp.getName(), "File Size: " + (temp.length() / 1000000) + "Mb", temp.getAbsolutePath()));
						}
					}
				}
			}
		}
	
        return;

	}

	public List<FileDetail> getAllMusic(){
		
		try {
			listAllDirs(sdcard);	
			listAllDirs(extSdcard);
			
		}catch (IOException e) {
			Log.i("IOException",e.getMessage());
		}catch(Exception e){
			e.getStackTrace();
		}
		
		
		
		return result;
		
	}
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		l.setItemChecked(position, true);

		FileDetail o = adapter.getItem(position);
		
		onFileClick(o);	

	}

	// Metodo gatilho para que os itens selecionados na PL seja adicionado em
	// Memoria ou no DB
	private void onFileClick(FileDetail f) {
		Toast.makeText(getActivity(), "Item Adicionado: " + f.getName(), Toast.LENGTH_SHORT).show();

		this.fileSelecteds.add(f);
	}

}

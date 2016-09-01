package br.unb.mobileMedia.core.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.domain.AudioFormats;
import br.unb.mobileMedia.core.view.AudioPlayerFragment;

public class FileChooserFragment extends ListFragment {
	File currentDir;
	private File sdcard, extSdcard;
	private List<FileDetail> result = new ArrayList<FileDetail>();
	private FileArrayAdapter adapter;
	private ArrayList<FileDetail> fileSelecteds = new ArrayList<FileDetail>();
	private int playListId;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		// //get from intent the playlist`s id.
		// Bundle extras = getArguments();
		// playListId =
		// extras.getInt(MainPlaylistListFragment.SELECTED_PLAYLIST_ID);
		//
		// Log.i("FileChooser PlayList", ""+playListId);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		getActivity().setTitle("File Chooser Fragment");

		sdcard = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		extSdcard = new File("/mnt/extSdCard/");

		CreateUI();

		return inflater.inflate(R.layout.activity_list_file_chooser, container,	false);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.activity_file_chooser_action_bar, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.ExecuteMusicSelected:
			// To do insert animate rotate in this item of action bar
			executePlayListSelected();
			break;
		//
		// case R.id.AddPlayListActionBar:
		// createPlayList();
		// break;

		default:
			Log.i("Message PL", "nao Implementado");
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Envia as musicas adicionada no arrayList File Detail para a quem chamou o
	 * file chooser. Ate o memento é o AudioPlayerFragment
	 */
	private void executePlayListSelected() {

		// AudioPlayerFragment.getInstance(getActivity().getApplicationContext()).receiveFileChooser(fileSelecteds);
		//
		Fragment target = getTargetFragment();
		if (target instanceof AudioPlayerFragment) {
//			((AudioPlayerFragment) target).receiveFileChooser(fileSelecteds);
		}

		getActivity().getSupportFragmentManager().popBackStack();
	}

	private void CreateUI() {
		listAllDirs(sdcard);

		this.setListAdapter(adapter);

	}

	private void listAllDirs(File f) {

		File[] dirs = f.listFiles();
		Log.i("----", "f.listFiles() OK");
		List<FileDetail> dir = new ArrayList<FileDetail>();
		List<FileDetail> fls = new ArrayList<FileDetail>();
		
		
		// Listando todos os diretorios com os arquivos contidos
		try {
			for (File ff : dirs) {
				if (ff.isDirectory()) {
					dir.add(new FileDetail(R.drawable.icon_folder,
							ff.getName(), "Folder", ff.getAbsolutePath()));
					// Log.i("### Dir ###: ", ff.getName());
				} else {
					// Loop Para Comparar as extens�es de arquivos v�lidas
					for (AudioFormats extensionAccepted : AudioFormats.values()) {
						if (ff.getName().endsWith(extensionAccepted.getFormatAsString())) {
							fls.add(new FileDetail(R.drawable.icon_audio, ff.getName(), "File Size: "
									+ (ff.length() / 1000000) + "Mb", ff.getAbsolutePath()));
							// Log.i("### FILE ###: ", ff.getName());
						}
					}
				}
			}
		} catch (Exception e) {
			Log.i("EXCEPTION::::", e.getStackTrace().toString());
		}
		Collections.sort(dir);
		Collections.sort(fls);
		// add as files aos respectivos dirs
		dir.addAll(fls);
		if (!f.getName().equalsIgnoreCase("sdcard")) {
			dir.add(0, new FileDetail(R.drawable.icon_back, "..",
					"Parent Directory", f.getParent()));
		}
		// Adicionando os itens na view list e informo qual xml vai ser
		// carregado no fragment
		adapter = new FileArrayAdapter(getActivity().getApplicationContext(),
				R.layout.activity_list_file_chooser, dir);
		this.setListAdapter(adapter);
		

	}

	public List<FileDetail> getAllMusic() {

		try {
			listAllDirs(sdcard);
			// listAllDirs(extSdcard);

		} catch (Exception e) {
			e.getStackTrace();
		}

		return result;

	}

	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		l.setItemChecked(position, true);
		// v.setBackgroundColor("#FF0000");
		FileDetail o = adapter.getItem(position);
		if (o.getData().equalsIgnoreCase("folder")|| o.getData().equalsIgnoreCase("parent directory")) {			currentDir = new File(o.getPath());
			listAllDirs(currentDir);
		} else {
			onFileClick(o);
		}
	}

	// Metodo gatilho para que os itens selecionados na PL seja adicionado em
	// Memoria ou no DB
	private void onFileClick(FileDetail f) {
		Toast.makeText(getActivity(), "Item Adicionado: " + f.getName(),Toast.LENGTH_SHORT).show();

		this.fileSelecteds.add(f);
	}

}

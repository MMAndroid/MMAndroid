package br.unb.mobileMedia.core.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.unb.mobileMedia.R;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class FileChooserFragment extends ListFragment {
	File currentDir;
	FileArrayAdapter adapter;
	String[] extensionsAccepteds;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		getActivity().setTitle("File Chooser Fragment");
		
		//Array que recebe o array contido no xml extensions_accepteds
		extensionsAccepteds = getResources().getStringArray(R.array.extensions_accepteds);
		
		currentDir = Environment.getExternalStorageDirectory();
		
		CreateUI(currentDir);
        
		return inflater.inflate(R.layout.activity_list_file_chooser, container, false);
	}
	
	
	
	private void CreateUI(File f) {
		
		File[] dirs = f.listFiles();
		Log.i("----","f.listFiles() OK");
		
		List<FileDetail> dir = new ArrayList<FileDetail>();
		List<FileDetail> fls = new ArrayList<FileDetail>();
		
		//Listando todos os diretorios com os arquivos contidos
		try {
			for (File ff : dirs) {
				
				if (ff.isDirectory()){
					dir.add(new FileDetail(ff.getName(), "Folder", ff.getAbsolutePath()));
					Log.i("### Dir ###: ", ff.getName());
				}else {
					//Loop Para Comparar as extensões de arquivos válidas 
					for(int i=0; i<extensionsAccepteds.length; i++){
						if(ff.getName().endsWith(extensionsAccepteds[i])){
							fls.add(new FileDetail(ff.getName(), "File Size: "+ (ff.length()/1000000)+"Mb", ff.getAbsolutePath()));
							Log.i("### FILE ###: ", ff.getName());
						}
					}
					
					
				}
			}
		} catch (Exception e) {
			Log.i("EXCEPTION::::", e.getStackTrace().toString());
		}

		// Ordena os dir e files
		Collections.sort(dir);
		Collections.sort(fls);

		// add as files aos respectivos dirs
		dir.addAll(fls);

		if (!f.getName().equalsIgnoreCase("sdcard")) {
			dir.add(0, new FileDetail("..", "Parent Directory", f.getParent()));
		}


		// Adicionando os itens na view list e informo qual xml vai ser carregado no fragment
			adapter = new FileArrayAdapter(getActivity().getApplicationContext(), R.layout.activity_list_file_chooser, dir);
			this.setListAdapter(adapter);
		
	}
	

	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		FileDetail o = adapter.getItem(position);

		if (o.getData().equalsIgnoreCase("folder") || o.getData().equalsIgnoreCase("parent directory")) {
			currentDir = new File(o.getPath());
			CreateUI(currentDir);
		} else {
			onFileClick(o);
		}
		
	}

	
	//Metodo gatilho para que os itens selecionados na PL seja adicionado em Memoria ou no DB
	private void onFileClick(FileDetail o) {
		Toast.makeText(getActivity(), "Item Adicionado: " + o.getPath(),
				Toast.LENGTH_SHORT).show();
		

	}

	
}

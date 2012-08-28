package br.unb.projetopositivo.mm.view.video;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import br.unb.projetopositivo.mm.R;
import br.unb.projetopositivo.mm.util.FileUtility;
import br.unb.projetopositivo.mm.util.MMConstants;

public class VideoListActivity extends ListActivity {


	public static final String FILE_OBJ_PARAMETER = "FILE_OBJECT_PARAMETER";
	private List<File> fileList;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_list_activity);
		List<File> values = new ArrayList<File>();
		ListAdapter adapter = new VideoFileArrayAdapter(getApplicationContext(), values);
		setListAdapter(adapter);


		this.configureUI();
	}

	private void configureUI() {
		fileList = FileUtility.listFiles(new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/"), ".mp4");
		
		for(fileList.)
		((VideoFileArrayAdapter)getListAdapter()).addAll(fileList);
		((VideoFileArrayAdapter)getListAdapter()).notifyDataSetChanged();

		ListView lv = getListView();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				File file = fileList.get(position);
				Intent intent = new Intent(VideoListActivity.this.getApplicationContext(),VideoSelectionActivity.class);
				intent.putExtra(FILE_OBJ_PARAMETER, file);
				startActivity(intent);			
				Log.i(MMConstants.TAG,"Arquivo selecionado: "+file.getPath());
			}
		});

	}


}

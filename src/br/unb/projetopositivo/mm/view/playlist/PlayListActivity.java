package br.unb.projetopositivo.mm.view.playlist;

import java.util.List;

import br.unb.projetopositivo.mm.R;
import br.unb.projetopositivo.mm.R.layout;
import br.unb.projetopositivo.mm.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PlayListActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
       
        ListView listPlayLists = (ListView) findViewById(R.id.list_playlist);
      
        PlayListManager manager = new PlayListManager(this);
        
        manager.open();
        
        manager.addPlayList(new PlayList("Nirvana", "Blah"));
        manager.addPlayList(new PlayList("Sonic youth", "Foo"));
        manager.addPlayList(new PlayList("Bon Jovi", "Argh"));
        
        List<PlayList> playLists = manager.listAll();
        
        String names[] = new String[playLists.size()];
        int i = 0;
        for (PlayList p : playLists) {
			names[i++] = p.getName();
		}
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, names);
        listPlayLists.setAdapter(adapter);	
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_play_list, menu);
        return true;
    }
    
    
}

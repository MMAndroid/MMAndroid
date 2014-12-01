package br.unb.mobileMedia;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.unb.mobileMedia.core.FileChooser.FileDetail;
import br.unb.mobileMedia.core.db.DBConstants;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.db.DBHelper;
import br.unb.mobileMedia.core.db.DefaultAudioListDAO;
import br.unb.mobileMedia.core.db.DefaultAuthorDAO;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.extractor.DefaultAudioExtractor;
import br.unb.mobileMedia.core.manager.Manager;
import br.unb.mobileMedia.core.view.AudioPlayerFragment;
import br.unb.mobileMedia.core.view.AuthorListFragment;
import br.unb.mobileMedia.core.view.ShareListFragment;
import br.unb.mobileMedia.playlist.MainPlaylistListFragment;
import br.unb.mobileMedia.util.ListAllFiles;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MMUnBActivity extends FragmentActivity implements OnItemClickedCallBack{
	
	private AlertDialog alerta;
	
	/*
	 * Alert with question: Do you like Sync yours musics?
	 */
	private void mensagemSincronizarVazio() { 
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle("Você não tem músicas para executar."); 
		
		
		builder.setMessage("Gostaria de sincronizar suas músicas?"); 
		builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() { 
			
			public void onClick(DialogInterface arg0, int arg1) { 
				try {
					sincronizarTudo();
				} catch (DBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
		}); 
		
		builder.setNegativeButton("Não", new DialogInterface.OnClickListener() { 
			
			public void onClick(DialogInterface arg0, int arg1) { 
				//Não quer sincronizar
			}
		}); 
		
		alerta = builder.create(); 
		alerta.show();
		
		
	}
	

	private MenuItem menuItem;
	private ActionBar actionBar;
	private SyncFiles syncFiles;	
	
	@Override
	protected void onCreate(Bundle savedInstanceStace){
		
		super.onCreate(savedInstanceStace);
		setContentView(R.layout.main);
		
		int countAudioInt = 0;
		
		ActionBar actionBar = getActionBar();
		actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setTitle("MMAndroid"); 
		actionBar.setSubtitle("mobile media");

		//Menu com os botoes
		MenuFragment menuFrag = new MenuFragment();
		
		//SyncFile auto
		syncFiles = new SyncFiles();
		syncFiles.execute(new ListAllFiles());
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (findViewById(R.id.main)!=null){
			transaction.add(R.id.main, menuFrag);
		}else{
			transaction.add(R.id.menu, menuFrag);
			transaction.add(R.id.content, new ContentFragment());
		}
		
		
		
		//Possibilidade de sincronizar tudo
		DefaultAudioListDAO countAudio = new DefaultAudioListDAO(getApplicationContext());
		
		try {
			
			countAudioInt = countAudio.countListAudioBanco();
			
			//if bank is void, sync all files
			if(countAudioInt == 0){
				
				//Alert
				mensagemSincronizarVazio();
				
			}
			
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Fim possibilidade de sincronizar tudo
		
		transaction.commit();
	}

	
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.activity_mmunb_action_bar, menu);
	    return true;
	  }
  
	  

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Fragment newFragment = null;
		
		switch(item.getItemId()){
			case R.id.exitActionBar:
				MMUnBActivity.this.finish();
				break;
			
			case R.id.syncFiles:
				menuItem = item;
				menuItem.setActionView(R.layout.sync_files_load);
				menuItem.expandActionView();
				
				//Sync files when is clicked
				new SyncFiles().execute(new ListAllFiles());

				
				break;
				
			case R.id.twiiterAction:
				newFragment = new ShareListFragment();
				break;
				
			case R.id.PlayList:
				newFragment = new MainPlaylistListFragment();
				break;
				
		}
		
		
		if (newFragment !=null){
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			
			if(findViewById(R.id.main) != null){
				transaction.replace(R.id.main, newFragment);
				transaction.addToBackStack(null);
			}else{
				transaction.replace(R.id.content, newFragment);
			}
			transaction.commit();
		}
		
		
	    return super.onOptionsItemSelected(item);
	}


	  
	
	public void onItemClicked(int menuItem){
		Fragment newFragment = null;
		switch(menuItem){
			case R.id.btn_list_authors:
				newFragment = new AuthorListFragment();
				break;
				
			case R.id.btn_open_music_player:
				newFragment = new AudioPlayerFragment(); 
				break;

			case R.id.exitActionBar:
				exit();
				break;
				
			case R.id.btn_synchronize:
				try {
					Manager.instance().synchronizeMedia(getApplicationContext());
					Toast.makeText(getApplicationContext(), R.string.message_synchronization_finished, Toast.LENGTH_LONG).show();
				}catch(DBException e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
				break;
				
			default:
				Toast.makeText(getApplicationContext(), R.string.need_to_be_implemented, Toast.LENGTH_LONG).show();
		}
		// TODO Extract this to a method (repeated in AuthorListFragment too)
		if (newFragment !=null){
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			
			if(findViewById(R.id.main) != null){
				transaction.replace(R.id.main, newFragment);
				transaction.addToBackStack(null);
			}else{
				transaction.replace(R.id.content, newFragment);
			}
			transaction.commit();
		}
		
	}

	
	private void exit(){
		try {
			Manager.instance().synchronizeMedia(getApplicationContext());
			Toast.makeText(getApplicationContext(), R.string.message_synchronization_finished, Toast.LENGTH_LONG).show();
		}
		catch(DBException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		} 
	}


	
	
											//Parametro, Progresso, Resultado
	private class SyncFiles extends AsyncTask<ListAllFiles, Integer, Integer> {
				
		int num_music = -1;
		List<FileDetail> list = new ArrayList<FileDetail>();
		
		public SyncFiles(){}
		
		@Override
        protected Integer doInBackground(ListAllFiles... f){
              try{
                  Thread.sleep(3000);                  
                  
                  list.addAll(f[0].getAllMusic());
        
                  num_music = list.size();
                  
                }catch (Exception e){
                    e.printStackTrace();
                }
            
            return num_music;
        }

		
		@Override
        protected void onPostExecute(Integer result) {
			
			
			if (menuItem != null && menuItem.getActionView() != null){
				menuItem.collapseActionView();
				menuItem.setActionView(null);
				
				actionBar.setSubtitle("Loading complete.");
				
				//TODO: create a List<Audio> to reveive all files and call DAO to persist all Musics in DB
				//Is necessary change a return type in ListAllFiles -> getAllMusic to List<Audio>.
				
            }
			

//			Log.i("Nao musicas Post: ", ""+num_music + " - "+ list.size());

        }
		

		
	  };
	  
	  /*
	   * Get the audio list from ListAllFiles and register in the bank
	   */
	  private void sincronizarTudo() throws DBException{
		  
		    ListAllFiles listaDeMusicas = new ListAllFiles();
		    List<FileDetail> lista;
		    DefaultAudioListDAO includeAudioInBank = new DefaultAudioListDAO(getApplicationContext());
	    	DefaultAudioExtractor extrator = new DefaultAudioExtractor(getApplicationContext());
	    	DefaultAuthorDAO adjustAuthor = new DefaultAuthorDAO(getApplicationContext());
	    	
	    	String[] author;
	    	String album;
	    	Integer fkAuthor;
	    	
		    lista = listaDeMusicas.getAllMusic();
		    	    	
		    for( FileDetail item : lista )  
		    {  
		    	
		    	//Adiciono todas as musicas da pilha
		    	try {
					
		    		author = extrator.nameOfAuthor(item.getPath());
		    		album = extrator.nameOfAlbum(item.getPath());
		    		
		    		Integer audioId = item.getName().hashCode();
		    		if(audioId < 0){
		    			audioId = audioId*(-1);
		    		}
					
		    		
		    		Log.i("ALBUM: ","Nome: "+author[0]+" UnicName: "+author[1]);
		    		
		    		fkAuthor = adjustAuthor.ManageAuthor(author[1], author[0]);
					
		    		includeAudioInBank.adicionaAudio(audioId,item.getName(),item.getPath(),album,fkAuthor);
					
				} catch (DBException e) {
					
					Log.i("Ocorreu um erro de sincronização.", null);
					
					e.printStackTrace();
				}
		    }
		    

	  }
	
	
	
}

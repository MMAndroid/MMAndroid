package br.unb.mobileMedia.playlist;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Playlist;
import br.unb.mobileMedia.core.manager.Manager;

import android.app.DialogFragment;

/**
 * The main activity of the playlist feature.
 
 * @author willian
 * <remarks>
/// | Procedimento: onContextItemSelected(MenuItem item)
/// | Funcionalidade: Add geographical position - adiciona coordenada geografica.
/// 
/// | Criada em: 
/// | Criada por: willian
/// | 
/// | Alterada em: 21/05/2013
/// | Alterada por: Rogério - MPCA/UNB
/// | Motivo da Alteracao: 1º Classe StubGPS retorna coordenadas fixas./2º Criação de método de alerta para refatoração/ 3º Criação de metódos de verificação de ativação do GPS
/// </remarks>
 */

/// <summary>
/// Activity Playlist.
/// </summary>

/// <remarks>
/// | Procedimento: onContextItemSelected(MenuItem item)
/// | Funcionalidade: Add geographical position - adiciona coordenada geografica.
/// 
/// | Criada em: 
/// | Criada por: willian
/// | 
/// | Alterada em: 21/05/2013
/// | Alterada por: Rogério
/// | Motivo da Alteracao: 1º Classe StubGPS retorna coordenadas fixas./ Criação de método de alerta para refatoração
/// </remarks>
/// 
///<param name=''></param>
/// <returns>
/// </returns>

// TODO change the extends from MainPlaylistListActivity from Activity to ListActivity
public class MainPlaylistListActivity extends Activity {
	
	//Rogério
	
	 private Double Latitude;
	 private Double Longitude;
	
	

	//Store the Playlists names to display in the ListView
	private String names[];
	List<Playlist> playlists;	
	
	//String containing the playlist id that will be passed on to another activity through an intent.
	public final static String SELECTED_PLAYLIST_ID = "idPlaylist";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_list);
		configureUI();
	}

	private void configureUI() {
		//ListView to show all playlists in a scrollable view
		ListView listPlayLists = (ListView) findViewById(R.id.list_playlist);

		//Associar a ListView ao ContextMenu
		registerForContextMenu(listPlayLists);
		
		// Add playlist button
		((Button)findViewById(R.id.btn_addPlaylist)).setOnClickListener(new View.OnClickListener(){     
			public void onClick(View v) {                

				//Dialog (Alert) to get the information of the new playlist
				AlertDialog.Builder alert = new AlertDialog.Builder(MainPlaylistListActivity.this);

				alert.setTitle("Add Playlist");
				alert.setMessage("Name:");

				// Set an EditText view to get user input 
				final EditText input = new EditText(MainPlaylistListActivity.this);
				alert.setView(input);
				//Ok button
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

						String value = input.getText().toString();
						try {
							Manager.instance().newPlaylist(getApplicationContext(), new Playlist(value));
						} catch (DBException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


						//refresh the ViewList with recent added playlist
						refreshListPlayLists ();
					}
				});
				//Cancel button
				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

				alert.show();  
			}        

		});

		//Refresh the List View (List of Playlists)
		refreshListPlayLists ();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_play_list, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId()==R.id.list_playlist) {

			menu.setHeaderTitle("Menu:");
			String[] menuItems = getResources().getStringArray(R.array.menu_playlist);
			for (int i = 0; i<menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		//Index number of the selected option from the context menu
		int menuItemIndex = item.getItemId();

		//Retrieve the name of all of the options from the context menu
		/*
      String[] menuItems = getResources().getStringArray(R.array.menu_playlist);
      String menuItemName = menuItems[menuItemIndex];
		 */

		//Retrieve the playlist name that we will be working on...
		final String listItemName = names[info.position];

		
		/*
		 * 
		 * NEEDS REFACTORING!!!
		 * 
		 */


		//Option - EDIT
		if(menuItemIndex == 0 ){
		
			//GET NEW NAME
			
			//Dialog (Alert) to get the information of the new playlist
			AlertDialog.Builder alert = new AlertDialog.Builder(MainPlaylistListActivity.this);

			alert.setTitle("Edit Playlist");
			alert.setMessage("New Name:");

			// Set an EditText view to get user input 
			final EditText input = new EditText(MainPlaylistListActivity.this);
			alert.setView(input);
			//Ok button
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int whichButton) {

					String newName = input.getText().toString();
					
					Playlist editedPlaylist = null;
					
					try {
						editedPlaylist = Manager.instance().getSimplePlaylist(MainPlaylistListActivity.this, listItemName);
						// playlist with new values
						editedPlaylist.setName(newName);
						
						Manager.instance().editPlaylist(MainPlaylistListActivity.this, editedPlaylist);
						
					} catch (DBException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					refreshListPlayLists ();
				}
			});
			//Cancel button
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			alert.show();  
	

		}
		//Option - REMOVE
		if(menuItemIndex == 1){
			try {
				Manager.instance().removePlaylist(this, listItemName);
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			refreshListPlayLists ();
		}
		
		//Add geographical position
		if(menuItemIndex == 2 ){
			
			Playlist playlist = null;
			
			try {
				playlist = Manager.instance().getSimplePlaylist(MainPlaylistListActivity.this, listItemName);
				// playlist with new values
				/*
				 * Método substituído.
				 * Para recuperar a coordenada ele acessava o método location da classe StubGPS.
				 * Esse método retornava valores estáticos para as coordenadas Latitude e Logitude
				 Manager.instance().addPositionPlaylist(this, playlist, location.getLatitude(), location.getLongitude());
				*/
				/*
				 * Agora com a nova implemntação ele chama antes o método CapturaCoordenada(),
				 * que captura as coordenadas do smartphone retornando a Latitude e Longitude.
				 * Além disso foi criado um novo método para retornar uma mensagem de alerta e para verificar se o GPS está ativo ou não!,
				 * informando ao usuário que a coordenada foi adicionada ao PlayList
				 */
				String provider = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
					
					if(!provider.contains("gps"))
						{
							VerificaGPSAtivo();						
					}
					else{
							CapturaCoordenada();
						    Manager.instance().addPositionPlaylist(this, playlist, Latitude ,Longitude );
						    MensagemAlerta("Coordenada Adicionada", "As coordenadas " + Latitude+","  + Latitude + "  foram adicionadas.");
					}
		
			} catch (DBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			refreshListPlayLists ();
		}
		
		return true;
	}

	private void refreshListPlayLists (){

		//Update the List View
		ListView listPlayLists = (ListView) findViewById(R.id.list_playlist);
		playlists = null;
		try {
			playlists = Manager.instance().listSimplePlaylists(this);
			
			// check if there is any playlist
			if (playlists == null || playlists.size() == 0) {
				names = new String[1];
				// TODO Refactor: extract string to xml
				names[0] = "No playlist found.";
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
				listPlayLists.setAdapter(adapter);
			} else {
				names = new String[playlists.size()];
				int i = 0;
				for (Playlist p : playlists) {
					names[i++] = p.getName();
				}
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
						android.R.layout.simple_list_item_1, 
						android.R.id.text1, 
						names);
				listPlayLists.setAdapter(adapter);
				
				//Calls the Playlist Editor when a playlist is pressed.
				listPlayLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		            
		            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		            //if(position == 1) {
		            Intent intent = new Intent(getApplicationContext(), PlayListEditorActivity.class);
		            //Get the selected playlist name!
		            String selectedPlaylistName = (String) parent.getItemAtPosition(position);
		            Playlist recoveredPlaylist = null;		
		            try {
						recoveredPlaylist = Manager.instance().getSimplePlaylist(getApplicationContext(), selectedPlaylistName);
					} catch (DBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            
		            int message = recoveredPlaylist.getId();
		            
		        	intent.putExtra(SELECTED_PLAYLIST_ID, message);
		            
		            startActivity(intent);
		            		//	}	
		            		}
		            });
		    	
				
			}
			
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	public void CapturaCoordenada()
	{
		MMUNB_GPS GPS = new MMUNB_GPS(); 
		LocationManager LM = null; 
			   
			
		LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String bestProvider = null;
		
		if (LM != null)
			bestProvider = LM.getBestProvider(new Criteria(), true);
							
		if (GPS != null)
			
		   //atualiza a cada 0 minutos e 0 metros
			LM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, GPS);

		Location l = null;
		if (bestProvider != null)
			l = LM.getLastKnownLocation(bestProvider);
		if (l != null) {
			
			 Latitude= l.getLatitude();
			 Longitude =l.getLongitude();
		
		} else {
			 Latitude=null;
		     Longitude =null;
		}
			
	}
	
	public void VerificaGPSAtivo()
	{
		String provider = Settings.Secure.getString(getContentResolver(),
		Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
											
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		// Verifica se o GPS está ativo
		boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// Caso não esteja ativo abre um novo diálogo com as configurações para.
		// realizar se ativamento0.
		if (!enabled) {
			CaixaDialogo();
         }

	}
	
	public void MensagemAlerta(String TituloAlerta,String MensagemAlerta )
	{
		   AlertDialog.Builder Mensagem = new AlertDialog.Builder(MainPlaylistListActivity.this);
		   Mensagem.setTitle(TituloAlerta);
		   Mensagem.setMessage(MensagemAlerta);
		   Mensagem.setNeutralButton("OK",null); 
		   Mensagem.show();
		  
				   
	}
	
	public void CaixaDialogo()
	{
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);     
	        builder.setMessage("GPS desativado! Deseja ativá-lo?")            
	        .setCancelable(false)        
	        .setIcon(android.R.drawable.ic_dialog_alert) // ícone de alerta
	        .setTitle("Atenção:") //título do caixa de diálogo
	                 
	        //Evento disparado se clicar no botão Sim
	        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {               
	            public void onClick(DialogInterface dialog, int id) {                     
	            	  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                      startActivity(intent);  
	            }            
	        })            
	   
	        //Event disparado se clicar no botão Não
	        .setNegativeButton("Não", new DialogInterface.OnClickListener() {                
	            public void onClick(DialogInterface dialog, int id) {                     
	                dialog.cancel(); //Cancela a caixa de diálogo e volta a tela anterior
	            }            
	        });     
	        AlertDialog alert = builder.create();     
	        alert.show(); //Chama a caixa de diálogo
	}

}

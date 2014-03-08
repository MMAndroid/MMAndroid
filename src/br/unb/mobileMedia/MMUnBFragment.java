package br.unb.mobileMedia;

import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.manager.Manager;
import br.unb.mobileMedia.core.view.AudioPlayerActivity;
import br.unb.mobileMedia.core.view.AuthorListActivity;
import br.unb.mobileMedia.core.view.ShareListActivity;
import br.unb.mobileMedia.playlist.MainPlaylistListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MMUnBFragment extends Fragment {

    /** Called when the activity is first created. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.main, container, false);
    }
    
    // onCreate -> onCreateView
    /*
     * Configure the UI action listeners. One action listener is 
     * defined to each button.
     */
    private void configureUI(){
        //configures the btn_list_authors action listener 
        ((Button)findViewById(R.id.btn_list_authors)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent startActivtyIntent = new Intent(getApplicationContext(), AuthorListActivity.class);
                startActivity(startActivtyIntent);
                }
            });
    
        ((Button)findViewById(R.id.btn_media_list)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent startActivtyIntent = new Intent(getApplicationContext(), AudioPlayerActivity.class);
                startActivity(startActivtyIntent);
                }
            });
    
        ((Button)findViewById(R.id.btn_play_list)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent startActivtyIntent = new Intent(getApplicationContext(), MainPlaylistListActivity.class);
                startActivity(startActivtyIntent);
                }
            });
     
        ((Button)findViewById(R.id.btn_synchronize)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    Manager.instance().synchronizeMedia(getApplicationContext());
                    Toast.makeText(getApplicationContext(), R.string.message_synchronization_finished, Toast.LENGTH_LONG).show();
                }
                catch(DBException e) {
                    Toast.makeText(getApplicationContext(),  e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        /**
         * 
         * 
         * 
         * VELOCITY - SOCIAL NETWORK
         * INICIO
         * 
         */
    
        //social network button
        ((Button)findViewById(R.id.btn_share)).setOnClickListener(new OnClickListener() {
            public  void onClick(View v) {
                Intent startActivtyIntent = new Intent(getApplicationContext(), ShareListActivity.class);
                startActivity(startActivtyIntent);
            }
        });
    
        /**
         * 
         * 
         * 
         * VELOCITY - SOCIAL NETWORK
         * FIM
         * 
         */
        
        ((Button)findViewById(R.id.btn_exit)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    protected Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    private Button findViewById(int btn) {
        getActivity().findViewById(btn);
        return null;
    }
}
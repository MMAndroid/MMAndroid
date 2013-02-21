package br.unb.mobileMedia.core.view;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Author;
import br.unb.mobileMedia.core.manager.Manager;

/**
 * The activity that displays the list of authors.
 * @author rbonifacio
 */
public class AuthorListActivity extends ListActivity {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("All artists");
		try {
			List<Author> authors = Manager.instance().listAuthors(getApplicationContext());

			if (authors == null || authors.size() == 0) {
				String[] values = new String[] { "No author found." };
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
				setListAdapter(adapter);
			} else {
				AuthorArrayAdapter adapter = new AuthorArrayAdapter(this,
						authors);
				setListAdapter(adapter);
			}
		} catch (DBException e) {
			Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}

	/*
	 * This method is called whenever the user selects 
	 * one of the items of a list. In this case, whenever 
	 * the user selects on artist. 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Author item = (Author) getListAdapter().getItem(position);
		Intent intent = new Intent(getApplicationContext(), AudioExpandableListActivity.class);
		intent.putExtra(AudioExpandableListActivity.SELECTED_ARTIST_ID, item.getId()); //this makes available the primary key of the author
		intent.putExtra(AudioExpandableListActivity.SELECTED_ARTIST_NAME, item.getName());
		startActivity(intent);	
	}
}

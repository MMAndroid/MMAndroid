package br.unb.mobileMedia.util;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import br.unb.mobileMedia.R;

public class MultiChoiceMode implements AbsListView.MultiChoiceModeListener {
	private OnChooseMoreListener loadMoreListener;
	private int menuInflate;
	AbsListView usingView;

	public MultiChoiceMode(OnChooseMoreListener l, AbsListView view,
			int menuInflate) {
		this.menuInflate = menuInflate;
		loadMoreListener = l;
		usingView = view;
	}

	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		mode.getMenuInflater().inflate(this.menuInflate, menu);
		return true;
	}

	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.removeMedia:
			this.loadMoreListener.removeItems();
			mode.finish();
			return true;
		case R.id.saveMedia:
			this.loadMoreListener.saveItems();
			mode.finish();
		default:
			return false;
		}
	}

	public void onDestroyActionMode(ActionMode mode) {
	}

	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked) {
		// Capture total checked items
		final int checkedCount = usingView.getCheckedItemCount();
		// Set the CAB title according to total checked items
		mode.setTitle(checkedCount + " Selected");
		// Calls toggleSelection method from ListViewAdapter Class
		this.loadMoreListener.onChooseMore(position);
		
		
		// Calls toggleSelection method from ListViewAdapter Class
//		listviewadapter.toggleSelection(position);
	}

	public interface OnChooseMoreListener {
		public void onChooseMore(int position);

		public void removeItems();

		public void saveItems();
	}
}
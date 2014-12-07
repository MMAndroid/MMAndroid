package br.unb.mobileMedia.util;

import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class EndlessScrollListener implements OnScrollListener {
	private int visibleThreshold = 5;
	private int currentPage = 0;
	private int previousTotalItemCount = 0;
	private int startingPageIndex = 0;
	private boolean loading = true;
	

	public EndlessScrollListener() {
		
	}
	
	public EndlessScrollListener(int visibleThreshold, int startPage){
		this.visibleThreshold = visibleThreshold;
		this.startingPageIndex = startPage;
		this.currentPage = startPage;
	}



	public void onScroll(AbsListView absListView, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		if(totalItemCount < previousTotalItemCount){
			this.currentPage = this.startingPageIndex;
			this.previousTotalItemCount = totalItemCount;
			if(totalItemCount == 0){
				this.loading = true;
			}
		}
		
		if(loading && (totalItemCount > previousTotalItemCount)){
			loading = false;
			Log.e("loading", ""+loading);
			previousTotalItemCount = totalItemCount;
			currentPage++;
		}
		
		if(!loading && ( (firstVisibleItem + visibleItemCount) >= totalItemCount)){
			onLoadMore(currentPage+1, totalItemCount);
			Log.e("totalItemsCount", ""+totalItemCount);
			loading = true;	
		}
		
	}
	
	
	public void onScrollStateChanged(AbsListView absListView, int i) {	}

	public abstract void onLoadMore(int page, int totalItemsCount);

}
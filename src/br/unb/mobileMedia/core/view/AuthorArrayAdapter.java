package br.unb.mobileMedia.core.view;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.domain.Album;
import br.unb.mobileMedia.core.domain.Author;
import br.unb.mobileMedia.core.manager.Manager;
import br.unb.mobileMedia.util.LoadImageCache;

public class AuthorArrayAdapter extends ArrayAdapter<Author> {

	private Context context;
	private LayoutInflater mInflater;
    private LoadImageCache cache;

	
	public AuthorArrayAdapter(Context context, List<Author> authors) {
		super(context, R.layout.author_row);
		this.context = context;
		
		this.mInflater = LayoutInflater.from(context);
        this.cache = LoadImageCache.Instance(context);
		
		Iterator<Author> iterator = authors.iterator();
		while (iterator.hasNext()) {
			this.add(iterator.next());
		}
	}
	
	
	
	public List<Album> getAlbumsAuthor(int position){
		return Manager.instance().listAlbumsByAuthor(context, getItem(position).getId());
	}
 
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder v;
		
		if(convertView == null){
			
			convertView = mInflater.inflate(R.layout.author_row, parent, false);
			v = new ViewHolder();
			v.authorArt = (ImageView) convertView.findViewById(R.id.AuthorArt);
			v.nameAuthor = (TextView) convertView.findViewById(R.id.NameAuthor);
			v.numAlbumAuthor = (TextView) convertView.findViewById(R.id.numAlbumAuthor);
		
			convertView.setTag(v);
		}else{
        	v = (ViewHolder) convertView.getTag();
        }
		
		Author item = getItem(position);
		
		v.nameAuthor.setText(item.getName());

		List<Album>AlbumAuthor = getAlbumsAuthor(position);
		
		if(AlbumAuthor.size() == 0){
			v.numAlbumAuthor.setText("Albums: "+ 0	+".");
			this.cache.loadBitmap(
       			 bitmapToBytes(this.decodeSampledBitmapFromResource(context.getResources(),
               			 R.drawable.img_author, 100, 100)), v.authorArt, R.drawable.img_author);
		}else{
			v.numAlbumAuthor.setText("Albums: "+ AlbumAuthor.size()	+".");
			this.cache.loadBitmap(AlbumAuthor.get(0).getImage(), v.authorArt, R.drawable.img_author);
		}
		

		
		return convertView;
	}
	
	private static class ViewHolder{
		ImageView authorArt;
		TextView nameAuthor;
		TextView numAlbumAuthor;
	}
	

	    
	    
	    private byte[] bitmapToBytes(Bitmap bm) {
	    	synchronized(this){
				if (bm != null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
					byte[] bytes = baos.toByteArray();
					return bytes;
				} else {
					return null;
				}
	    	}
		}
	    
	  //to decode R.drawable in Bitmap
	    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
	    	synchronized(this){
		        // First decode with inJustDecodeBounds=true to check dimensions
		        final BitmapFactory.Options options = new BitmapFactory.Options();
		        options.inJustDecodeBounds = true;
		       
		        BitmapFactory.decodeResource(res, resId, options);
		
		        // Calculate inSampleSize
		        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		
		        // Decode bitmap with inSampleSize set
		        options.inJustDecodeBounds = false;
		
		        return BitmapFactory.decodeResource(res, resId, options);
	    	}
	    }
	    
	    
	    //To calulate size of image
	    private  int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	       synchronized(this){
		        final int height = options.outHeight;
		        final int width  = options.outWidth;
		       
		        int inSampleSize = 1;
		       
		        if(height > reqHeight || width > reqWidth){
		            final int halfHeight = height / 2;
		            final int halfWidth = width / 2;
		
		         // Calculate the largest inSampleSize value that is a power of 2 and keeps both
		            // height and width larger than the requested height and width.
		            while ((halfHeight / inSampleSize) > reqHeight
		                    && (halfWidth / inSampleSize) > reqWidth) {
		                inSampleSize *= 2;
		            }
		
		        }
		       
		        return inSampleSize;
	    	}
	   
	    }
}

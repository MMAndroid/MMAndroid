package br.unb.mobileMedia.core.view;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.domain.Album;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.manager.Manager;

public class AlbumArrayAdapter extends ArrayAdapter<Album> {

    private Context context;
    private LayoutInflater mInflater;
    private LruCache<String, Bitmap> mMemoryCache;
    
    public AlbumArrayAdapter(Context context, List<Album> albums) {
        super(context, R.layout.author_row);
        this.context = context;
       
        this.mInflater = LayoutInflater.from(context);
        
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

       
        //Add all album in this arrayAdapter
        Iterator<Album> iterator = albums.iterator();
        while (iterator.hasNext()) {
            this.add(iterator.next());
        }
        
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       
        ViewHolder v;
       
        if(convertView == null){
           
            convertView = mInflater.inflate(R.layout.album_row, parent, false);
            v = new ViewHolder();
            v.imageArt = (ImageView) convertView.findViewById(R.id.albumArt);
            v.nameAlbum = (TextView) convertView.findViewById(R.id.nameAlbum);
            v.numAudioInAlbum = (TextView) convertView.findViewById(R.id.numAudioInAlbum);
       
            convertView.setTag(v);
        }else{
            v = (ViewHolder) convertView.getTag();
        }
      
        Album item = getItem(position);

        loadBitmap(item.getImage(), v.imageArt);
        
        v.nameAlbum.setText(item.getName());
        
        List<Audio> audiosAuthor = Manager.instance().getAudioByAlbum(context, item.getId());
        
        if(audiosAuthor == null)
        	v.numAudioInAlbum.setText("Audios: "+  0 + ".");
        else
        	v.numAudioInAlbum.setText("Audios: "+  audiosAuthor.size()+".");
        
        return convertView;
    }
   
    private static class ViewHolder{
        ImageView imageArt;
        TextView nameAlbum;
        TextView numAudioInAlbum;
    }
    
    
    
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
    
  
    private void loadBitmap(byte[] byteImage, ImageView imageView){
    	
    	String imageKey = byteImage.toString(); 
	    	
    	if(cancelPotentialWork(byteImage, imageView)){
    		Log.i("cancelPotentialWork","True");
    		final BitmapWorkerTask task = new BitmapWorkerTask(imageView);

    		final AsyncDrawable asyncDrawable = 
    				new AsyncDrawable(context.getResources(), decodeSampledBitmapFromResource(context.getResources(), R.drawable.loading, 100, 100), task);
    		
    		imageView.setImageDrawable(asyncDrawable);
    		task.execute(byteImage);
    	}
    	
    }
   
  
   
    //To calulate size of image
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
       
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
   
   //To decode Byte[] to Bitmap
    public static Bitmap decodeSampledBitmapFromByte(byte[] data, int reqWidth, int reqHeight){
    	
    	 // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
       
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(data, 0, data.length, options);    	
    }
    
    
    
    //to decode R.drawable in Bitmap
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

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
    
    
    
    
    //class thread to load bitmap
    private class BitmapWorkerTask extends AsyncTask<byte[], Void, Bitmap>{
    	
    	private final WeakReference<ImageView> imageViewReference;
    	private byte[] data = null;
    	
    	public BitmapWorkerTask(ImageView imageView){
    		//Use a WeakReference to ensure the ImageView ca be garbage collected
    		imageViewReference = new WeakReference<ImageView>(imageView);
    	}
    	
    	@Override
    	protected Bitmap doInBackground(byte[]... params){
    		data = params[0];
    		
    		if(data != null)
    			return decodeSampledBitmapFromByte(data, 100, 100);     		
    		else
    			return decodeSampledBitmapFromResource(context.getResources(), R.drawable.adele, 100, 100);
    	}
 
    	
    	@Override
    	protected void onPostExecute(Bitmap bitmap){
    		if(imageViewReference != null && bitmap != null){
    			final ImageView imageView = imageViewReference.get();
    			
    			final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
    			
    			if(this == bitmapWorkerTask && imageView != null){
    				imageView.setImageBitmap(bitmap);
    			}

    		}
    	}
    	
    }
    
    
    
    
    /**
     * Subclasse responsável por armazenar a referência retornada pelo Task.
     * Neste caso um BitmapDrawable é usado para exibir uma Image (uma imagem de loading) equanto o trabalho eh concluido
     * @author thiago
     *
     */
    static class AsyncDrawable extends BitmapDrawable{
    	private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;
    	
    	public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask){
    		super(res, bitmap);
    		bitmapWorkerTaskReference =
    	            new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
    	}
    	
    	
    	public BitmapWorkerTask getBitmapWorkerTask(){
    		return bitmapWorkerTaskReference.get();
    	}
    }
    
    
    
    /*
     * Methodo verifica se existe outro task rodando associado a uma ImageView. Se existir este cancela seu trabalho
     */
    public static boolean cancelPotentialWork(byte[] data, ImageView imageView){
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if(bitmapWorkerTask != null){
        	final byte[] bitmapData = bitmapWorkerTask.data;
        	
            //if bitmapData is not yet set or it differs from the new data
        	if(bitmapData.length == 0 || bitmapData != data){
        		//Cancel previous task
        		bitmapWorkerTask.cancel(true);
        	}else{
        		//The same work is already in progress
        		return false;
        	}
        }
        
        
        return true;
    }
    
    
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView){
    	if(imageView != null){
    		final Drawable drawable = imageView.getDrawable();
    		if(drawable instanceof AsyncDrawable){
    			final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
    			return asyncDrawable.getBitmapWorkerTask();
    		}
    	}
    	
    	return null;
    }
    
    
    
    
   
}

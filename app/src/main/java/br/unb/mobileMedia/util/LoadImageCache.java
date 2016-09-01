package br.unb.mobileMedia.util;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;
import br.unb.mobileMedia.R;

public class LoadImageCache {

	/* singleton instance of the ImageCache class */
	private static LoadImageCache instance;
	
	private Context context;
    private LruCache<String, Bitmap> mMemoryCache;
    private int imageToNull;
    
    public static final LoadImageCache Instance(Context context){
    	if(instance == null){
    		instance = new LoadImageCache(context);
    	}
    	
    	return instance;
    }
    
    
    
	private LoadImageCache(Context c){
		
		this.context = c;
		
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/16th of the available memory for this memory cache.
        final int cacheSize = maxMemory/16;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
		
	}
	
	
	
	
    public void loadBitmap(byte[] byteImage, ImageView imageView, int imageToNull){
    	
    	String imageKey = String.valueOf(byteImage);
    	
    	this.imageToNull = imageToNull;
    	
    	
    	final Bitmap bitmap = getBitmapFromMemCache(imageKey);
    	
    	if (bitmap != null) {
    		Log.i("bitmap",  "em cache");
    		imageView.setImageBitmap(bitmap);
        } else {
        	Log.i("bitmap", "Nao esta em cache");
        	
        	if(cancelPotentialWork(byteImage, imageView)){
        		Log.i("cancelPotentialWork","True");
        		
        		final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        		
            	if(byteImage == null ){
            		byteImage = this.bitmapToBytes(BitmapFactory.decodeResource(context.getResources(), imageToNull));
            	}
            	
            	final AsyncDrawable asyncDrawable = 
        				new AsyncDrawable(context.getResources(), decodeSampledBitmapFromResource(context.getResources(), R.drawable.loading, 100, 100), task);
        		imageView.setImageDrawable(asyncDrawable);
        		
        		task.execute(byteImage);
        	}
        	
        }
    	
    	
    	
    }
	
	
	
	
	private void addBitmapToMemoryCache(String key, Bitmap bitmap){
		synchronized (this) {
	        try{
	        	
	        	if (getBitmapFromMemCache(key) == null) {
	        		mMemoryCache.put(key, bitmap);
	        	}
	        	
	        }catch(NullPointerException e){
	        	Log.e("NullPointerException", e.getMessage());
	    		bitmap = decodeSampledBitmapFromResource(context.getResources(), this.imageToNull, 100, 100);    	
	        	mMemoryCache.put(key, bitmap);
	        }
		}
    }

	
	
    private Bitmap getBitmapFromMemCache(String key) {
    	synchronized(this){
    		return mMemoryCache.get(key);
    	}
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
   
   //To decode Byte[] to Bitmap
    private Bitmap decodeSampledBitmapFromByte(byte[] data, int reqWidth, int reqHeight){
    	synchronized(this){
	    	 // First decode with inJustDecodeBounds=true to check dimensions
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	       
	        // Calculate inSampleSize
	        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	
	        // Decode bitmap with inSampleSize set
	        options.inJustDecodeBounds = false;
	
	        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
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

    		final String key =  String.valueOf(data);
    		final Bitmap bitmap = decodeSampledBitmapFromByte(data, 100, 100);
			addBitmapToMemoryCache(key, bitmap);        		
    		return bitmap;
    		
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
    		bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
    	}
    	
    	public BitmapWorkerTask getBitmapWorkerTask(){
    		return bitmapWorkerTaskReference.get();
    	}
    }
    
    
    
    /*
     * Methodo verifica se existe outro task rodando associado a uma ImageView. Se existir este cancela seu trabalho
     */
    private static boolean cancelPotentialWork(byte[] data, ImageView imageView){
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

package br.unb.mobileMedia.core.extractor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;

/**
 * A defaul implementation of an audio extractor.
 * 
 * @author rbonifacio
 */
@SuppressLint("InlinedApi")
public class DefaultAudioExtractor implements MediaExtractor {
	
	private static final String UNKNOWN = "Unknown";

//	private File mp3File;
//	private MediaScannerConnection msc;
	private Context context;
	private MediaMetadataRetriever mmr;
	
	/**
	 * Constructs a DefaultAudioExtractor with the specified context. The
	 * context is necessary because it provides a useful way to obtain a manager
	 * query.
	 * 
	 * @param context
	 *            the current context of the application.
	 */
	public DefaultAudioExtractor(Context context) {
		this.context = context;
		this.mmr = new MediaMetadataRetriever();
	}
	
	
	
	public Bitmap getAlbumArt(String url){
		
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), 
    		    R.drawable.adele);
		
		try{
		
			mmr.setDataSource(url);
						
			if(mmr.getEmbeddedPicture() != null){
				BitmapFactory.decodeByteArray(mmr.getEmbeddedPicture(), 0, mmr.getEmbeddedPicture().length);
				Log.i("artByte", "nao null");
		        InputStream is = new ByteArrayInputStream(mmr.getEmbeddedPicture());
		        bm = BitmapFactory.decodeStream(is);
			}
			
		}catch(RuntimeException e){
//			Log.i("Exception", "Art image");
		}
		
		return bm;
	}
	
	
	public String getBitRate(String url){
		
		String bitRate = "0";
		
		try{
			mmr.setDataSource(url);
			bitRate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
		}catch(Exception e){
			e.getStackTrace();
		}
		
		return bitRate;
		
	}
	
	/**
	 * @see MediaExtractor#processFile(File[])
	 */
	public List<Author> processFiles(List<File> audioFiles) {
		
		
		List<Author> result = new ArrayList<Author>();
		
		for(File file: audioFiles){

	      try{
	       mmr.setDataSource(context, Uri.parse(Uri.encode(file.getAbsolutePath()).toString()));
	      }catch(RuntimeException e){
	    	  //Exibir alert para informar sobre acentuação no arquivo
		       Log.i("RuntimeExeption: ", file.getAbsolutePath());
		       Log.i("", e.getMessage());
	      }
	      
	       String authorName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST); 
	       authorName = authorName == null || authorName.equals("") ? UNKNOWN : authorName;
	       
//	       Integer authorId = authorName.hashCode();
	       
	       String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
	       title = title == null || title.equals("") ? UNKNOWN : title;
	       
	       String titleKey = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
	       titleKey = titleKey == null || titleKey.equals("") ? UNKNOWN : titleKey;
	       
	       String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM); //cursor.
	       album = album == null || album.equals("") ? UNKNOWN : album;
	      
//	       Log.i("authorId: ", String.valueOf(authorId));
//		   Log.i("authorName: ", authorName);
//		   Log.i("title: ", title);
//		   Log.i("titleKey: ", titleKey);
//		   Log.i("album: ", album);


	       result.add(new Author(null, authorName));
		   
		}
		
		
		
		return result;
	}

	
	
	public List<Audio> processAudio(List<Author> authors, List<File> files) {
		
		List<Audio> audios = new ArrayList<Audio>();
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();

		
		for(Author author: authors){

			for(File file : files){
				try{
					mmr.setDataSource(context, Uri.parse(Uri.encode(file.getAbsolutePath())));
				}catch(RuntimeException e){
					e.getStackTrace();
					//Exibir alert para informar sobre acentuação no arquivo
//					Log.i("RuntimeExeption f: ", file.getAbsolutePath());
				}	
				
				
		       String authorName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST); 
		       authorName = authorName == null || authorName.equals("") ? UNKNOWN : authorName;
		       
//			   Integer authorId = authorName.hashCode();
		       
		       String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		       title = title == null || title.equals("") ? UNKNOWN : title;
		       
		       String titleKey = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		       titleKey = titleKey == null || titleKey.equals("") ? UNKNOWN : titleKey;
		       
		       String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM); //cursor.
		       album = album == null || album.equals("") ? UNKNOWN : album;
		       
		       if(author.getName().equals(authorName)){
//		    	   Long id, String title, String url, long albumId
		    	   audios.add(new Audio(null, titleKey, file.getAbsolutePath(), album, author.getId()));
		       }
		      
			}

		}
			
		return audios;
	}

	

	
//	private MediaScannerConnection createMediaScanner(final List<File> audioFiles) {
//		return new MediaScannerConnection(context,
//				new MediaScannerConnection.MediaScannerConnectionClient() {
//					public void onScanCompleted(String path, Uri uri) {
//						msc.disconnect();
//					}
//
//					public void onMediaScannerConnected() {
//						for (final File file : audioFiles) {
//							mp3File = file;
//							msc.scanFile(mp3File.getAbsolutePath(), null);
//
//						}
//					}
//				});
//	}
}

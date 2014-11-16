package br.unb.mobileMedia.core.extractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import br.unb.mobileMedia.Exception.ExceptionMediaExtractor;
import br.unb.mobileMedia.core.domain.Album;
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

	// private File mp3File;
	// private MediaScannerConnection msc;
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

	
	public void setMMR(String url) throws ExceptionMediaExtractor{
		try{
			
			mmr.setDataSource(context, Uri.parse(url));

		}catch(IllegalArgumentException e) {

			throw new ExceptionMediaExtractor(e.getMessage());
		}
		
	}
	
	public byte[] getAlbumArt(){

		try {
			
			if (mmr.getEmbeddedPicture() == null) {
				throw new ExceptionMediaExtractor(
						mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
						+ " Hasn't getEmbeddedPicture!");
			}

			return 	mmr.getEmbeddedPicture();
			
		} catch (ExceptionMediaExtractor e) {
			
			return null;
//			return BitmapFactory.decodeResource(context.getResources(), R.drawable.adele);
			
		}

		
	}

	public String getBitRate() {

		String bitRate = "0";

		try {
			return bitRate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
			
		} catch (Exception e) {
			e.getStackTrace();
		}

		return bitRate;

	}

	/**
	 * @see MediaExtractor#processFile(File[])
	 */
	public List<Author> processFiles(List<File> audioFiles) {

		List<Author> result = new ArrayList<Author>();

		for (File file : audioFiles) {

			try {
				mmr.setDataSource(context, Uri.parse(Uri.encode(
						file.getAbsolutePath()).toString()));
			} catch (RuntimeException e) {
				// Exibir alert para informar sobre acentuação no arquivo
				Log.i("RuntimeExeption: ", file.getAbsolutePath());
				Log.i("", e.getMessage());
			}

			String authorName = getAuthor();
			authorName = authorName == null || authorName.equals("") ? UNKNOWN
					: authorName;

			// Integer authorId = authorName.hashCode();

			String title = mmr
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			title = title == null || title.equals("") ? UNKNOWN : title;

			String titleKey = mmr
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			titleKey = titleKey == null || titleKey.equals("") ? UNKNOWN
					: titleKey;

			String album = mmr
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM); // cursor.
			album = album == null || album.equals("") ? UNKNOWN : album;

			// Log.i("authorId: ", String.valueOf(authorId));
			// Log.i("authorName: ", authorName);
			// Log.i("title: ", title);
			// Log.i("titleKey: ", titleKey);
			// Log.i("album: ", album);

			result.add(new Author(null, authorName));

		}

		return result;
	}
	
	
	public List<String> extractAllAuthors(List<File> audioFiles){
		List<String> result = new ArrayList<String>();
		
		
		for(File audio : audioFiles){
			
			try {
				mmr.setDataSource(context, Uri.parse(Uri.encode(audio.getAbsolutePath().toString())));
			} catch (RuntimeException e) {
				// Exibir alert para informar sobre acentuação no arquivo
				Log.i("RuntimeExeption: ", audio.getAbsolutePath().toString());
				Log.i("", e.getMessage());
			}
			
			String author = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
			
			if(!result.contains(author)){
				result.add(author);
			}
		}
		
		return result;
	}

	
	public List<Album> extractAllAlbum(List<File> audioFiles, List<Author> authors){
		
		List<Album> result = new ArrayList<Album>();
		
		for(File audio : audioFiles){
			
			try {
				mmr.setDataSource(context, Uri.parse(Uri.encode(audio.getAbsolutePath().toString())));
			} catch (RuntimeException e) {
				// Exibir alert para informar sobre acentuação no arquivo
				Log.i("RuntimeExeption: ", audio.getAbsolutePath().toString());
				Log.i("", e.getMessage());
			}
			
			String nameAlbum = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
			String nameAuthor = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
			
			//create album and add NameAlbum
			Album album = new Album(null);
			album.setName(nameAlbum);
			
			for(Author author : authors){
				if(author.getName().equals(nameAuthor)){
					album.setAutorId(author.getId());
					break;
				}
			}
	
			
			if(!result.contains(album)){
				result.add(album);
			}
		}
		
		return result;
	}
	
	
	
	public List<Audio> processAudio(List<File> files, List<Album> albums) {

		List<Audio> result = new ArrayList<Audio>();

		for (File file : files) {
			try {
				mmr.setDataSource(context, Uri.parse(Uri.encode(file.getAbsolutePath())));
			} catch (RuntimeException e) {
				e.getStackTrace();
				// Exibir alert para informar sobre acentuação no arquivo
				// Log.i("RuntimeExeption f: ", file.getAbsolutePath());
			}

			
			String nameAlbum = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
			String title     = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			
			
			Audio audio = new Audio(null);
			audio.setTitle(title);
			audio.setUrl(file.getAbsolutePath());
		
			for(Album album : albums){
				if(album.getName().equals(nameAlbum)){
					audio.setAlbumId(album.getId());
					break;
				}
			}
			
			if(!result.contains(audio)){
				result.add(audio);
			}
			

		}

		return result;
	}
	
	
	
	

	public String getAuthor() {
	
		String Author = null;
		
		try {

			Author =  mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

		} catch (IllegalArgumentException e) {
		
		 e.getStackTrace();
		
		}
		
		return Author;
		
	}
		

	public String getAlbum() {
		String Album = null;
		
		try {
		
			Album =  mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

		} catch (IllegalArgumentException e) {
		
		 e.getStackTrace();
		
		}
		
		return Album;
	}

	public String getGenre() {
		String Genre = null;
		
		try {

			Genre =  mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

		} catch (IllegalArgumentException e) {
		
		 e.getStackTrace();
		
		}
		
		return Genre;
	}

	// private MediaScannerConnection createMediaScanner(final List<File>
	// audioFiles) {
	// return new MediaScannerConnection(context,
	// new MediaScannerConnection.MediaScannerConnectionClient() {
	// public void onScanCompleted(String path, Uri uri) {
	// msc.disconnect();
	// }
	//
	// public void onMediaScannerConnected() {
	// for (final File file : audioFiles) {
	// mp3File = file;
	// msc.scanFile(mp3File.getAbsolutePath(), null);
	//
	// }
	// }
	// });
	// }
}

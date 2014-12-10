package br.unb.mobileMedia.core.extractor;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.util.Log;
import br.unb.mobileMedia.core.domain.Album;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;

/**
 * A default implementation of an video extractor.
 * 
 * @author WillianJunior
 */
public class DefaultVideoExtractor implements MediaExtractor {
	
	private static final String UNKNOWN = "Unknown";

	private static final String PROJECTION[] = {
		android.provider.MediaStore.Video.Media._ID,
		android.provider.MediaStore.Video.Media.TITLE};
//		android.provider.MediaStore.Video.Media.ALBUM};
//		android.provider.MediaStore.Video.Media.ARTIST};
	
	private File mp4File;
	private MediaScannerConnection msc;
	private Context context;
	private MediaMetadataRetriever mmr;

	/**
	 * Constructs a DefaultVideoExtractor with the specified context. The
	 * context is necessary because it provides a useful way to obtain a manager
	 * query.
	 * 
	 * @param context
	 *            the current context of the application.
	 */
	public DefaultVideoExtractor(Context context) {
		this.context = context;
		this.mmr = new MediaMetadataRetriever();
	}

	/**
	 * @see MediaExtractor#processFile(File[])
	 */
	public List<Author> processFiles(List<File> videoFiles) {
		Map<Integer, Author> authors = new HashMap<Integer, Author>();

		

		Log.i("Video files to process: ", String.valueOf(videoFiles.size()));
		for(File file: videoFiles){

			URI u =  file.getAbsoluteFile().toURI();

			Log.i("URI: ", u.getPath());

			mmr.setDataSource(u.getPath());
			
			String authorName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
			authorName = authorName == null || authorName.equals("") ? UNKNOWN : authorName;

			Integer authorId = authorName.hashCode();

			String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			title = title == null || title.equals("") ? UNKNOWN : title;

			String titleKey = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			titleKey = titleKey == null || titleKey.equals("") ? UNKNOWN : titleKey;

			String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
			album = album == null || album.equals("") ? UNKNOWN : album;

			Log.i("authorId: ", String.valueOf(authorId));
			Log.i("authorName: ", authorName);
			Log.i("title: ", title);
			Log.i("titleKey: ", titleKey);
			Log.i("album: ", album);

			Author author = authors.get(authorId);

			if(author == null) {
//				author = new  Author(authorId.hashCode(), authorName);
			}
			Integer id = titleKey.hashCode();
//			author.addProduction(new VideoOld(id, id, album, u));

//			authors.put(author.getId(), author);
		}
		return null;
//		return new ArrayList<Author>(authors.values());
	}

	public List<Audio> processAudio(List<File> files, List<Album> albums) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getAlbumArt(String mediaPath) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBitRate(String mediaPath) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAuthor(String mediaPath) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAlbum(String mediaPath) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getGenre(String mediaPath) {
		// TODO Auto-generated method stub
		return null;
	}



	public List<Author> extractAllAuthors(List<File> audiosFiles) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Album> extractAllAlbum(List<File> audioFiles, List<Author> authors) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle(String mediaPath) {
		// TODO Auto-generated method stub
		return null;
	}

}

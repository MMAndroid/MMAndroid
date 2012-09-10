package br.unb.mobileMedia.core.extractor;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;

/**
 * A defaul implementation of an audio extractor.
 * 
 * @author rbonifacio
 */
public class DefaultAudioExtractor implements MediaExtractor {
	
	private static final String UNKNOWN = "Unknown";

	private static final String PROJECTION[] = {
		android.provider.MediaStore.Audio.Media.ARTIST_ID,
		android.provider.MediaStore.Audio.Media.ARTIST, 
		android.provider.MediaStore.Audio.Media.TITLE,
		android.provider.MediaStore.Audio.Media.TITLE_KEY,
		android.provider.MediaStore.Audio.Media.ALBUM};
	
	private File mp3File;
	private MediaScannerConnection msc;
	private Context context;

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
	}

	/**
	 * @see MediaExtractor#processFile(File[])
	 */
	public List<Author> processFiles(List<File> audioFiles) {
		Map<Integer, Author> authors = new HashMap<Integer, Author>();

		msc = createMediaScanner(audioFiles);
		
		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; 
		
		Cursor cursor = context.getContentResolver().query(uri, PROJECTION, null, null, null);
		
		
		int i = 0;
		
		if(cursor.getCount() > 0 && cursor.moveToFirst()) {
			do{
				Integer authorId = cursor.getInt(0);
				String authorName = cursor.getString(1);
				String title = cursor.getString(2);
				String titleKey = cursor.getString(3);
				String album = cursor.getString(4) == null || cursor.getString(4).equals("") ? UNKNOWN :  cursor.getString(4);
				
				Author author = authors.get(authorId);
				
				if(author == null) {
					author = new  Author(authorId.hashCode(), authorName);
				}
				
				URI u =  audioFiles.get(i++).getAbsoluteFile().toURI();
				
				Log.i("URI: ", u.toASCIIString());
				
				author.addProduction(new Audio(titleKey.hashCode(), title, u, album));
				
				authors.put(author.getId(), author);
			} while(cursor.moveToNext());
		}
		return new ArrayList<Author>(authors.values());
	}

	private MediaScannerConnection createMediaScanner(final List<File> audioFiles) {
		return new MediaScannerConnection(context,
				new MediaScannerConnection.MediaScannerConnectionClient() {
					public void onScanCompleted(String path, Uri uri) {
						msc.disconnect();
					}

					public void onMediaScannerConnected() {
						for (final File file : audioFiles) {
							mp3File = file;
							msc.scanFile(mp3File.getAbsolutePath(), null);

						}
					}
				});
	}
}

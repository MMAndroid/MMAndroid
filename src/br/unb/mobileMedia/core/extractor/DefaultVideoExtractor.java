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
import br.unb.mobileMedia.core.domain.Video;

/**
 * A default implementation of an video extractor.
 * 
 * @author WillianJunior
 */
public class DefaultVideoExtractor implements MediaExtractor {
	
//	private static final String UNKNOWN = "Unknown";

	private static final String PROJECTION[] = {
		android.provider.MediaStore.Video.Media._ID,
		android.provider.MediaStore.Video.Media.TITLE};
//		android.provider.MediaStore.Video.Media.ALBUM};
//		android.provider.MediaStore.Video.Media.ARTIST};
	
	private File mp4File;
	private MediaScannerConnection msc;
	private Context context;

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
	}

	/**
	 * @see MediaExtractor#processFile(File[])
	 */
	public List<Video> processFiles(List<File> videoFiles) {
		List<Video> videos = new ArrayList<Video>();

		msc = createMediaScanner(videoFiles);
		
		Uri uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI; 
		
		Cursor cursor = context.getContentResolver().query(uri, PROJECTION, null, null, null);
		
		int i = 0;
		
		if(cursor.getCount() > 0 && cursor.moveToFirst()) {
			do{
				Integer _id = cursor.getInt(0);
				String title = cursor.getString(1);
//				String album = cursor.getString(2) == null || cursor.getString(2).equals("") ? UNKNOWN :  cursor.getString(2);
//				String author = cursor.getString(3) == null || cursor.getString(3).equals("") ? UNKNOWN :  cursor.getString(3);
				
				URI u =  videoFiles.get(i++).getAbsoluteFile().toURI();
				
				Log.i("URI: ", u.toASCIIString());
				
				videos.add(new Video(_id, title, u));
			} while(cursor.moveToNext());
		}
		return videos;
	}

	private MediaScannerConnection createMediaScanner(final List<File> videoFiles) {
		return new MediaScannerConnection(context,
				new MediaScannerConnection.MediaScannerConnectionClient() {
					public void onScanCompleted(String path, Uri uri) {
						msc.disconnect();
					}

					public void onMediaScannerConnected() {
						for (final File file : videoFiles) {
							mp4File = file;
							msc.scanFile(mp4File.getAbsolutePath(), null);

						}
					}
				});
	}
}

package br.unb.mobileMedia.core.extractor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
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
//				Log.i("RuntimeExeption: ", file.getAbsolutePath());
				Log.i("", e.getMessage());
			}

			String authorName = getAuthor(file.getAbsolutePath());
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

			result.add(new Author(null, authorName));

		}

		return result;
	}

	public List<Author> extractAllAuthors(List<File> audioFiles) {
		List<Author> result = new ArrayList<Author>();

		String nameAuthor = null;
		for (File audio : audioFiles) {

			try {
				mmr.setDataSource(context, Uri.parse(Uri.encode(audio.getAbsolutePath().toString())));
			} catch (Exception e) {
				Log.i("RuntimeExeption: ", audio.getAbsolutePath().toString());
			}
			
			nameAuthor = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
			nameAuthor = nameAuthor == null || nameAuthor.equals("") ? UNKNOWN : nameAuthor;
			
			Author author = new Author(null, nameAuthor);

			if (!result.contains(author)) {
				result.add(author);
			}
		}

		return result;
	}

	public List<Album> extractAllAlbum(List<File> audioFiles,
			List<Author> authors) {

		List<Album> result = new ArrayList<Album>();
		String nameAlbum = null;
		String nameAuthor = null;
		byte[] imageAlbum = null;

		for (File audio : audioFiles) {

			try {
				mmr.setDataSource(context, Uri.parse(Uri.encode(audio.getAbsolutePath().toString())));
			} catch (Exception e) {
				Log.i("Exception: ", audio.getAbsolutePath().toString());
			}
			
			
			nameAlbum = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
			nameAlbum = nameAlbum == null || nameAlbum.equals("")? UNKNOWN : nameAlbum;
			nameAuthor = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
			nameAuthor = nameAuthor == null || nameAuthor.equals("") ? UNKNOWN : nameAuthor;
			imageAlbum = mmr.getEmbeddedPicture();
			imageAlbum = imageAlbum == null || imageAlbum.length == 0 ? null : imageAlbum;
			
			
			Album album = new Album();
			album.setName(nameAlbum);
			album.setImage(imageAlbum);

			for (Author author : authors) {
				if (author.getName().equals(nameAuthor)) {
					album.setAuthorId(author.getId());
					break;
				}
			}

			if (!result.contains(album)) {
				result.add(album);
			}
			
		}
		
		return result;
	}

	private byte[] drawableToBytes(Drawable drawable) {
		if (drawable != null) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			byte[] bytes = bitmapToBytes(bitmap);
			
			return bytes;
		} else {
			return null;
		}

	}

	public static byte[] bitmapToBytes(Bitmap bm) {
		if (bm != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} else {
			return null;
		}
	}

	public List<Audio> processAudio(List<File> files, List<Album> albums) {

		List<Audio> result = new ArrayList<Audio>();
		String nameAlbum = null;
		String title = null;
		
		for (File file : files) {
			try {
				mmr.setDataSource(context, Uri.parse(Uri.encode(file.getAbsolutePath())));
				
				nameAlbum = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
				nameAlbum = nameAlbum == null || nameAlbum.equals("")? UNKNOWN : nameAlbum;
				
				title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
				title = title == null || title.equals("") ? UNKNOWN : title;
				
			} catch (RuntimeException e) {
				e.getStackTrace();
				nameAlbum  = "Exception";
			}

			
			Audio audio = new Audio(null);
			audio.setUrl(file.getAbsolutePath());

			for (Album album : albums) {
				if (album.getName().equals(nameAlbum)) {
					audio.setAlbumId(album.getId());
					break;
				}
			}

			if (!result.contains(audio)) {
				result.add(audio);
			}

		}

		return result;
	}

	public String getAuthor(String mediaPath) {
		String nameAuthor = null;
		try {
			this.mmr.setDataSource(context, Uri.parse(Uri.encode(mediaPath)));
		} catch (Exception e) {
			Log.e("AudioExtractor-getAuthor", e.getMessage());
		}
		
		nameAuthor = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
		nameAuthor = nameAuthor == null || nameAuthor.equals("") ? UNKNOWN : nameAuthor;

		return nameAuthor;
	}

	public String getAlbum(String mediaPath) {
		String nameAlbum = null;

		try {
			this.mmr.setDataSource(context, Uri.parse(Uri.encode(mediaPath)));
		} catch (Exception e) {
			Log.e("AudioExtractor-getAlbum", e.getLocalizedMessage() +" - " +e.getCause());
		}

		nameAlbum = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
		nameAlbum = nameAlbum == null || nameAlbum.equals("")? UNKNOWN : nameAlbum;
		
		return nameAlbum;
	}

	
	public String getTitle(String mediaPath) {
		String titleMedia = null;

		try {
			this.mmr.setDataSource(context, Uri.parse(Uri.encode(mediaPath)));
		} catch (Exception e) {
			Log.e("AudioExtractor-getTitle", e.getLocalizedMessage() +" - " +e.getCause());
		}

		titleMedia = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		titleMedia = titleMedia == null || titleMedia.equals("")? UNKNOWN : titleMedia;
		
		return titleMedia;
	}

	
	
	public String getGenre(String mediaPath) {
		String genreMedia = null;

		try {
			this.mmr.setDataSource(context, Uri.parse(Uri.encode(mediaPath)));
		} catch (Exception e) {
			Log.e("AudioExtractor-getGenre", e.getLocalizedMessage() +" - " +e.getCause());
		}
		
		genreMedia = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
		genreMedia = genreMedia == null || genreMedia.equals("") ? UNKNOWN : genreMedia;
		return genreMedia;
	}

	
	public String getBitRate(String mediaPath) {

		String bitRate = null;

		try {
			this.mmr.setDataSource(context, Uri.parse(Uri.encode(mediaPath)));
		} catch (Exception e) {
			Log.e("AudioExtractor-getBitRate", e.getLocalizedMessage() +" - " +e.getCause());
		}

		bitRate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
		bitRate = bitRate == null || bitRate.equals("") ? "0" : bitRate;
		
		return bitRate;

	}
	
	
	public byte[] getAlbumArt(String mediaPath) {
		try {
			
			this.mmr.setDataSource(context, Uri.parse(Uri.encode(mediaPath)));
			
			return this.mmr.getEmbeddedPicture();
			
		} catch (Exception e) {
			Log.e("AudioExtractor-getAlbumArt", e.getLocalizedMessage() +" - " +e.getCause());
		}
		
		return null;
	}

	


}

package br.unb.mobileMedia.core.extractor;

import java.io.File;
import java.util.List;

import android.graphics.Bitmap;
import br.unb.mobileMedia.Exception.ExceptionMediaExtractor;
import br.unb.mobileMedia.core.domain.Album;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.domain.Author;

/**
 * An interface that abstracts over the 
 * machanism of extracting audio data from a 
 * list of audio files.
 * 
 * @author rbonifacio
 */
public interface MediaExtractor {
	
	/**
	 * Obtain a list of audio data from a list of files.
	 * 
	 * @param audioFiles input files used to obtain audio data
	 * @return a list of audio data obtained from <i>audioFiles</i>.
	 */
	
	
	public void setMMR(String url) throws ExceptionMediaExtractor;
	
	public List<Author> processFiles(List<File> audioFiles);
	
	public List<String> extractAllAuthors(List<File> audiosFiles);
	
	public List<Album> extractAllAlbum(List<File> audioFiles, List<Author> authors);
	
	public List<Audio> processAudio(List<File> files, List<Album> albums);
	
	public String getAuthor();
	
	public String getAlbum();
	
	public String getGenre();

	public byte[] getAlbumArt();
	
	public String getBitRate();

}

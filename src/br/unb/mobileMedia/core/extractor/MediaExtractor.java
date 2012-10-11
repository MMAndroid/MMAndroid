package br.unb.mobileMedia.core.extractor;

import java.io.File;
import java.util.List;

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
	public List<Author> processFiles(List<File> audioFiles) throws ExtractorException; 

}

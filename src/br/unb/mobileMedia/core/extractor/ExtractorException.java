package br.unb.mobileMedia.core.extractor;

/**
 * An exception for reporting problems that might occur 
 * during the process of extracting metadata information 
 * from a multimedia content. 
 * 
 * @author rbonifacio
 */
public class ExtractorException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ExtractorException() {
		super();
	}
	
	public ExtractorException(String message) {
		super(message);
	}
	
	public ExtractorException(Throwable t) {
		super(t);
	}

}

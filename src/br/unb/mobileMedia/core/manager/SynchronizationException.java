package br.unb.mobileMedia.core.manager;

/**
 * Exception that reports to the view components a problem 
 * that might occur during the synchronization phase.
 * 
 * @author rbonifacio
 */
public class SynchronizationException extends Exception {

	public SynchronizationException() {
		super();
	}

	public SynchronizationException(String detailMessage) {
		super(detailMessage);
	}

	public SynchronizationException(Throwable throwable) {
		super(throwable);
	}
}

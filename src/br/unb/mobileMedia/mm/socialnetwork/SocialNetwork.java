package br.unb.mobileMedia.mm.socialnetwork;


/**
 * An interface that allows MMUnB to send 
 * information to SocialNetworks. 
 * 
 * @author rbonifacio
 */
public interface SocialNetwork {

	/**
	 * Make available the top artists of the last 
	 * week.
	 */
	public void publishTopArtistisOfTheWeek();
	
}

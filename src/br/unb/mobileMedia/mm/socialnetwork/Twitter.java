package br.unb.mobileMedia.mm.socialnetwork;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import android.content.Context;
import br.unb.mobileMedia.core.db.DBException;
import br.unb.mobileMedia.core.domain.Audio;
import br.unb.mobileMedia.core.manager.Manager;

/**
 * An integration to the Twitter social network
 * 
 * @author rbonifacio
 */
public class Twitter implements SocialNetwork {
	
	private static final int NUMBER_OF_DAYS_OF_WEEK = 7;
	
	private Context context;
	
	public Twitter(Context context) {
		this.context = context;
	}
	
	/*@
	 * @see br.unb.mobileMedia.mm.socialnetwork.SocialNetwork#publishTopArtistisOfTheWeek()
	 */
	public void publishTopArtistisOfTheWeek() {
		Date today = Calendar.getInstance().getTime();
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(today);
		calendar.add(Calendar.DATE, -NUMBER_OF_DAYS_OF_WEEK);
		
		Date lastWeek = calendar.getTime();
	
	    try {
	    	Map<Audio, List<Date>> executionHistory = Manager.instance().recently(context, lastWeek);
	    }
	    catch(DBException e) {
	    	e.printStackTrace();
	    }
		
		
		
		
//		SocialAuthAdapter adapter = new SocialAuthAdapter(new DialogListener() {
//			public void onError(SocialAuthError arg0) {
//			}
//			
//			public void onComplete(Bundle arg0) {
//			}
//			
//			public void onCancel() {
//			}
//		});
//		// Add providers
//		adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
	}

	
}

package br.unb.mobileMedia.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import br.unb.mobileMedia.MMAndroid;
import br.unb.mobileMedia.R;
import br.unb.mobileMedia.activities.ActivityNowPlaying;
import br.unb.mobileMedia.core.domain.Audio;

/**
 * Stick a message on the system with the current music playing.
 */
public class NotificationMusic extends NotificationSimple {

	Context context = null;								//Reference to the context that notified.
	Service service = null;								//Reference to the service we're attached to.
	Notification.Builder notificationBuilder = null;	//Used to create and update the same notification.
	RemoteViews notificationView = null;				//Custom appearance of the notification, also updated.
	NotificationManager notificationManager = null;		//Used to broadcast the notification.

	/**
	 * Sends a system notification with music information.
	 *
	 * If the user clicks the notification, will be redirected
	 * to the Now Playing Activity.
	 */
	public void notifySong(Context context, Service service, Audio song) {
		if (this.context == null) {
			this.context = context;
		}
		if (this.service == null) {
			this.service = service;
		}

		Intent notifyIntent = new Intent(context, ActivityNowPlaying.class);					//Intent that launches the Now Playing Activity
		notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		notificationView = new RemoteViews(MMAndroid.packageName, R.layout.notification);		// Setting custom appearance for notification

		//Setting the buttons and texts
		notificationView.setImageViewResource(R.id.notification_button_play, R.drawable.pause);
		notificationView.setImageViewResource(R.id.notification_button_skip, R.drawable.skip);
		notificationView.setTextViewText(R.id.notification_text_title, song.getTitle());
		notificationView.setTextViewText(R.id.notification_text_artist, song.getAuthor());

		Intent buttonPlayIntent = new Intent(context, NotificationPlayButtonHandler.class);		//Building the play button
		buttonPlayIntent.putExtra("action", "togglePause");

		PendingIntent buttonPlayPendingIntent = PendingIntent.getBroadcast(context, 0, buttonPlayIntent, 0);
		notificationView.setOnClickPendingIntent(R.id.notification_button_play, buttonPlayPendingIntent);

		Intent buttonSkipIntent = new Intent(context, NotificationSkipButtonHandler.class);		//Building the Skip button
		buttonSkipIntent.putExtra("action", "skip");

		PendingIntent buttonSkipPendingIntent = PendingIntent.getBroadcast(context, 0, buttonSkipIntent, 0);
		notificationView.setOnClickPendingIntent(R.id.notification_button_skip, buttonSkipPendingIntent);

		notificationBuilder = new Notification.Builder(context);								//Creating the Notification

		notificationBuilder.setContentIntent(pendingIntent)
		                   .setSmallIcon(R.drawable.notification_icon)
		                   .setTicker("MMAndroid: Playing '" + song.getTitle() + "' from '" + song.getAuthor() + "'")
		                   .setOngoing(true)
		                   .setContentTitle(song.getTitle())
		                   .setContentText(song.getAuthor())
		                   .setContent(notificationView);

		Notification notification = notificationBuilder.build();

		notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		service.startForeground(NOTIFICATION_ID, notification);
	}

	/**
	 * Called when user clicks the play/pause button
	 */
	public static class NotificationPlayButtonHandler extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			MMAndroid.musicService.togglePlayback();
		}
	}

	/**
	 * Called when user clicks the skip button
	 */
	public static class NotificationSkipButtonHandler extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			MMAndroid.musicService.next(true);
			MMAndroid.musicService.playSong();
		}
	}

	/**
	 * Updates the Notification icon if the music is paused.
	 */
	public void notifyPaused(boolean isPaused) {
		if ((notificationView == null) || (notificationBuilder == null)){
			return;
		}

		int iconID = ((isPaused) ? R.drawable.play : R.drawable.pause);

		notificationView.setImageViewResource(R.id.notification_button_play, iconID);
		notificationBuilder.setContent(notificationView);
		service.startForeground(NOTIFICATION_ID, notificationBuilder.build());
	}

	/**
	 * Cancels this notification.
	 */
	public void cancel() {
		service.stopForeground(true);
		notificationManager.cancel(NOTIFICATION_ID);
	}

	/**
	 * Cancels all sent notifications.
	 */
	public static void cancelAll(Context c) {
		NotificationManager manager = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancelAll();
	}
	
}

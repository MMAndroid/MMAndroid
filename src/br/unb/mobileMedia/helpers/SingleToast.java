package br.unb.mobileMedia.helpers;

import android.content.Context;
import android.widget.Toast;

/**
 * A single Toast (text message) is displayed on the screen
 *
 * When calling several Toasts, they wait other Toasts to finish.
 * This class, you get an immediate Toast right away.
 *
 * @author daniel--santos
 */
public class SingleToast {

	private static Toast singleToast = null;

	/**
	 * Immediately shows a text message.
	 *
	 * @note This Toast calls show() immediately.
	 */
	public static void show(Context context, String text, int duration) {
		if (singleToast != null) {
			singleToast.cancel();	//override the current Toast
		}
		singleToast = Toast.makeText(context, text, duration);
		singleToast.show();
	}
}

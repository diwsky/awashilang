package com.diwangkoro.awashilang.gcm;

import com.diwangkoro.awashilang.apiutils.UrlApi;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class CommonUtilities {

	// give your server registration url here
//	static final String SERVER_URL = Url._GCM;
	// development
//	public static final String SENDER_ID = "215290871081";
	// production

	public static final String SENDER_ID = "898773096193";
	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "AndroidHive GCM";

	public static final String DISPLAY_MESSAGE_ACTION = "com.RajaPremi.RajaPremi.DISPLAY_MESSAGE";

	public static final String EXTRA_MESSAGE = "message";
	static final String EXTRA_MESSAGE_COUNT = "count";

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	public static void displayMessage(Context context, String message) {
		Log.e("msg notif", ""+message);

		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE_COUNT, 1);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}

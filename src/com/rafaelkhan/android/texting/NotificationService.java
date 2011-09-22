package com.rafaelkhan.android.texting;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

public class NotificationService extends Service {

	private String number;
	private String message;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		Bundle b = intent.getExtras();
		this.number = b.getString("number");
		Log.e("herp", this.number);
		this.message = b.getString("message");
		this.createNotification();
		return START_STICKY;
	}

	/*
	 * Takes a number, and gets the contact name associated with it, if it
	 * doesn't exist, return the number.
	 */
	private String getContactName(String number) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(number));
		Cursor c = this.getContentResolver().query(uri,
				new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
				null, null, null);
		if (c.moveToNext()) {
			String name = c.getString(c
					.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME));
			return name;
		} else {
			return number;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private void createNotification() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.icon;
		CharSequence tickerText = this.getContactName(this.number) + ": "
				+ this.message;
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		CharSequence contentTitle;
		String contentText = "";

		contentTitle = "SMS from: " + this.getContactName(this.number);
		contentText = this.message;
		
		Intent ni = new Intent();
		ni.setClass(this, Launcher.class);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, ni, 0);

		notification.setLatestEventInfo(this, contentTitle, contentText,
				contentIntent);

		notification.flags = Notification.FLAG_AUTO_CANCEL;

		mNotificationManager.notify(1, notification);
	}
}
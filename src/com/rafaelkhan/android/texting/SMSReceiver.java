package com.rafaelkhan.android.texting;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		if (bundle != null) {
			Object[] pdus = (Object[]) bundle.get("pdus");
			SmsMessage[] msgs = new SmsMessage[pdus.length];

			String[] smsStrings = new String[pdus.length];
			String sender = "derp";
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				sender = msgs[i].getOriginatingAddress();
				smsStrings[i] = msgs[i].getMessageBody();
			}

			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
			String time = sdf.format(cal.getTime());
			
			Launcher.dbhdr.onSMSReceive(sender, 
					smsStrings, 
					time);

			Bundle b = new Bundle();
			b.putString("phone number", sender);
			b.putString("message", smsStrings[0]);
			// Intent i = new Intent(context, NotificationService.class);
			// i.putExtras(b);
			// context.startService(i);
		}
	}
}
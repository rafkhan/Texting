package com.rafaelkhan.android.texting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ThreadActivity extends Activity {

	/*
	 * TODO: Make listview start at the bottom
	 */

	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	private String number; // contacts number
	private Boolean knownNumber; // is this an existing thread or not
	private String contactName = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thread_layout);

		this.getBundleInfo();
		if (this.knownNumber) {
			this.updateListView();
			this.setTitleWithContact();
		} else {
			this.toggleNumberBarVisibility(true);
		}
	}

	/*
	 * Gets data from the bundle, if its non existant, display number input bar
	 */
	private void getBundleInfo() {
		Bundle b = this.getIntent().getExtras();
		if (b != null) {
			this.number = b.getString("number");
			this.knownNumber = true;
		} else {
			this.knownNumber = false;
		}
	}

	/*
	 * Refreshes the contents of the listview, and gets the contact name
	 */
	private void updateListView() {
		Toast.makeText(this, "fgsfds", 0).show();
		ArrayList<HashMap<String, String>> al = Launcher.dbhdr
				.getMessageThread(this.number);

		this.contactName = null;
		// get contact name
		for (int i = 0; i < al.size(); i++) {
			HashMap<String, String> temp = al.get(i);
			String number = temp.get("number");
			if (!number.equals("Me")) {
				if (this.contactName == null) { // check only once
					this.contactName = this.getContactName(number);

				}
				temp.put("number", this.contactName); // set contact name to
														// array list
			}
			al.set(i, temp);
		}

		ListView lv = (ListView) findViewById(R.id.sms_thread_list);
		SimpleAdapter adapter = new SimpleAdapter(this, al,
				R.layout.sms_list_item, new String[] { "number", "lastmsg",
						"lasttime" }, new int[] { R.id.sender_tv,
						R.id.message_tv, R.id.time_tv });
		lv.setAdapter(adapter);
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

	/*
	 * It's in the name...
	 */
	private void toggleNumberBarVisibility(boolean toggle) {
		EditText et = (EditText) findViewById(R.id.phone_number_input);
		if (toggle) {
			et.setVisibility(View.VISIBLE);
		} else {
			et.setVisibility(View.GONE);
		}
	}

	public void setTitleWithContact() {
		setTitle("Texting. - " + this.contactName);
	}

	/*
	 * Big-ass method, I'll document it tomorrow.
	 */
	public void onSendButton(View v) {
		if (this.knownNumber) {
			EditText et = (EditText) findViewById(R.id.sms_input);
			String message = et.getText().toString();
			if (message.equals("")) {
				Toast.makeText(this, "You have to input a message.", 0).show();
			} else {
				this.sendSMS(this.number, message);
				Launcher.dbhdr.onSMSSend(this.number, message, this.getTime());
				this.updateListView();
				this.clearTextInput();
			}
		} else {
			EditText phoneNumberInput = (EditText) findViewById(R.id.phone_number_input);
			String tempNum = phoneNumberInput.getText().toString();
			if (tempNum.equals("")) {
				Toast.makeText(this, "You have to input a phone number.", 0)
						.show();
			} else {
				this.knownNumber = true;
				this.number = tempNum;
				EditText et = (EditText) findViewById(R.id.sms_input);
				String message = et.getText().toString();
				if (message.equals("")) {
					Toast.makeText(this, "You have to input a message.", 0)
							.show();
				} else {
					Launcher.dbhdr.onSMSSend(this.number, message,
							this.getTime());
					this.sendSMS(this.number, message);
					this.updateListView();
					this.clearTextInput();
					this.toggleNumberBarVisibility(false);
				}
			}
		}
	}

	private void clearTextInput() {
		EditText et = (EditText) findViewById(R.id.sms_input);
		et.setText("");
	}

	private String getTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		String time = sdf.format(cal.getTime());
		return time;
	}

	// I found this method on the interwebs
	// http://mobiforge.com/developing/story/sms-messaging-android
	private void sendSMS(String phoneNumber, String message) {
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
				SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);

		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS sent",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "Generic failure",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "No service",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "Null PDU",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "Radio off",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SENT));

		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS delivered",
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(DELIVERED));

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
		this.updateDefaultSMSApp(phoneNumber, message);
	}

	/*
	 * Adds message content to the default android app
	 */
	private void updateDefaultSMSApp(String phoneNumber, String message) {
		ContentValues values = new ContentValues();
		values.put("address", phoneNumber);
		values.put("body", message);
		// Note: This uses an Android internal API to save to Sent-folder
		this.getContentResolver().insert(Uri.parse("content://sms/sent"),
				values);
	}
}
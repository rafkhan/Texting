package com.rafaelkhan.android.texting;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Launcher extends Activity {

	public static String LOG_TAG = "Texting: ";
	public static DatabaseHandler dbhdr;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Launcher.dbhdr = new DatabaseHandler(this);
		this.updateListView();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStop() {
		this.updateListView();
		super.onStop();
	}
	
	@Override
	public void onPause() {
		this.updateListView();
		super.onPause();
	}

	@Override
	public void onResume() {
		this.updateListView();
		super.onResume();
	}

	/*
	 * Refreshes the contents of the listview
	 */
	private void updateListView() {
		ArrayList<HashMap<String, Object>> al = Launcher.dbhdr.getAllThreads();
		for (int i = 0; i < al.size(); i++) {
			HashMap<String, Object> temp = al.get(i);
			String number = (String) temp.get("number");
			if (!number.equals("Me")) {
				if (temp.get("lasttime") != null) {
					String contact = this.getContactName(number);
					contact += " (" + Launcher.dbhdr.unreadCount(number) + ")";
					temp.put("name", contact);
				} else {
					temp.put("name", "New message");
				}
			}
			al.set(i, temp);
		}

		ListView lv = (ListView) findViewById(R.id.all_thread_listview);
		SimpleAdapter adapter = new SimpleAdapter(this, al,
				R.layout.all_thread_listitem, new String[] { "name", "lastmsg",
						"lasttime" }, new int[] { R.id.contact_textview,
						R.id.lastmsg_textview, R.id.lasttime_textview });

		lv.setAdapter(adapter);
		this.setClickListener();
	}

	/*
	 * Adds onItemClickListener to listview
	 */
	private void setClickListener() {
		ListView lv = (ListView) findViewById(R.id.all_thread_listview);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, Object> hash = (HashMap<String, Object>) parent
						.getItemAtPosition(position);
				Launcher.this.clickHandler(hash);
			}
		});
	}

	/*
	 * This is what's called when an item is clicked
	 */
	protected void clickHandler(HashMap<String, Object> hash) {
		if (hash.get("lasttime") == null) {
			Intent i = new Intent();
			i.setClass(this, ThreadActivity.class);
			startActivity(i);
		} else {
			// get data from hashmap
			String number = (String) hash.get("number");
			String lastmsg = (String) hash.get("lastmsg");
			String lasttime = (String) hash.get("lasttime");
			// add data to bundle
			Bundle b = new Bundle();
			b.putString("number", number);
			b.putString("lastmsg", lastmsg);
			b.putString("lasttime", lasttime);
			// start intent
			Intent i = new Intent();
			i.setClass(this, ThreadActivity.class);
			i.putExtras(b);
			startActivity(i);
		}
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
}
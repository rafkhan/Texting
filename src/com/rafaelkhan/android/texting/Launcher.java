package com.rafaelkhan.android.texting;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

	/*
	 * Refreshes the contents of the listview
	 */
	private void updateListView() {
		ListView lv = (ListView) findViewById(R.id.all_thread_listview);

		SimpleAdapter adapter = new SimpleAdapter(this,
				Launcher.dbhdr.getAllThreads(), R.layout.all_thread_listitem,
				new String[] { "number", "lastmsg", "lasttime" }, new int[] {
						R.id.contact_textview, R.id.lastmsg_textview,
						R.id.lasttime_textview });

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
		if(hash.get("lasttime") == null) {
			Intent i = new Intent();
			i.setClass(this, ThreadActivity.class);
			startActivity(i);
		} else {
			//open thread
		}
	}
}
package com.rafaelkhan.android.texting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ThreadActivity extends Activity {

	public String number;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thread_layout);

		this.getBundleInfo();
		this.updateListView();
	}

	/*
	 * Refreshes the contents of the listview
	 */
	private void updateListView() {
		ListView lv = (ListView) findViewById(R.id.sms_thread_list);
		SimpleAdapter adapter = new SimpleAdapter(this,
				Launcher.dbhdr.getMessageThread(this.number),
				R.layout.sms_list_item, new String[] { "number", "lastmsg",
						"lasttime" }, new int[] { R.id.sender_tv,
						R.id.message_tv, R.id.time_tv });
		lv.setAdapter(adapter);
	}

	/*
	 * Gets data from the bundle, if its non existant, display number input bar
	 */
	private void getBundleInfo() {
		Bundle b = this.getIntent().getExtras();
		if (b != null) {
			this.number = b.getString("number");
		} else {
			this.toggleNumberBarVisibility(true);
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
}
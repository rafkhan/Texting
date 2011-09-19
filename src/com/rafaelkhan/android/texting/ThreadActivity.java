package com.rafaelkhan.android.texting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ThreadActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thread_layout);

		this.getBundleInfo();
	}

	/*
	 * Gets data from the bundle, if its non existant, display number input bar
	 */
	private void getBundleInfo() {
		Bundle b = this.getIntent().getExtras();
		if (b != null) {
			// There is data
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
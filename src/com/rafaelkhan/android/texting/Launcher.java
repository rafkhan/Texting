package com.rafaelkhan.android.texting;

import android.app.Activity;
import android.os.Bundle;

public class Launcher extends Activity {
	
	public static String LOG_TAG = "Texting: ";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    
    
}
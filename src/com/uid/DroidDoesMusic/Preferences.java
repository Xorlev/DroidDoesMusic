package com.uid.DroidDoesMusic;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class Preferences extends PreferenceActivity {
	protected static final String TAG = "DroidDoesMusic";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Preferences: onCreate");
		
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);
	}
}

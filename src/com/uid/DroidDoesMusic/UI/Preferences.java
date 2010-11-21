package com.uid.DroidDoesMusic.UI;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.uid.DroidDoesMusic.R;

public class Preferences extends PreferenceActivity {
	protected static final String TAG = "DroidDoesMusic";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Preferences: onCreate");
		
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.settings);
		
		checkLastFm();
	}
	
	public void checkLastFm() {
		final boolean lastFmAvailable = isLastFmInstalled();
		
		if (!lastFmAvailable) {
			Preference lastfm_scrobble = findPreference("lastfm_scrobble");
			lastfm_scrobble.setEnabled(false);
			lastfm_scrobble.setSummary(lastfm_scrobble.getSummary() + getResources().getString(R.string.lastfm_scrobble_app_required));
		}
	}
	
	private boolean isLastFmInstalled() {
		PackageManager pm = getPackageManager();
		boolean result = false;
		try {
			pm.getPackageInfo("fm.last.android", PackageManager.GET_ACTIVITIES);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
}

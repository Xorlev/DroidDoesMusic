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
		
		checkLastFM();
	}
	
	public void checkLastFM() {
		final boolean lastFMAvailable = isIntentAvailable(this, "fm.last.android.metachanged");
		
		if (!lastFMAvailable) {
			Preference lastfm_scrobble = findPreference("lastfm_scrobble");
			lastfm_scrobble.setEnabled(false);
			lastfm_scrobble.setSummary(lastfm_scrobble.getSummary() + getResources().getString(R.string.lastfm_scrobble_app_required));
		}
	}
	
	public static boolean isIntentAvailable(Context context, String action) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list =
	            packageManager.queryIntentActivities(intent,
	                    PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}
}

package com.uid.DroidDoesMusic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class NowPlaying extends Activity {
	protected static final String TAG = "DroidDoesMusic";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nowplaying);
        
        Log.d(TAG, "NowPlaying Activity started");
    }
}
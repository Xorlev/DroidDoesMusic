package com.uid.DroidDoesMusic.UI;

import com.uid.DroidDoesMusic.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class NowPlaying extends Activity {
	protected static final String TAG = "DroidDoesMusic";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nowplaying);
              
        

    }
}
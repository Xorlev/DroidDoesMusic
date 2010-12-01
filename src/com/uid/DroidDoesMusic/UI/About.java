package com.uid.DroidDoesMusic.UI;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.uid.DroidDoesMusic.R;

public class About extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
    	finish();
    	return true;
    }
}
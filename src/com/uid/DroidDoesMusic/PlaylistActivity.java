package com.uid.DroidDoesMusic;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class PlaylistActivity extends ListActivity {
	protected static final String TAG = "DroidDoesMusic";
	
    /** Called when the activity is first created. */
	private String[] lvItems = {"Lil John", "Lil Wayne", "Yount Jeezy", "Chicago" };             
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Playlist Activity started");

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , lvItems));
    }
}


package com.uid.DroidDoesMusic.UI;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class Playlist extends ListActivity {
	protected static final String TAG = "DroidDoesMusic";
	
    /** Called when the activity is first created. */
	private String[] lvItems = {"Lil John", "Lil Wayne", "Yount Jeezy", "Chicago", "Fratellis" };             
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        
        super.onCreate(savedInstanceState);

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , lvItems));
    }
}


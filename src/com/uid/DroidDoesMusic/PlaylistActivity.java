package com.uid.DroidDoesMusic;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PlaylistActivity extends ListActivity {
    /** Called when the activity is first created. */
	private ListView lv;
	
	private String[] lvItems = {"Lil John", "Lil Wayne", "Yount Jeezy", "Chicago" };             
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.playlist);
        lv=getListView();
        Log.d("TESSST", this.toString());

        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , lvItems));
    }
}


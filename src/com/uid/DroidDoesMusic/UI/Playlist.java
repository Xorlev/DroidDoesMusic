package com.uid.DroidDoesMusic.UI;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;

public class Playlist extends ListActivity {
	protected static final String TAG = "DroidDoesMusic";
	Cursor cur = null;
	Uri extContentPlaylists = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
	
    /** Called when the activity is first created. */
	private String[] lvItems = {"Lil John", "Lil Wayne", "Yount Jeezy", "Chicago", "Fratellis" };             
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        
        super.onCreate(savedInstanceState);
        String[] projection = {Audio.Playlists._ID,Audio.Playlists.NAME};
        String[] displayColumns = {Audio.Playlists.NAME};
        int layout = android.R.layout.simple_list_item_1;
        int[] display = new int[] { android.R.id.text1};
        //setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , lvItems));
       
        cur = managedQuery(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,projection,null,null,Audio.Playlists.DEFAULT_SORT_ORDER);
        Log.d("DroidDoesMusic", MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI.toString());
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this,layout,cur,displayColumns,display);
        setListAdapter(mAdapter);
    }
	
	
	public void getData(Uri datauri, String[] projection, String[] displayColumns, int[] display, String sort, int layout) {
	
        // Activity-managed cursor to get sorted list of playlists
        cur = managedQuery(datauri, projection, null, null, sort);
       
        // SimpleCursorAdapter maps the cursor columns to simplelistitems
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, layout, cur, displayColumns, display);
        
        // Set adapter of listview to the SimpleCursorAdapter 
        setListAdapter(mAdapter);
	}
	
	
	
}


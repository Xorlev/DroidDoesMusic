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

/**
 * This class extends List Activity to show what playlists are on the device.
 * 
 * @author jzeimen
 *
 */
public class Playlist extends ListActivity {
	protected static final String TAG = "DroidDoesMusic";
	Cursor cur = null;
	Uri extContentPlaylists = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
	
    /** Called when the activity is first created. */	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        super.onCreate(savedInstanceState);
        
        //Some variables we need for querying and displaying
        String[] projection = {Audio.Playlists._ID,Audio.Playlists.NAME};
        String[] displayColumns = {Audio.Playlists.NAME};
        int layout = android.R.layout.simple_list_item_1;
        int[] display = new int[] { android.R.id.text1};
        
        //Querys and function calls to display on the screen
        cur = managedQuery(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,projection,null,null,Audio.Playlists.DEFAULT_SORT_ORDER);
        Log.d("DroidDoesMusic", MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI.toString());
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this,layout,cur,displayColumns,display);
        setListAdapter(mAdapter);
    }
	
	
}


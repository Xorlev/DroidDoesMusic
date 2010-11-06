package com.uid.DroidDoesMusic;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

public class Library extends ListActivity {
	protected static final String TAG = "DroidDoesMusic";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Library: onCreate");
        
        super.onCreate(savedInstanceState);
        
        // Grabs content URI for a unique list of Artists on the SDcard
        Uri Artists = Audio.Artists.EXTERNAL_CONTENT_URI;

        // Columns to grab from the DB, then the expected mappings
        String[] projection = new String[] {Audio.Artists._ID, Audio.Artists.ARTIST};
        String[] displayColumns = new String[] {Audio.ArtistColumns.ARTIST};
        int[] display = new int[] { android.R.id.text1 };
        
        // Activity-managed cursor to get sorted list of artists
        Cursor cur = managedQuery(Artists, projection, null, null, Audio.ArtistColumns.ARTIST + " ASC");
        
        // SimpleCursorAdatper maps the cursor columns to simplelistitems
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cur, displayColumns, display);
        
        // Set adapter of listview to the SimpleCursorAdapter 
        setListAdapter(mAdapter);
    }
}
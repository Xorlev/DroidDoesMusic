package com.uid.DroidDoesMusic;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Library extends ListActivity {
	protected static final String TAG = "DroidDoesMusic";
	boolean populated = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);

        populateDataIfReady();
    }
	
	@Override
	public void onResume() {
        Log.d(TAG, getClass().getSimpleName() + ": onResume");
		super.onResume();
		
		IntentFilter iff = new IntentFilter();
		iff.addAction(Intent.ACTION_MEDIA_SHARED);
		iff.addAction(Intent.ACTION_MEDIA_MOUNTED);
		iff.addAction(Intent.ACTION_UMS_CONNECTED);
		iff.addAction(Intent.ACTION_UMS_DISCONNECTED);
		registerReceiver(this.externalMediaListener, iff);
		
		populateDataIfReady();
	}
	
	@Override
	public void onPause() {
        Log.d(TAG, getClass().getSimpleName() + ": onResume");
		super.onResume();
		
		unregisterReceiver(this.externalMediaListener);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, getClass().getSimpleName() + ": onListItemClick: " + ((TextView)v).getText() + " (" + id + ")");
		super.onListItemClick(l, v, position, id);
		
		Toast t = Toast.makeText(this, ((TextView)v).getText() + " (" + id + ")", Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}
	
	public void getArtists() {
		// Flag
		populated = true;
		
        // Grabs content URI for a unique list of Artists on the SDcard
        Uri Artists = Audio.Artists.EXTERNAL_CONTENT_URI;

        // Columns to grab from the DB, then the expected mappings
        String[] projection = new String[] {Audio.Artists._ID, Audio.Artists.ARTIST};
        String[] displayColumns = new String[] {Audio.ArtistColumns.ARTIST};
        int[] display = new int[] { android.R.id.text1 };
        
        // Activity-managed cursor to get sorted list of artists
        Cursor cur = managedQuery(Artists, projection, null, null, Audio.ArtistColumns.ARTIST + " ASC");
        
        // SimpleCursorAdapter maps the cursor columns to simplelistitems
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cur, displayColumns, display);
        
        // Set adapter of listview to the SimpleCursorAdapter 
        setListAdapter(mAdapter);		
	}
	
	public void populateDataIfReady() {
        if (!isSdPresent()) {
        	TextView tv = (TextView)findViewById(android.R.id.empty);
        	tv.setText(getResources().getString(R.string.no_sd_card));
        } else if (!populated) {
        	getArtists();
        }
	}
	
	public static boolean isSdPresent() {
	    return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	private BroadcastReceiver externalMediaListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context content, Intent intent) {
	        Log.d(TAG, getClass().getSimpleName() + ": onReceive: " + intent.getData());
	        receivedBroadcast(intent);
		}
	};
	
	private void receivedBroadcast(Intent i) {
		Log.d(TAG, getClass().getSimpleName() + ": receivedBroadcast: " + i.getData());
		
		populateDataIfReady();
	}
}
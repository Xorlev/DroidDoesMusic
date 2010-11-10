package com.uid.DroidDoesMusic.UI;

import com.uid.DroidDoesMusic.R;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
	
	public static final int ARTIST_VIEW = 0;
	public static final int ALBUM_VIEW = 1;
	public static final int SONG_VIEW = 2;
	
	private Cursor cur;
	private int instanceView;
	private boolean populated = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);

        // Get extra data from intent
        instanceView = getIntent().getExtras().getInt("view");
        
        // Populate ListView
        populateDataIfReady(instanceView);
    }
	
	@Override
	public void onResume() {
        Log.d(TAG, getClass().getSimpleName() + ": onResume");
		super.onResume();
		
		IntentFilter iff = new IntentFilter();
		iff.addAction(Intent.ACTION_MEDIA_SHARED);
		iff.addAction(Intent.ACTION_MEDIA_MOUNTED);
		iff.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		iff.addAction(Intent.ACTION_UMS_CONNECTED);
		iff.addAction(Intent.ACTION_UMS_DISCONNECTED);
		registerReceiver(this.externalMediaListener, iff);
		
		populateDataIfReady(instanceView);
	}
	
	@Override
	public void onPause() {
        Log.d(TAG, getClass().getSimpleName() + ": onPause");
		super.onResume();
		
		unregisterReceiver(this.externalMediaListener);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, getClass().getSimpleName() + ": onListItemClick: (" + id + ")");
		super.onListItemClick(l, v, position, id);
		
		cur.moveToPosition(position);
		String selection = cur.getString(0);
		Toast t = Toast.makeText(this, selection + " (" + id + ")", Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
	}
	
	public void getArtists() {
        // Grabs content URI for a unique list of Artists on the SDcard
        Uri extUri = Audio.Artists.EXTERNAL_CONTENT_URI;

        // Columns to grab from the DB, then the expected mappings
        String[] projection = new String[] {Audio.Artists._ID, Audio.Artists.ARTIST, Audio.Artists.NUMBER_OF_ALBUMS};
        String[] displayColumns = new String[] {Audio.Artists.ARTIST, Audio.Artists.NUMBER_OF_ALBUMS};
        int[] display = new int[] { android.R.id.text1, android.R.id.text2 };
        
        String sort = Audio.Media.ARTIST + " ASC";
        
        int layout = android.R.layout.simple_list_item_2;
        
        getData(extUri, projection, displayColumns, display, sort, layout);
	}
	
	public void getAlbums() {
        // Grabs content URI for a unique list of Artists on the SDcard
        Uri extUri = Audio.Albums.EXTERNAL_CONTENT_URI;

        // Columns to grab from the DB, then the expected mappings
        String[] projection = new String[] {Audio.Albums._ID, Audio.Albums.ALBUM, Audio.Albums.ARTIST};
        String[] displayColumns = new String[] {Audio.Albums.ALBUM, Audio.Albums.ARTIST};
        int[] display = new int[] { android.R.id.text1, android.R.id.text2 };

        String sort = Audio.Media.ARTIST + " ASC, " + Audio.Albums.ALBUM + " ASC";
        
        int layout = android.R.layout.simple_list_item_2;
        
        getData(extUri, projection, displayColumns, display, sort, layout);
	}
	
	public void getSongs() {
        // Grabs content URI for a unique list of Artists on the SDcard
        Uri extUri = Audio.Media.EXTERNAL_CONTENT_URI;

        // Columns to grab from the DB, then the expected mappings
        String[] projection = new String[] {Audio.Artists._ID, Audio.Media.TITLE, Audio.Media.ARTIST, Audio.Media.ALBUM, Audio.Media.TRACK};
        String[] displayColumns = new String[] {Audio.Media.TITLE, Audio.Media.ARTIST};
        int[] display = new int[] { android.R.id.text1, android.R.id.text2 };

        String sort = Audio.Media.ARTIST + " ASC, " + Audio.Media.ALBUM + " ASC, " + Audio.Media.TRACK + " ASC";
        
        int layout = android.R.layout.simple_list_item_2;
        
        getData(extUri, projection, displayColumns, display, sort, layout);
	}
	
	public void getData(Uri datauri, String[] projection, String[] displayColumns, int[] display, String sort, int layout) {
		// Flag
		populated = true;

        // Activity-managed cursor to get sorted list of artists
        cur = managedQuery(datauri, projection, null, null, sort);
        
        // SimpleCursorAdapter maps the cursor columns to simplelistitems
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, layout, cur, displayColumns, display);
        
        // Set adapter of listview to the SimpleCursorAdapter 
        setListAdapter(mAdapter);
	}
	
	public void populateDataIfReady(int view) {
        if (!isSdPresent()) {
        	TextView tv = (TextView)findViewById(android.R.id.empty);
        	tv.setText(getResources().getString(R.string.no_sd_card));
        } else if (!populated) {
        	switch (view) {
        	case ARTIST_VIEW:
        		getArtists();
        		return;
        	case ALBUM_VIEW:
        		getAlbums();
        		return;
        	case SONG_VIEW:
        		getSongs();
        		return;
        	}
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
		
		if (i.getAction().equals(Intent.ACTION_UMS_CONNECTED) || i.getAction().equals(Intent.ACTION_MEDIA_SHARED) || i.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
			populated = false;
		} else {
			// Try again after 5 seconds using a handler
			Handler h = new Handler();
			h.postDelayed(new Runnable() {
				public void run() {
					populateDataIfReady(instanceView);
				}
			}, 5000);
		}
	}
}
package com.uid.DroidDoesMusic.UI;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.uid.DroidDoesMusic.R;
import com.uid.DroidDoesMusic.player.Player;

public class LibrarySongView extends ListActivity {
	protected static final String TAG = "DroidDoesMusic";
	public static final String INTENT_ITEM_KEY = "albumName";
	public static final String INTENT_ITEM_KEY2 = "artistId";
	
	private Cursor cur;
	private String albumName = new String();
	private long artistId;
	private boolean populated = false;
	
	private boolean isPlayerBound = false;
	private Player mPlayer;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);

        // Get extra data from intent
        
        try {
	        albumName = getIntent().getExtras().getString(INTENT_ITEM_KEY);
	        setTitle(albumName);
	        
	        if (albumName == null) {
	        	albumName = "";
	        }
	        
	        artistId = getIntent().getExtras().getLong(INTENT_ITEM_KEY2);
        } catch (NullPointerException e) {
        	albumName = "";
        	artistId = 0;
        }
        
        getListView().setFastScrollEnabled(true);
        
        // Populate ListView
        bind();
        populateDataIfReady();
    }
	
	@Override
	public void onResume() {
        Log.d(TAG, getClass().getSimpleName() + ": onResume");
		super.onResume();
		
		IntentFilter iff = new IntentFilter();
		iff.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		iff.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		iff.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		iff.addDataScheme("file");
		registerReceiver(this.externalMediaListener, iff);
		
		populateDataIfReady();
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

		String dataPath = cur.getString(cur.getColumnIndex(Audio.Media.DATA));
		
		if (isPlayerBound) {
			mPlayer.stopMusic();
			mPlayer.setSong(dataPath);
			mPlayer.startMusic();
		}
	}
	
	public void getSongs(String... albumid) {
		String album = new String();
		long artistId;
		String filter = new String();

        // Grabs content URI for a unique list of songs on the SDcard
		try {
			album = albumid[0];
			artistId = Long.parseLong(albumid[1]);
			
			if (album == null || album != "") {
				//filter = Audio.Albums.ALBUM_KEY + " = '" + Audio.keyFor(album) + "'";
				filter = Audio.Albums.ALBUM + " LIKE '" + album + "'";
			} else {
				filter = null;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			album = "";
			filter = null;
		}
		
		Uri extUri = Audio.Media.EXTERNAL_CONTENT_URI;

        // Columns to grab from the DB, then the expected mappings
        String[] projection = new String[] {Audio.Artists._ID, Audio.Media.TITLE, Audio.Media.ARTIST, Audio.Media.ALBUM, Audio.Media.TRACK, Audio.Media.ALBUM_KEY, Audio.Media.DATA};
        String[] displayColumns = new String[] {Audio.Media.TITLE, Audio.Media.ARTIST};
        int[] display = new int[] { android.R.id.text1, android.R.id.text2 };

        String sort = Audio.Media.TITLE + " ASC, " + Audio.Media.ALBUM + " ASC";
        
        int layout = android.R.layout.simple_list_item_2;
        
        getData(extUri, projection, filter, displayColumns, display, sort, layout);
	}
	
	public void getData(Uri datauri, String[] projection, String filter, String[] displayColumns, int[] display, String sort, int layout) {
		// Flag
		populated = true;

        // Activity-managed cursor to get sorted list of artists
        cur = managedQuery(datauri, projection, filter, null, sort);
        
        // SimpleCursorAdapter maps the cursor columns to simplelistitems
        SongListAdapter mAdapter = new SongListAdapter(this, layout, cur, displayColumns, display);
        
        // Set adapter of listview to the SimpleCursorAdapter 
        setListAdapter(mAdapter);
	}
	
	public void populateDataIfReady() {
        if (!isSdPresent()) {
        	TextView tv = (TextView)findViewById(android.R.id.empty);
        	tv.setText(getResources().getString(R.string.no_sd_card));
        } else if (!populated) {
        	getSongs(albumName, Long.toString(artistId));
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
					populateDataIfReady();
				}
			}, 5000);
		}
	}
    private void bind() {
    	Log.d(TAG, "bind: Attempting to bind to Player" );
    	if (!getParent().bindService(new Intent("com.uid.DroidDoesMusic.player.Player"), mConnection, Context.BIND_AUTO_CREATE)) {
    		Toast.makeText(this, "Failed to bind.", Toast.LENGTH_SHORT).show();
    	}
    }
    
    private ServiceConnection mConnection = new ServiceConnection() {
    	public void onServiceConnected(ComponentName classname, IBinder service){
    		Log.d(TAG, "onServiceConnected: Player Service Connected" + classname.toShortString());
    		
    		Player player = ((Player.DataBinder)service).getService();
    		mPlayer = player;
    		    		
    		isPlayerBound = true;
    	}
    	public void onServiceDisconnected(ComponentName classname){
    		Log.d(TAG, "onServiceDisconnected: Player Service Disconnected");
    		
    		isPlayerBound = false;
    	}
    };
	
	private static class SongListAdapter extends SimpleCursorAdapter implements SectionIndexer {
		private AlphabetIndexer mIndexer;
		private final Resources mResources;
		
		public SongListAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
			super(context, layout, c, from, to);
			
			// Get resources for use later
			mResources = context.getResources();
			
			// Fire up an AlphabetIndexer for ListView fastscroll
			mIndexer = new AlphabetIndexer(c, c.getColumnIndex(Audio.Media.TITLE), mResources.getString(R.string.fastscroll_index));
		}
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View v = super.newView(context, cursor, parent);
			
			// Container for views
			ViewHolder vh = new ViewHolder();
			
			// Set views into container
			vh.line1 = (TextView)v.findViewById(android.R.id.text1);
			vh.line2 = (TextView)v.findViewById(android.R.id.text2);
			
			// 'tag' the view with the ViewHolder for use by bindView
			v.setTag(vh);
			return v;
		}
		
		@Override
		public void bindView(View view, Context context, Cursor c) {
			ViewHolder vh = (ViewHolder)view.getTag();
			
			// Get track name, set "unknown" if missing
			String trackName = c.getString(c.getColumnIndex(Audio.Media.TITLE));
			if (trackName == null || trackName.equals(MediaStore.UNKNOWN_STRING)) {
				trackName = mResources.getString(R.string.unknown_track);
			}
			
			// Get album name, set "unknown" if missing
			String albumName = c.getString(c.getColumnIndex(Audio.Media.ALBUM));
			if (albumName == null || albumName.equals(MediaStore.UNKNOWN_STRING)) {
				albumName = mResources.getString(R.string.unknown_album);
			}
			
			// Get artist name, set "unknown" if missing
			String artistName = c.getString(c.getColumnIndex(Audio.Media.ARTIST));
			if (albumName == null || albumName.equals(MediaStore.UNKNOWN_STRING)) {
				albumName = mResources.getString(R.string.unknown_album);
			}
			
			// Set view
			vh.line1.setText(trackName);
			vh.line2.setText(artistName + " - " + albumName);
		}

		public int getPositionForSection(int section) {
			return mIndexer.getPositionForSection(section);
		}

		public int getSectionForPosition(int position) {
			return 0;
		}

		public Object[] getSections() {
			return mIndexer.getSections();
		}
		
		private static class ViewHolder {
			TextView line1;
			TextView line2;
		}
	}
}
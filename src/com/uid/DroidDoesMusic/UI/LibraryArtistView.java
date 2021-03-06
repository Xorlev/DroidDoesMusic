package com.uid.DroidDoesMusic.UI;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import com.uid.DroidDoesMusic.R;

public class LibraryArtistView extends ListActivity {
	protected static final String TAG = "DroidDoesMusic";
	
	private Cursor cur;
	private boolean populated = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);
        
        getListView().setFastScrollEnabled(true);
        
        // Populate ListView
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
				
		Intent i = new Intent(Intent.ACTION_PICK);
		i.setDataAndType(Uri.EMPTY, "vnd.android.cursor.dir/ddm.album");
		i.putExtra(LibraryAlbumView.INTENT_ITEM_KEY, cur.getString(cur.getColumnIndex(Audio.Artists.ARTIST)));
		i.putExtra(LibraryAlbumView.INTENT_ITEM_KEY2, cur.getInt(cur.getColumnIndex(Audio.Artists._ID)));
		startActivity(i);
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
        
        getData(extUri, projection, null, displayColumns, display, sort, layout);
	}
	
	public void getData(Uri datauri, String[] projection, String filter, String[] displayColumns, int[] display, String sort, int layout) {
		// Flag
		populated = true;

        // Activity-managed cursor to get sorted list of artists
        cur = managedQuery(datauri, projection, filter, null, sort);
        
        // SimpleCursorAdapter maps the cursor columns to simplelistitems
        ArtistListAdapter mAdapter = new ArtistListAdapter(this, layout, cur, displayColumns, display);
        
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
	
	private static class ArtistListAdapter extends SimpleCursorAdapter implements SectionIndexer {
		private AlphabetIndexer mIndexer;
		private final Resources mResources;
		
		public ArtistListAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
			super(context, layout, c, from, to);
			
			// Get resources for use later
			mResources = context.getResources();
			
			try {
				// Fire up an AlphabetIndexer for ListView fastscroll
				mIndexer = new AlphabetIndexer(c, c.getColumnIndexOrThrow(Audio.Artists.ARTIST), mResources.getString(R.string.fastscroll_index));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
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
			
			// Get artist name, set "unknown" if missing
			String artistName = c.getString(c.getColumnIndex(Audio.Albums.ARTIST));
			if (artistName == null || artistName.equals(MediaStore.UNKNOWN_STRING)) {
				artistName = mResources.getString(R.string.unknown_artist);
			}
			
//			// Get album name, set "unknown" if missing
//			String albumName = c.getString(c.getColumnIndex(Audio.Albums.ALBUM));
//			if (albumName == null || albumName.equals(MediaStore.UNKNOWN_STRING)) {
//				albumName = mResources.getString(R.string.unknown_album);
//			}
			
			// Set view
			vh.line1.setText(artistName);
//			vh.line2.setText(artistName);
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
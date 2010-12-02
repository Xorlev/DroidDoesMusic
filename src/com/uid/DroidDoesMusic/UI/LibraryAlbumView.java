package com.uid.DroidDoesMusic.UI;

import java.util.HashMap;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.uid.DroidDoesMusic.R;

public class LibraryAlbumView extends ListActivity {
	protected static final String TAG = "DroidDoesMusic";
	public static final String INTENT_ITEM_KEY = "artistName";
	public static final String INTENT_ITEM_KEY2 = "artistId";
	
	private Cursor cur;
	private String artistName = new String();
	private int artistId;
	private boolean populated = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);
        
        // Get extra data from intent
        
        try {
	        artistName = getIntent().getExtras().getString(INTENT_ITEM_KEY);
	        artistId = getIntent().getExtras().getInt(INTENT_ITEM_KEY2);
	        setTitle(artistName);
	        
	        if (artistName == null) {
	        	artistName = "";
	        }
        } catch (NullPointerException e) {
        	artistName = "";
        }
        
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
		super.onPause();
		
		unregisterReceiver(this.externalMediaListener);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, getClass().getSimpleName() + ": onListItemClick: (" + id + ")");
		super.onListItemClick(l, v, position, id);
		cur.moveToPosition(position);
		
		Intent i = new Intent(Intent.ACTION_PICK);
		i.setDataAndType(Uri.EMPTY, "vnd.android.cursor.dir/ddm.track");
		i.putExtra(LibrarySongView.INTENT_ITEM_KEY, cur.getString(1));
		i.putExtra(LibrarySongView.INTENT_ITEM_KEY2, (Long)v.getTag(R.id.artist_id));
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
//		View view = LibraryGroup.group.getLocalActivityManager().startActivity("SongView", i).getDecorView();
//		LibraryGroup.group.replaceView(view);

		startActivity(i);
	}
	
	public void getAlbums(int... artistId) {
		int artist;
		String filter = new String();
		
        // Grabs content URI for a unique list of albums on the SDcard
		try {
			artist = artistId[0];
			
			if (artist > 0) {
				filter = Audio.Media.ARTIST_ID + " = '" + artist + "'";
			} else {
				filter = null;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			artist = 0;
			filter = null;
		}
		
		Uri extUri = Audio.Albums.EXTERNAL_CONTENT_URI;

        // Columns to grab from the DB, then the expected mappings
        String[] projection = new String[] {Audio.Albums._ID, Audio.Albums.ALBUM, Audio.Media.ARTIST_ID, Audio.Albums.ARTIST, Audio.Albums.ALBUM_ART};

        String[] displayColumns = new String[] {Audio.Albums.ALBUM, Audio.Albums.ARTIST};
        int[] display = new int[] { android.R.id.text1, android.R.id.text2 };

        String sort = Audio.Media.ARTIST + " ASC, " + Audio.Albums.ALBUM + " ASC";
        
        int layout = R.layout.album;
        
        getData(extUri, projection, filter, displayColumns, display, sort, layout);
	}
	
	public void getData(Uri datauri, String[] projection, String filter, String[] displayColumns, int[] display, String sort, int layout) {
		// Flag
		populated = true;

        // Activity-managed cursor to get sorted list of artists
        cur = managedQuery(datauri, projection, filter, null, sort);
        
        // Check if cursor is empty
        if (cur.getCount() < 1) {
    		Intent i = new Intent(Intent.ACTION_PICK);
    		i.setDataAndType(Uri.EMPTY, "vnd.android.cursor.dir/ddm.track");
    		i.putExtra(LibrarySongView.INTENT_ITEM_KEY, "");
    		i.putExtra(LibrarySongView.INTENT_ITEM_KEY2, artistId);
    		i.putExtra(LibrarySongView.INTENT_ITEM_KEY3, artistName);
    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        
        // SimpleCursorAdapter maps the cursor columns to simplelistitems
        AlbumListAdapter mAdapter = new AlbumListAdapter(this, layout, cur, displayColumns, display);
        
        // Set adapter of listview to the SimpleCursorAdapter 
        setListAdapter(mAdapter);
	}
	
	public void populateDataIfReady() {
        if (!isSdPresent()) {
        	TextView tv = (TextView)findViewById(android.R.id.empty);
        	tv.setText(getResources().getString(R.string.no_sd_card));
        } else if (!populated) {
        	getAlbums(artistId);
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
	
	public static class AlbumListAdapter extends SimpleCursorAdapter implements SectionIndexer {
		private AlphabetIndexer mIndexer;
		private final Resources mResources;
		
		public AlbumListAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
			super(context, layout, c, from, to);
			
			// Get resources for use later
			mResources = context.getResources();
			
			try {
				// Fire up an AlphabetIndexer for ListView fastscroll
				mIndexer = new AlphabetIndexer(c, c.getColumnIndex(Audio.Albums.ALBUM), mResources.getString(R.string.fastscroll_index));
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
			vh.icon = (ImageView)v.findViewById(R.id.icon);
			vh.line1 = (TextView)v.findViewById(R.id.text1);
			vh.line2 = (TextView)v.findViewById(R.id.text2);
			
			// 'tag' the view with the ViewHolder for use by bindView
			v.setTag(vh);
			return v;
		}
		
		@Override
		public void bindView(View view, Context context, Cursor c) {
			ViewHolder vh = (ViewHolder)view.getTag();
			
			// Album ID
			long albumId = c.getLong(c.getColumnIndex(Audio.Albums._ID));
			
			// Get album icon
			Drawable d;
			String art = c.getString(c.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
	
			ArtRender.Instance(mResources);
			d = ArtRender.getArt(art, albumId);
			
			// Get artist name, set "unknown" if missing
			String artistName = c.getString(c.getColumnIndex(Audio.Albums.ARTIST));
			if (artistName == null || artistName.equals(MediaStore.UNKNOWN_STRING)) {
				artistName = mResources.getString(R.string.unknown_artist);
			}
			
			// Get album name, set "unknown" if missing
			String albumName = c.getString(c.getColumnIndex(Audio.Albums.ALBUM));
			if (albumName == null || albumName.equals(MediaStore.UNKNOWN_STRING)) {
				albumName = mResources.getString(R.string.unknown_album);
			}

			view.setTag(R.id.artist_id, c.getLong(c.getColumnIndex(Audio.Media.ARTIST_ID)));
			
			// Set view
			vh.icon.setImageDrawable(d);
			vh.icon.setScaleType(ScaleType.CENTER);
			vh.icon.setPadding(0, 0, 1, 0);
			vh.line1.setText(albumName);
			vh.line2.setText(artistName);
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
			ImageView icon;
			TextView line1;
			TextView line2;
		}
	}
	
	private static class ArtRender {
		private static ArtRender single;
		
		private static Resources mResources;
		private static final HashMap<Point, Matrix> matrixCache = new HashMap<Point, Matrix>(5);
		private static BitmapDrawable defaultDrawable;
		private static final HashMap<Long, BitmapDrawable> artCache = new HashMap<Long, BitmapDrawable>(25);
		
		private final static int defaultIconResourceId = R.drawable.icon;
		
		private ArtRender(Resources mResources) {
			ArtRender.mResources = mResources;
		}
		
		public static ArtRender Instance(Resources mResources) {
			if (single == null) {
				single = new ArtRender(mResources);
			}
			
			return single;
		}
		
		public static BitmapDrawable getArt(String art, long albumId) {
			BitmapDrawable d;
			
			if (defaultDrawable == null) {
				defaultDrawable = resizeBitmap(BitmapFactory.decodeResource(mResources, defaultIconResourceId));
			}
			
			if (art == null || art.length() == 0) {
				d = defaultDrawable;
			} else {
				if (artCache.containsKey(albumId)) {
					d = artCache.get(albumId);
				} else {
					try {
						Bitmap orig = BitmapFactory.decodeFile(art);

						d = resizeBitmap(orig);
						artCache.put(albumId, d);
					} catch (Exception e) {
						e.printStackTrace();
						d = defaultDrawable;
					}
				}
			}
			
			if (d != null) {
				d.setDither(false);
			} else {
				d = defaultDrawable;
			}
			
			return d;
		}
		
		private static BitmapDrawable resizeBitmap(Bitmap orig) {
			final float scale = mResources.getDisplayMetrics().density;
			Log.d(TAG, String.valueOf(scale));
			
			int w = orig.getWidth();
			int h = orig.getHeight();
			Point pair = new Point(w, h);
			
			Matrix m;
			
			if (matrixCache.containsKey(pair)) {
				m = matrixCache.get(pair);
			} else {
				final int newWidth = 64; // dip
				final int newHeight = 64;
				// Convert the dips to pixels
				float scaleWidth = (float)newWidth/w * scale * scale;
				float scaleHeight = (float)newHeight/h * scale * scale;
				
				m = new Matrix();
				m.postScale(scaleWidth, scaleHeight);
			}
			

			Bitmap resized = Bitmap.createBitmap(orig, 0, 0, w, h, m, true);
			return new BitmapDrawable(resized);
		}
	}
}
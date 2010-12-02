package com.uid.DroidDoesMusic.UI;

import java.util.HashMap;

import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.uid.DroidDoesMusic.UI.SimpleGestureFilter.SimpleGestureListener;
import com.uid.DroidDoesMusic.util.PlaylistManager;

/**
 * This class extends List Activity to show what songs are on the device.
 * 
 * @author jzeimen
 *
 */
public class PlaylistSongView extends ListActivity implements SimpleGestureListener {
	protected static final String TAG = "DroidDoesMusic";
	public static final String INTENT_ITEM_PLAYLIST_NAME = "playlistName";
	public static final String INTENT_ITEM_PLAYLIST_ID = "playlistID";
	Uri extContentPlaylists = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
	private String playlistName = new String();
	private int playlistId;
	private ListAdapter mAdapter;
	private PlaylistManager mPlaylistManager;
	private SimpleGestureFilter detector; 
	
	/** Called when the activity is first created. */	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, getClass().getSimpleName() + ": onCreate");
		super.onCreate(savedInstanceState);
		
		//add gestures
        detector = new SimpleGestureFilter(this,this);
        detector.setMode(SimpleGestureFilter.MODE_DYNAMIC);
        detector.setEnabled(true);
		
		mPlaylistManager = PlaylistManager.getInstance(this);
		try {
			playlistName = getIntent().getExtras().getString(INTENT_ITEM_PLAYLIST_NAME);
			playlistId = getIntent().getExtras().getInt(INTENT_ITEM_PLAYLIST_ID);
			setTitle(playlistName);

			if (playlistName == null) {
				playlistName = "";
			}
		} catch (NullPointerException e) {
			playlistName = "";
		}            
		PlaylistManager.getInstance(this).setPlaylistId(playlistId);
		mAdapter = PlaylistManager.getInstance(this).listSongs(playlistId);
		
		this.setListAdapter(mAdapter);

	}
	
	public void refresh(){
		mAdapter = mPlaylistManager.listSongs(mPlaylistManager.getCurrentPlaylistId());
		this.setListAdapter(mAdapter);
	}
	@Override
	public void onResume(){
		super.onResume();
	}
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, getClass().getSimpleName() + ": onListItemClick: (" + id + ")");
		super.onListItemClick(l, v, position, id);


		/*Toast.makeText(this,info.get(PlaylistManager.ID), Toast.LENGTH_SHORT).show();

		Toast.makeText(this,info.get(PlaylistManager.ARTIST), Toast.LENGTH_SHORT).show();
		Toast.makeText(this,info.get(PlaylistManager.ALBUM), Toast.LENGTH_SHORT).show();
		Toast.makeText(this,info.get(PlaylistManager.TITLE), Toast.LENGTH_SHORT).show();
		Toast.makeText(this,info.get(PlaylistManager.DATAPATH), Toast.LENGTH_SHORT).show();
		info = mPlaylistManager.nextSong();
		
		
		if (info!=null){
			Toast.makeText(this, "Next song:", Toast.LENGTH_SHORT).show();
			Toast.makeText(this,info.get(PlaylistManager.ARTIST), Toast.LENGTH_SHORT).show();
			Toast.makeText(this,info.get(PlaylistManager.ALBUM), Toast.LENGTH_SHORT).show();
			Toast.makeText(this,info.get(PlaylistManager.TITLE), Toast.LENGTH_SHORT).show();
			Toast.makeText(this,info.get(PlaylistManager.DATAPATH), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "This was the last song in the playlist", Toast.LENGTH_SHORT).show();
		}*/
	}
	
	public void onDoubleTap() {
		  
	}
	@Override 
	public boolean dispatchTouchEvent(MotionEvent me){ 
		this.detector.onTouchEvent(me);
		return super.dispatchTouchEvent(me); 
	}
	
	public void onSwipe(int direction, int x, int y) {
		String str = "";
		Log.d(TAG,"Swiped in PLSV");
		
		int pos = getListView().pointToPosition(x, y);
		
		PlaylistManager pm = PlaylistManager.getInstance(this);
		int number=this.mAdapter.getCount();
		if (pos==-1){
			pos=number;
		}
		int id = pm.getSongIdAtPosition(pos-1);
		if (id==-1) return;
		
		Toast.makeText(this, "id "+ id+ " position " +pos, Toast.LENGTH_SHORT).show();
		switch (direction) {
		case SimpleGestureFilter.SWIPE_RIGHT:
			if (pm.addToCurrentPlaylist(id)) {
				Toast.makeText(this, "Added " + str + " to playlist", Toast.LENGTH_SHORT).show();
			}
			
			break;
		case SimpleGestureFilter.SWIPE_LEFT:
			if (pm.removeFromCurrentPlaylist(id)) {
				Toast.makeText(this, "Removed " + str + " from playlist", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "failed to remove "+ id, Toast.LENGTH_SHORT).show();
			}
			
			break;
		}

	}


}


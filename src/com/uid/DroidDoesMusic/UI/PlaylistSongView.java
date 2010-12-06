package com.uid.DroidDoesMusic.UI;

import java.util.HashMap;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.uid.DroidDoesMusic.UI.SimpleGestureFilter.SimpleGestureListener;
import com.uid.DroidDoesMusic.player.Player;
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
	private Player mPlayer;
	private boolean isPlayerBound;
	
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
		bind();
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
		mPlaylistManager.setPosition(position);
		HashMap<String,String> info = mPlaylistManager.currentSong();
		
		if (this.isPlayerBound && info!=null){
			mPlayer.stopMusic();
			mPlayer.setSong(
					info.get(PlaylistManager.ALBUM),
					info.get(PlaylistManager.ARTIST),
					info.get(PlaylistManager.TITLE),
					info.get(PlaylistManager.DATAPATH));
			mPlayer.startMusic();
			finish();
		}

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
		
		//Toast.makeText(this, "id "+ id+ " position " +pos, Toast.LENGTH_SHORT).show();
		switch (direction) {
		case SimpleGestureFilter.SWIPE_RIGHT:

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
    
    private void bind() {
    	Log.d(TAG, "bind: Attempting to bind to Player" );
    	
    	
    	try {
    		getParent().bindService(new Intent("com.uid.DroidDoesMusic.player.Player"), mConnection, Context.BIND_AUTO_CREATE);
    	} catch(NullPointerException e) {
	    	bindService(new Intent("com.uid.DroidDoesMusic.player.Player"), mConnection, Context.BIND_AUTO_CREATE);
	    }
    }

}


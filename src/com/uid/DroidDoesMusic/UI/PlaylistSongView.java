package com.uid.DroidDoesMusic.UI;

import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.uid.DroidDoesMusic.util.PlaylistManager;

/**
 * This class extends List Activity to show what songs are on the device.
 * 
 * @author jzeimen
 *
 */
public class PlaylistSongView extends ListActivity {
	protected static final String TAG = "DroidDoesMusic";
	public static final String INTENT_ITEM_PLAYLIST_NAME = "playlistName";
	public static final String INTENT_ITEM_PLAYLIST_ID = "playlistID";
	Uri extContentPlaylists = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
	private String playlistName = new String();
	private int playlistId;
	private PlaylistManager mPlaylistManager;
    /** Called when the activity is first created. */	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        super.onCreate(savedInstanceState);
        mPlaylistManager = PlaylistManager.getInstance(this);
        try {
	        playlistName = getIntent().getExtras().getString(INTENT_ITEM_PLAYLIST_NAME);
	        playlistId = getIntent().getExtras().getInt(INTENT_ITEM_PLAYLIST_ID);
	       // setTitle(playlistName);
	        
	        if (playlistName == null) {
	        	playlistName = "";
	        }
        } catch (NullPointerException e) {
        	playlistName = "";
        }            

        	this.setListAdapter(PlaylistManager.getInstance(this).listSongs(playlistId));
                
    }
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, getClass().getSimpleName() + ": onListItemClick: (" + id + ")");
		super.onListItemClick(l, v, position, id);
		mPlaylistManager.setSelectedPlaylist(playlistId);
		mPlaylistManager.setPosition(position);

	}
		
	
}


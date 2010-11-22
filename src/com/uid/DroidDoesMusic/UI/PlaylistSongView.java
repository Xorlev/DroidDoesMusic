package com.uid.DroidDoesMusic.UI;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Playlists.Members;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.uid.DroidDoesMusic.R;
import com.uid.DroidDoesMusic.util.PlaylistManager;

/**
 * This class extends List Activity to show what playlists are on the device.
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
	
    /** Called when the activity is first created. */	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        super.onCreate(savedInstanceState);
        
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

        	this.setListAdapter(PlaylistManager.getInstance(this).listSongs(playlistId));
                
    }
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, getClass().getSimpleName() + ": onListItemClick: (" + id + ")");
		super.onListItemClick(l, v, position, id);
		
		this.setListAdapter(PlaylistManager.getInstance(this).listSongs(position));
		
		
		Intent i = new Intent(Intent.ACTION_PICK);
		i.setDataAndType(Uri.EMPTY, "vnd.android.cursor.dir/ddm.track");
		i.putExtra(Playlist.INTENT_ITEM_PLAYLIST_NAME,"Selected Playlist");
		i.putExtra(Playlist.INTENT_ITEM_PLAYLIST_ID, (Integer)v.getTag(R.id.artist_id));
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		startActivity(i);
		
	}
	

	
	
}


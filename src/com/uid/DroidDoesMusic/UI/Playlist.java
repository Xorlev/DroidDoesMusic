package com.uid.DroidDoesMusic.UI;

import android.app.ListActivity;
import android.content.Context;
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

import com.uid.DroidDoesMusic.util.PlaylistManager;

/**
 * This class extends List Activity to show what playlists are on the device.
 * 
 * @author jzeimen
 *
 */
public class Playlist extends ListActivity {
	protected static final String TAG = "DroidDoesMusic";
	Cursor cur = null;
	Uri extContentPlaylists = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
	
    /** Called when the activity is first created. */	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        super.onCreate(savedInstanceState);
       ListAdapter adapter;
       Log.d("DroidDoesMusic",Audio.Playlists.EXTERNAL_CONTENT_URI.toString());

       // PlaylistManager pl = PlaylistManager.getInstance();
        adapter = listPlaylists(this);
        
		this.setListAdapter(adapter);
        
    }
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, getClass().getSimpleName() + ": onListItemClick: (" + id + ")");
		super.onListItemClick(l, v, position, id);
		
		cur.moveToPosition(position);
		String selection = cur.getString(0);
		
	}
	
	public SimpleCursorAdapter listPlaylists(Context context){
		   
        String[] projection = {Audio.Playlists._ID,Audio.Playlists.NAME};
        String[] displayColumns = {Audio.Playlists.NAME};
        int layout = android.R.layout.simple_list_item_1;
        int[] display = new int[] { android.R.id.text1};
        Log.d("DroidDoesMusic",Audio.Playlists.EXTERNAL_CONTENT_URI.toString());
        cur = managedQuery(android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, projection, null, null, Audio.Playlists.DEFAULT_SORT_ORDER);
        Log.d("DroidDoesMusic","After Query");
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(context,layout,cur,displayColumns,display);
		return adapter;
	}
	public ListAdapter listSongs(Context context, long selection){
		ListAdapter adapter;
        String[] projection = {Audio.AudioColumns.TITLE,Audio.AudioColumns._ID};
        String[] displayColumns = {Audio.Playlists.NAME};
        int layout = android.R.layout.simple_list_item_1;
        int[] display = new int[] { android.R.id.text1};
        cur = managedQuery(Members.getContentUri(Members._ID, selection), projection, null, null, Audio.Media.DEFAULT_SORT_ORDER);
		adapter = new SimpleCursorAdapter(context,layout,cur,displayColumns,display);
		return adapter;
	}
	
	
}


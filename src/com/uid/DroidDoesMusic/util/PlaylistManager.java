package com.uid.DroidDoesMusic.util;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Playlists.Members;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public class PlaylistManager extends Activity {
	static private PlaylistManager instance;
	private PlaylistManager(){
		Log.d("DroidDoesMusic","Created manager");
		
	}
	public static PlaylistManager getInstance(){
		if(instance == null){
			return new PlaylistManager();
		} else return instance;
	}
	public SimpleCursorAdapter listPlaylists(Context context){
   
        String[] projection = {Audio.Playlists._ID,Audio.Playlists.NAME};
        String[] displayColumns = {Audio.Playlists.NAME};
        int layout = android.R.layout.simple_list_item_1;
        int[] display = new int[] { android.R.id.text1};
        Log.d("DroidDoesMusic",Audio.Playlists.EXTERNAL_CONTENT_URI.toString());
        Cursor cur = managedQuery(android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, projection, null, null, Audio.Playlists.DEFAULT_SORT_ORDER);
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
        Cursor cur = managedQuery(Members.getContentUri(Members._ID, selection), projection, null, null, Audio.Media.DEFAULT_SORT_ORDER);
		adapter = new SimpleCursorAdapter(context,layout,cur,displayColumns,display);
		return adapter;
	}
}

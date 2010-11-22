package com.uid.DroidDoesMusic.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Playlists.Members;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public class PlaylistManager {
	static private PlaylistManager instance;
	Context context;
	ContentResolver cr;
	private static Cursor mCur;
	private PlaylistManager(Context context){
		
		this.context = context;
		this.cr = context.getContentResolver();
		
	}
	public static PlaylistManager getInstance(Context context){
		if(instance == null){
			return new PlaylistManager(context);
		} else return instance;
	}
	public SimpleCursorAdapter listPlaylists(){
   
        String[] projection = {Audio.Playlists._ID,Audio.Playlists.NAME};
        String[] displayColumns = {Audio.Playlists.NAME};
        int layout = android.R.layout.simple_list_item_1;
        int[] display = new int[] { android.R.id.text1};
        mCur = cr.query(android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, projection, null, null, Audio.Playlists.DEFAULT_SORT_ORDER);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(context,layout,mCur,displayColumns,display);
		Log.d("DroidDoesMusic","Here I am:   "+android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI.toString());
		return adapter;
	}
	public ListAdapter listSongs( int selection){
		mCur.moveToPosition(selection);
		ListAdapter adapter;
        String[] projection = {Audio.AudioColumns.TITLE,Audio.AudioColumns._ID};
        String[] displayColumns = {Audio.AudioColumns.TITLE};
        int layout = android.R.layout.simple_list_item_1;
        int[] display = new int[] { android.R.id.text1};
        int id= mCur.getInt(0);
        Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
        mCur = cr.query(membersUri, projection, null, null, Audio.Media.DEFAULT_SORT_ORDER);
		adapter = new SimpleCursorAdapter(context,layout,mCur,displayColumns,display);
		return adapter;
	}
}

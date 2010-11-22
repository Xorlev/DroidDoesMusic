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
        
        
        /*************Some Useful code I found on the web*****************
        final String [] STAR= {"*"};
        Log.i(TAG, "All the titles");
        Uri allaudio_uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor ca= managedQuery(allaudio_uri, STAR, null,null,null);
        for(ca.moveToFirst(); !ca.isAfterLast(); ca.moveToNext()){
            if(ca.isFirst()){   // print all the fields of the first song
                for(int k= 0; k<ca.getColumnCount(); k++)
                    Log.i(TAG, "  "+ca.getColumnName(k)+"="+ca.getString(k));
            }else{              // but just the titles of the res
                Log.i(TAG, ca.getString(ca.getColumnIndex("title")));
            }
        }
        Log.i(TAG, "--------------------------");
        Log.i(TAG, "All the playlists");
        Uri playlist_uri= MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;    
        Cursor cursor= managedQuery(playlist_uri, STAR, null,null,null);
        cursor.moveToFirst();
        for(int r= 0; r<cursor.getCount(); r++, cursor.moveToNext()){
            Log.i(TAG, "-----");
            Log.i(TAG, "Playlist " + cursor.getString(cursor.getColumnIndex("name")));
            for(int k= 0; k<cursor.getColumnCount(); k++)           
                Log.i(TAG, cursor.getColumnName(k)+"="+cursor.getString(k));

            // the members of this playlist
            int id= cursor.getInt(0);
            Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri("external", id);
            Cursor membersCursor = managedQuery(membersUri, STAR, null, null, null);
            membersCursor.moveToFirst();
            for(int s= 0; s<membersCursor.getCount(); s++, membersCursor.moveToNext())
                Log.i(TAG, "  "+membersCursor.getString(membersCursor.getColumnIndex("title")));
            membersCursor.close();
        }
        cursor.close();
      /*******************************************************/
            
       ListAdapter adapter;
       Log.d("DroidDoesMusic",Audio.Playlists.EXTERNAL_CONTENT_URI.toString());

        PlaylistManager pl = PlaylistManager.getInstance(this);
        adapter = pl.listPlaylists();
        
		this.setListAdapter(adapter);
        
    }
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, getClass().getSimpleName() + ": onListItemClick: (" + id + ")");
		super.onListItemClick(l, v, position, id);
		
		this.setListAdapter(PlaylistManager.getInstance(this).listSongs(position));
		
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


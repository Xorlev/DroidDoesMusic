package com.uid.DroidDoesMusic.UI;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
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
	Uri extContentPlaylists = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
	private String playlistName = new String();
	
	private int playlistId;
	private ListAdapter mAdapter;
    /** Called when the activity is first created. */	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        super.onCreate(savedInstanceState);
        
        mAdapter = PlaylistManager.getInstance(this).listPlaylists();
       	this.setListAdapter(mAdapter);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		registerReceiver(addPlaylistReceiver, new IntentFilter(Main.ADDPLAYLIST));
	}
	
	@Override
	protected
	void onPause(){
		super.onPause();
		this.unregisterReceiver(addPlaylistReceiver);
	}
	
	
	/**
	 * broadcast reciver used to call add playlist
	 */
	private BroadcastReceiver addPlaylistReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context content, Intent intent) {
			addPlaylist();
			
		}
	};
	
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, getClass().getSimpleName() + ": onListItemClick: (" + id + ")");
		super.onListItemClick(l, v, position, id);
			
		
		Log.d(TAG,"Position: "+position);
		Cursor c = (Cursor)mAdapter.getItem(position);
		Log.d(TAG,"Cursor to string:"+c.toString());
		playlistId = c.getInt(c.getColumnIndex(MediaStore.Audio.Playlists._ID));
		Log.d(TAG,"Column Index: "+c.getColumnIndex(MediaStore.Audio.Playlists._ID));
		Log.d(TAG,"Playlistid: "+playlistId);
		Intent i = new Intent(this,PlaylistSongView.class);
		i.putExtra(PlaylistSongView.INTENT_ITEM_PLAYLIST_NAME,(String)v.getTag(android.R.id.text1));
		i.putExtra(PlaylistSongView.INTENT_ITEM_PLAYLIST_ID, playlistId);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		
	}
	
	public void addPlaylist(){
		final PlaylistManager pm = PlaylistManager.getInstance(this);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		alert.setView(input);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString().trim();
				if(value != null && value.length()>0)
				pm.createPlaylist(value);
				//Toast.makeText(getApplicationContext(), value,Toast.LENGTH_SHORT).show();
			}
		});
		
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.setMessage("Add a playlist");
		alert.show();
		
	}
	
	
}


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

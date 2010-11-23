package com.uid.DroidDoesMusic.util;

import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.uid.DroidDoesMusic.player.Player;


/**
 * A class to manage all of the playlists for DroidDoesMusic.
 * @author jzeimen
 */
public class PlaylistManager {
	static private PlaylistManager instance;
	Context context;
	ContentResolver cr;
	private static final String TAG = new String("DroidDoesMusic");
	private static Player mPlayer;
	private static boolean isPlayerBound;
	private static Cursor mCur;
	private static int mPosition;
	private static int mCurrentPlaylist =25;
	private static Cursor mCurrentPlaylistMembers;
	private static final String [] STAR = {"*"};

	//Positions in the next song string array
	public static final String ARTIST="ARTIST";
	public static final String ALBUM="ALBUM";
	public static final String TITLE="TITLE";
	public static final String DATAPATH="DATAPATH";
	
	
	/**
	 * Private constructor due to Singleton pattern.
	 * @param context
	 */
	private PlaylistManager(Context context){

		this.context = context;
		this.cr = context.getContentResolver();

	}
	
	/**
	 * This class is a Singleton pattern so you should get the instance that is
	 * all ready running.
	 * Pass in the context usually just type "this" into the function; 
	 * @param context
	 * @return
	 */
	public static PlaylistManager getInstance(Context context){
		if(instance == null){
			return new PlaylistManager(context);
		} else return instance;
	}
	
	/**
	 * Lists the current playlists on the device in a SimpleCursorAdapter.
	 * @return
	 */
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
	
	/**
	 * Prints the songs in the playlist given by the playlistId.
	 * @param playlistId
	 * @return
	 */
	public ListAdapter listSongs( int playlistId){
		ListAdapter adapter;
		String[] displayColumns = {Audio.AudioColumns.TITLE};
		int layout = android.R.layout.simple_list_item_1;
		int[] display = new int[] { android.R.id.text1};
		Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
		mCur = cr.query(membersUri, STAR, null, null, Audio.Media.DEFAULT_SORT_ORDER);
		adapter = new SimpleCursorAdapter(context,layout,mCur,displayColumns,display);
		return adapter;
	}

	/**
	 * Returns a string array for info on the next song.
	 * use PlaylistManager.ARTIST, PlaylistManager.ALBUM
	 * PlaylistManager.TITLE, PlaylistManager.DATAPATH for the
	 * indexes on the string array.
	 * Returns null if no next song.
	 * 
	 * @return
	 */
	public HashMap<String,String> nextSong(){
		HashMap<String,String> hashmap = new HashMap<String,String>();
		Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri("external", mCurrentPlaylist);
		Cursor currentSongQuery = cr.query(membersUri, STAR, null, null, Audio.Media.DEFAULT_SORT_ORDER);
		if(currentSongQuery!=null){
			if(currentSongQuery.moveToPosition(mPosition)){
				if(currentSongQuery.moveToNext()){
					this.mPosition+=1;
					String song [] = new String[4];
					hashmap.put(ARTIST,currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.ARTIST)));
					hashmap.put(ALBUM,currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.ALBUM)));
					hashmap.put(TITLE,currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.TITLE)));
					hashmap.put(DATAPATH,currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.DATA)));
					return hashmap;	
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	/**
	 *returns a string array with info about the current song
	 * use PlaylistManager.ARTIST, PlaylistManager.ALBUM
	 * PlaylistManager.TITLE, PlaylistManager.DATAPATH for the
	 * indexes on the string array.
	 * Returns null if there is no known current song
	 * @return
	 */
	public HashMap<String,String> currentSong(){
		HashMap<String,String> hashmap = new HashMap<String,String>();
		Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri("external", mCurrentPlaylist);
		Cursor currentSongQuery = cr.query(membersUri, STAR, null, null, Audio.Media.DEFAULT_SORT_ORDER);
		if(currentSongQuery!=null){
			if(currentSongQuery.moveToPosition(mPosition)){
				String song [] = new String[4];
				hashmap.put(ARTIST,currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.ARTIST)));
				hashmap.put(ALBUM,currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.ALBUM)));
				hashmap.put(TITLE,currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.TITLE)));
				hashmap.put(DATAPATH,currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.DATA)));
				return hashmap;	
			}else {
				return null;
			}
		} else {
			return null;
		}
	} 
	
	/**
	 * Calling this function will add the song given by it's Uri 
	 * to the currently selected playlist.
	 * CURRENTLY NOT COMPLETE
	 * @param uri
	 * @return
	 * Boolean return value returns true on success and false on failure
	 */
	public boolean addToCurrentPlaylist(String datapath){
       /* ContentValues initialValues = new ContentValues();
        String[] names;

    	SQLiteDatabase sq ;
		CursorFactory cf = null;
		sq = SQLiteDatabase.openDatabase(this.getCurrentPlaylistUri().getPath(), cf, SQLiteDatabase.OPEN_READWRITE);
		Cursor newcur = sq.query("audio_playlists_map", STAR, "*", STAR, null, null, null);
		names=newcur.getColumnNames();
		for (String name : names){
    		//Toast.makeText(context, name+": "+cur.getString(cur.getColumnIndex(name)), Toast.LENGTH_SHORT).show();
    		Log.d(TAG,"Column Names: "+name);
    	}
		
		Cursor cur;
        
        cur = cr.query(Audio.Media.EXTERNAL_CONTENT_URI, STAR, Audio.Media.DATA +" LIKE '"+datapath+"'", null, null);
        cur.moveToFirst();
        if (cur != null){        
        	names= cur.getColumnNames();
        	Toast.makeText(context, "Song Found!", Toast.LENGTH_SHORT);
        	
    		
        	
        	Log.d(TAG,"Number of entries:"+names.length);
        	for (String name : names){
        		//Toast.makeText(context, name+": "+cur.getString(cur.getColumnIndex(name)), Toast.LENGTH_SHORT).show();
        		Log.d(TAG,"Column Names: "+name);
        			
        		
        		
        		initialValues.put(name, "'"+cur.getString(cur.getColumnIndex(name))+"'");
        	}
        	
        	cur = cr.query(this.getCurrentPlaylistUri(), STAR, null,null,null);
        	cur.moveToFirst();
        	names= cur.getColumnNames();
        	Log.d(TAG,"Number of entries:"+names.length);
        	for (String name : names){
        		Toast.makeText(context, name,Toast.LENGTH_SHORT).show();
        		Log.d(TAG,"Column Names: "+name);
        	}
        	
        	Uri url= cr.insert(getCurrentPlaylistUri(), initialValues);
        	return (url!=null);
        	
        } else */return false;
		
	}

	/**
	 * Returns a Uri to that can be used to query the currently selected playlist.
	 * @return
	 */
	public Uri getCurrentPlaylistUri(){
		return MediaStore.Audio.Playlists.Members.getContentUri("external", mCurrentPlaylist);
	}
	/**
	 * Set's the song we are on in the playlist.
	 * @param position
	 */
	public void setPosition(int position) {
		mPosition = position;	
	}
	/**
	 * Sets the playlist we are using.
	 * @param playlistId
	 */
	public void setSelectedPlaylist(int playlistId){
		mCurrentPlaylist = playlistId;
	}



}

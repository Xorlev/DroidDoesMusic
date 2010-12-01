package com.uid.DroidDoesMusic.util;

import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
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
	private static int mCurrentPlaylist;
	private static Cursor mCurrentPlaylistMembers;
	private static final String [] STAR = {"*"};
	private static SharedPreferences sp;

	//Positions in the next song string array
	public static final String ARTIST="ARTIST";
	public static final String ALBUM="ALBUM";
	public static final String TITLE="TITLE";
	public static final String DATAPATH="DATAPATH";
	public static final String ID = "_ID";
	
	
	/**
	 * Private constructor due to Singleton pattern.
	 * @param context
	 */
	private PlaylistManager(Context context){

		this.context = context;
		this.cr = context.getContentResolver();
		PlaylistManager.sp = PreferenceManager.getDefaultSharedPreferences(context);
		
		mCurrentPlaylist = PlaylistManager.sp.getInt("PlaylistCurrentPlaylistId", 0);
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
		
		try{
		mCur.moveToFirst();
		} catch (NullPointerException e){
			return null;
		}
		//TODO this line causes something crazy to happen if there is no music
		//Log.d("DroidDoesMusic","Here I am:   "+mCur.getInt(mCur.getColumnIndex(Audio.Playlists._ID)));
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(context,layout,mCur,displayColumns,display);
		
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

	public void setPlaylistId(int playlistId){
		PlaylistManager.mCurrentPlaylist = playlistId;
		
		SharedPreferences.Editor e = sp.edit();
		e.putInt("PlaylistCurrentPlaylistId", playlistId);
		e.commit();
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
					hashmap.put(ID,currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media._ID)));
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
				hashmap.put(ID,currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media._ID)));
				Toast.makeText(context,"current playlist id: "+mCurrentPlaylist, Toast.LENGTH_SHORT).show();
				return hashmap;	
			}else {
				return null;
			}
		} else {
			return null;
		}
	} 
	

	
	//borrowed from http://stackoverflow.com/questions/3182937
	public void addToPlaylist(ContentResolver resolver, int audioId) {

        String[] cols = new String[] {
                "count(*)"
        };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", mCurrentPlaylist);
        
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base+audioId);
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
        uri=resolver.insert(uri, values);
        Toast.makeText(context,"uri path just added song: "+uri.getPath(), Toast.LENGTH_SHORT).show();
    }

   public void removeFromPlaylist(ContentResolver resolver, int audioId) {
       Log.v("made it to add",""+audioId);
        String[] cols = new String[] {
                "count(*)"
        };
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", mCurrentPlaylist);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();

        resolver.delete(uri, MediaStore.Audio.Playlists.Members.AUDIO_ID +" = "+audioId, null);
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

	public boolean addToCurrentPlaylist(int id) {
//		Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//		ContentResolver cr = context.getContentResolver();
//		Cursor cur = cr.query(Audio.Media.EXTERNAL_CONTENT_URI, STAR, null, null, null);
//		cur.moveToFirst();
//		int id2=cur.getInt(cur.getColumnIndex(Audio.Media._ID));
		this.addToPlaylist(context.getContentResolver(), id);
		return true;
	}
	
	public boolean removeFromCurrentPlaylist(int id) {
		this.removeFromPlaylist(context.getContentResolver(), id);
		return true;
	}



}

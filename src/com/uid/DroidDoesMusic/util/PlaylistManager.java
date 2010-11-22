package com.uid.DroidDoesMusic.util;

import com.uid.DroidDoesMusic.player.Player;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
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
	private static final String TAG = new String("DroidDoesMusic");
	private static Player mPlayer;
	private static boolean isPlayerBound;
	private static Cursor mCur;
	private static int mPosition;
	private static int mCurrentPlaylist;
	private static Cursor mCurrentPlaylistMembers;
	private static final String [] STAR = {"*"};

	//Positions in the next song string array
	public static final int ARTIST=0;
	public static final int ALBUM=1;
	public static final int TITLE=2;
	public static final int DATAPATH=3;

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
	public String [] nextSong(){
		Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri("external", mCurrentPlaylist);
		Cursor currentSongQuery = cr.query(membersUri, STAR, null, null, Audio.Media.DEFAULT_SORT_ORDER);
		if(currentSongQuery!=null){
			if(currentSongQuery.moveToPosition(mPosition)){
				if(currentSongQuery.moveToNext()){
					this.mPosition+=1;
					String song [] = new String[4];
					song[ARTIST]=currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.ARTIST));
					song[ALBUM]=currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.ALBUM));
					song[TITLE]=currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.TITLE));
					song[DATAPATH]=currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.DATA));
					return song;	
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
	public String[] currentSong(){
		Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri("external", mCurrentPlaylist);
		Cursor currentSongQuery = cr.query(membersUri, STAR, null, null, Audio.Media.DEFAULT_SORT_ORDER);
		if(currentSongQuery!=null){
			if(currentSongQuery.moveToPosition(mPosition)){
				String song [] = new String[4];
				song[ARTIST]=currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.ARTIST));
				song[ALBUM]=currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.ALBUM));
				song[TITLE]=currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.TITLE));
				song[DATAPATH]=currentSongQuery.getString(currentSongQuery.getColumnIndex(Audio.Media.DATA));
				return song;	
			}else {
				return null;
			}
		} else {
			return null;
		}
	} 



	public void setPosition(int position) {
		mPosition = position;	
	}
	public void setSelectedPlaylist(int playlistId){
		mCurrentPlaylist = playlistId;
	}



}

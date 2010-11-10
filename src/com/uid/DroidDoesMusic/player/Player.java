package com.uid.DroidDoesMusic.player;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class Player extends Service {
	MediaPlayer mp = new MediaPlayer();
	public final String TAG="Player";
	public void onCreate(){
		super.onCreate();
		Toast.makeText(this, "Player started...", Toast.LENGTH_SHORT).show();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		return START_STICKY;
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		Toast.makeText(this, "Player destroyed...", Toast.LENGTH_SHORT).show();
	}
	
	public class DataBinder extends Binder {
		public Player getService() {
			return Player.this;
		}
	}
	private final IBinder mBinder = new DataBinder();
	
	@Override
	public IBinder onBind(Intent i){
		Log.d(TAG,"Player service bound");
		return mBinder;	
	}
	
	public void startMusic(){
	    
	    
	    try {
			mp.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    mp.start();
				
	}
	
	public void setSong(String dataPath) {
		try {
			mp.setDataSource(dataPath);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//I see this being the 2 vertical line button that pauses the song in the middle, allowing the user to continue play later
	public void pauseMusic() {
		mp.pause();
		
	}
	//I see this ending playback and returning the song to the beginning or something like that
	public void stopMusic() {
		mp.stop();
	}
	public boolean isPlaying(){
		return mp.isPlaying();
	}
	
	
}

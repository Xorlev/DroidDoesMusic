package com.uid.DroidDoesMusic.player;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class Player extends Service {
	public void onCreate(){
	    MediaPlayer mp = new MediaPlayer();
	    try {
			mp.setDataSource("/sdcard/Music/3OH!3/Want [Explicit]/03 - Dont Trust Me (Explicit Album Version).mp3");
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
	
	public IBinder onBind(Intent i){
		return null;	
	}
}

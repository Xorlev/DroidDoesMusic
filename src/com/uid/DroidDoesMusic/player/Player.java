package com.uid.DroidDoesMusic.player;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class Player extends Service {
	protected final String TAG = "DroidDoesMusic";
	private static final String SERVICE_PREFIX = "com.uid.DroidDoesMusic.player.";
	public static final String SERVICE_CHANGE_NAME = SERVICE_PREFIX + "CHANGE";
	public static final String SERVICE_UPDATE_NAME = SERVICE_PREFIX + "UPDATE";
	public static final String SERVICE_STOP_NAME = SERVICE_PREFIX + "STOP";
	
	private final IBinder mBinder = new DataBinder();
	private MediaPlayer mp = new MediaPlayer();
	private Handler mHandler = new Handler();
	
	private boolean isSongStarted = false;
	String artist;
	String title;
	
	private Intent lastChangeBroadcast;
	private Intent lastUpdateBroadcast;
	
	private NotificationManager mNotificationManager;

	public void onCreate(){
		super.onCreate();
		mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		return START_STICKY;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
		mNotificationManager.cancelAll();
		mp.release();
	}
	
	public class DataBinder extends Binder {
		public Player getService() {
			return Player.this;
		}
	}
		
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
		isSongStarted = true;
		spawnNotification();
		mHandler.postDelayed(mUpdateProgressTimeTask, 500);
	}
	
	public void setSong(String artist, String title, String dataPath) {
		try {
			this.artist = artist;
			this.title = title;
			mp.setDataSource(dataPath);
			changeNotify();
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
		mNotificationManager.cancel(1);
		mHandler.removeCallbacks(mUpdateProgressTimeTask);
		mp.pause();
		
	}
	//I see this ending playback and returning the song to the beginning or something like that
	public void stopMusic() {
		isSongStarted = false;
		mNotificationManager.cancel(1);
		mHandler.removeCallbacks(mUpdateProgressTimeTask);
		mp.stop();
		mp.reset();
	}
	
	public boolean isSongStarted() {
		return isSongStarted;
	}
	
	public boolean isPlaying(){
		return mp.isPlaying();
	}
	
	private void updateProgress() {
		if (mp != null && mp.isPlaying()) {
			if (lastUpdateBroadcast != null) {
				getApplicationContext().removeStickyBroadcast(lastUpdateBroadcast);
			}
			
			lastUpdateBroadcast = new Intent(SERVICE_UPDATE_NAME);
			lastUpdateBroadcast.putExtra("duration", mp.getDuration());
			lastUpdateBroadcast.putExtra("position", mp.getCurrentPosition());
			getApplicationContext().sendStickyBroadcast(lastUpdateBroadcast);
		}
	}
	
	private void changeNotify() {
	    if (lastChangeBroadcast != null) {
	        getApplicationContext().removeStickyBroadcast(lastChangeBroadcast);
	      }
	      lastChangeBroadcast = new Intent(SERVICE_CHANGE_NAME);
	      lastChangeBroadcast.putExtra("artist", artist);
	      lastChangeBroadcast.putExtra("title", title);
	      getApplicationContext().sendStickyBroadcast(lastChangeBroadcast);
	}
	
	private void spawnNotification() {
		CharSequence contentTitle = getString(com.uid.DroidDoesMusic.R.string.app_name);
		String contentText = artist + " - " + title;
		
		Notification notification = new Notification(android.R.drawable.ic_media_play, contentText, System.currentTimeMillis());
		notification.flags = Notification.FLAG_NO_CLEAR
				| Notification.FLAG_ONGOING_EVENT;
		Context c = getApplicationContext();
		Intent notificationIntent = new Intent("com.uid.DroidDoesMusic.UI.Main");
//		notificationIntent.setAction(Intent.ACTION_MAIN);
//		notificationIntent.addCategory(Intent.CATEGORY_DEFAULT);
//		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(c, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(c, contentTitle, contentText, contentIntent);
		mNotificationManager.notify(1, notification);
	}
	
	private Runnable mUpdateProgressTimeTask = new Runnable() {
		public void run() {
			updateProgress();
			mHandler.postDelayed(this, 500);
		}
	};
	

}

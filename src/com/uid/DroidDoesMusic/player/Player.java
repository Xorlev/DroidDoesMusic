package com.uid.DroidDoesMusic.player;

import java.io.IOException;
import java.util.LinkedList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.uid.DroidDoesMusic.R;

public class Player extends Service implements OnCompletionListener {
	protected final String TAG = "DroidDoesMusic";
	private static final String SERVICE_PREFIX = "com.uid.DroidDoesMusic.player.";
	public static final String SERVICE_CHANGE_NAME = SERVICE_PREFIX + "CHANGE";
	public static final String SERVICE_UPDATE_NAME = SERVICE_PREFIX + "UPDATE";
	public static final String SERVICE_STOP_NAME = SERVICE_PREFIX + "STOP";
	
	private final IBinder mBinder = new DataBinder();
	private MediaPlayer mp = new MediaPlayer();
	private Handler mHandler = new Handler();
	
	private boolean isSongStarted = false;
	private String artist;
	private String album;
	private String title;
	
	private Intent lastChangeBroadcast;
	private Intent lastUpdateBroadcast;
	
	private NotificationManager mNotificationManager;
	private LastfmBroadcaster lbm = new LastfmBroadcaster(this);
	
	private LinkedList<Song> songQueue = new LinkedList<Song>();

	public void onCreate(){
		super.onCreate();
		mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		mp.setOnCompletionListener(this);
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
		lbm.playbackcomplete();
		
		if (lastUpdateBroadcast != null) {
			getApplicationContext().removeStickyBroadcast(lastUpdateBroadcast);
		}
		if (lastChangeBroadcast != null) {
			getApplicationContext().removeStickyBroadcast(lastChangeBroadcast);
		}
		
	}
	
	public void onCompletion(MediaPlayer mp) {
		nextSong();
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
	
	public void setSong(String artist, String album, String title, String dataPath) {
		try {
			this.artist = artist;
			this.album = album;
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
		
		isSongStarted = false;
	}

	public void startMusic() {
		if (!mp.isPlaying()) {
		    try {
				mp.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if (!isSongStarted) {
				lbm.startTrack(artist, album, title, mp.getDuration());
			}
			
		    mp.start();
			isSongStarted = true;
			spawnNotification();
			mHandler.postDelayed(mUpdateProgressTimeTask, 500);
		}
	}
	
	//I see this being the 2 vertical line button that pauses the song in the middle, allowing the user to continue play later
	public void pauseMusic() {
		mNotificationManager.cancel(1);
		mHandler.removeCallbacks(mUpdateProgressTimeTask);
		mp.pause();
		lbm.playbackPaused();
	}
	
	//I see this ending playback and returning the song to the beginning or something like that
	public void stopMusic() {
		isSongStarted = false;
		mNotificationManager.cancel(1);
		mHandler.removeCallbacks(mUpdateProgressTimeTask);
		mp.stop();
		mp.reset();
		stopNotify();
		lbm.playbackcomplete();
	}
	
	public void nextSong() {
		stopMusic();
		
		if (!songQueue.isEmpty()) {
			Song s = songQueue.poll();
			
			setSong(s.artist, s.album, s.title, s.datapath);
			
			// Delay start by a second
			new Handler().postDelayed(new Runnable() { 
				public void run() { 
					startMusic(); 
				}}, 1000);
		}
	}
	
	public void prevSong() {
		
	}
	
	public void enqueueFirst(String artist, String album, String title, String dataPath) {
		songQueue.addFirst(new Song(artist, album, title, dataPath));
		
		if (!mp.isPlaying() && !isSongStarted) {
			nextSong();
		}
		
		Toast.makeText(this, title + " " + getResources().getString(R.string.enqueue_first_success), Toast.LENGTH_SHORT).show();
	}
	
	public void enqueueLast(String artist, String album, String title, String dataPath) {
		songQueue.addLast(new Song(artist, album, title, dataPath));
		
		if (!mp.isPlaying() && !isSongStarted) {
			nextSong();
		}
		
		Toast.makeText(this, title + " " + getResources().getString(R.string.enqueue_last_success), Toast.LENGTH_SHORT).show();
	}
	
	public void seek(int position) {
		mp.seekTo(position);
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
		lastChangeBroadcast.putExtra("album", album);
		lastChangeBroadcast.putExtra("title", title);
		getApplicationContext().sendStickyBroadcast(lastChangeBroadcast);
	}
	
	private void stopNotify() {
		getApplicationContext().sendBroadcast(new Intent(SERVICE_STOP_NAME));
	}

	private void spawnNotification() {
		CharSequence contentTitle = title;
		String contentText = artist;
		
		Notification notification = new Notification(android.R.drawable.ic_media_play, contentText, System.currentTimeMillis());
		notification.flags = Notification.FLAG_NO_CLEAR
				| Notification.FLAG_ONGOING_EVENT;
		Context c = getApplicationContext();
		Intent notificationIntent = new Intent(this, com.uid.DroidDoesMusic.UI.Main.class);
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
	
	private static class LastfmBroadcaster {
		Context context;
		//SharedPreferences sp; 
		public LastfmBroadcaster(Context context) {
			this.context = context;
			//context.getApplicationContext()
			//sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		}
		
		public final void startTrack(String artist, String album, String track, int duration) {
			//if (sp.getBoolean("lastfm_scrobble", false)) {
				Intent i = new Intent("fm.last.android.metachanged");
				i.putExtra("artist", artist);
				i.putExtra("album", album);
				i.putExtra("track", track);
				i.putExtra("duration", duration);
				context.sendBroadcast(i);
			//}
		}
		
		public final void playbackPaused() {
			playbackPaused(0);
		}
		
		public final void playbackPaused(int resumeFrom) {
			//if (sp.getBoolean("lastfm_scrobble", false)) {
				Intent i = new Intent("fm.last.android.playbackpaused");
				if (resumeFrom > 0) {
					i.putExtra("position", (long)resumeFrom);
				}
				context.sendBroadcast(i);
			//}
		}
		
		public final void playbackcomplete() {
			//if (sp.getBoolean("lastfm_scrobble", false)) {
				context.sendBroadcast(new Intent("fm.last.android.playbackcomplete"));
			//}
		}
	}
	
	public class Song {
		public String artist;
		public String album;
		public String title;
		public String datapath;
		
		public Song(String artist, String album, String title, String datapath) {
			this.artist = artist;
			this.album = album;
			this.title = title;
			this.datapath = datapath;
		}
		
	}

	public Object[] getQueues() {
		return songQueue.toArray();
	}
	public Song[] getQueue() {
		
		return (Song[]) songQueue.toArray();
	}
}

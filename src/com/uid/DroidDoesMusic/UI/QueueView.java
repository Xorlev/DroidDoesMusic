package com.uid.DroidDoesMusic.UI;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uid.DroidDoesMusic.R;
import com.uid.DroidDoesMusic.player.Player;
import com.uid.DroidDoesMusic.player.Player.Song;


/**
 * This class should contain the songs in the queue 
 * 
 * @author Nick Hansen
 */

public class QueueView extends ListActivity {
	protected Player mPlayer;
    protected boolean isPlayerBound = false;
	protected static final String TAG = "DroidDoesMusic";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.library);
	}

	@Override
	public void onResume() {
        Log.d(TAG, getClass().getSimpleName() + ": onResume");
		super.onResume();
		
		Log.d(TAG, getClass().getSimpleName() + ": onResume: pre populate()");
        bind();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		unbindService(mConnection);
	}
	
	private void populate() {
		if (!isSdPresent()) {
        	TextView tv = (TextView)findViewById(android.R.id.empty);
        	tv.setText(getResources().getString(R.string.no_sd_card));
        } else {
        	try {
        		// TODO SectionListAdapter for current song, queue, and then playlist
				this.setListAdapter(new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1, mPlayer.getQueue()));
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
        }
	}

	public static boolean isSdPresent() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	private void bind() {
		try {
			getParent().bindService(new Intent("com.uid.DroidDoesMusic.player.Player"), mConnection, Context.BIND_AUTO_CREATE);
		} catch(NullPointerException e) {
			bindService(new Intent("com.uid.DroidDoesMusic.player.Player"), mConnection, Context.BIND_AUTO_CREATE);
		} 
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName classname, IBinder service) {
			Log.d(TAG, "onServiceConnected: Player Service Connected" + classname.toShortString());
			Player player = ((Player.DataBinder)service).getService();
			mPlayer = player;
			isPlayerBound = true;
			populate();
			getBaseContext().registerReceiver(trackChangeReceiver, new IntentFilter(com.uid.DroidDoesMusic.player.Player.SERVICE_CHANGE_NAME));
		}
		public void onServiceDisconnected(ComponentName classname) {
			Log.d(TAG, "onServiceDisconnected: Player Service Disconnected");
			isPlayerBound = false;
		}
	};
	
	private BroadcastReceiver trackChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context content, Intent intent) {
			populate();
		}
	};
}


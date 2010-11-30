package com.uid.DroidDoesMusic.UI;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
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
	private boolean populated = false;
	protected static final String TAG = "DroidDoesMusic";
	private Song[] mQueue;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        super.onCreate(savedInstanceState);
        bind();
        setContentView(R.layout.library);
	}

	@Override
	public void onResume() {
        Log.d(TAG, getClass().getSimpleName() + ": onResume");
		super.onResume();
		//mQueue = mPlayer.getQueue();
    	populate();
    	Log.d(TAG, "ZOMMMMMMMFFFFGGGGGG");
    	//Log.d(TAG, "ZOOOOOOMMMMMFFFFFFFFGGGGGGGG" + mPlayer.getQueue().toString());
    	//this.setListAdapter(new ArrayAdapter<Song>(this, android.R.layout.simple_list_item_1, mPlayer.getQueue()));
	
	}
	
	private void populate() {
		if (!isSdPresent()) {
        	TextView tv = (TextView)findViewById(android.R.id.empty);
        	tv.setText(getResources().getString(R.string.no_sd_card));
        }		
	}

	
	public void getData(String[] displayColumns, int[] display, String sort, int layout){
		populated = true;
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
	    	public void onServiceConnected(ComponentName classname, IBinder service){
	    		Log.d(TAG, "onServiceConnected: Player Service Connected" + classname.toShortString());
	    		
	    		Player player = ((Player.DataBinder)service).getService();
	    		mPlayer = player;
	    		    		
	    		isPlayerBound = true;
	    		
	    	}
	    	public void onServiceDisconnected(ComponentName classname){
	    		Log.d(TAG, "onServiceDisconnected: Player Service Disconnected");
	    		
	    		isPlayerBound = false;
	    	}
	    };
	}


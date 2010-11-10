package com.uid.DroidDoesMusic.UI;

import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

import com.uid.DroidDoesMusic.R;
import com.uid.DroidDoesMusic.player.Player;

public class Main extends TabActivity {      
	protected static final String TAG = "DroidDoesMusic";
	protected Player mPlayer;
    protected boolean isPlayerBound = false;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");

		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setupTabs();
        bind();
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
    	// Inflates menu items from resources
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	// Switch over options selected
    	switch (item.getItemId()) {
    	case R.id.settings:
    		startActivity(new Intent(this, Preferences.class));
    		return true;
    	}
    	return false;
    }
	
	public void setupTabs() {
		// Resource object for drawables
        Resources res = getResources();
        
        // TabHost from TabActivity
        TabHost tabHost = getTabHost();
        
        // TabSpec for reuse (object creation is expensive)
        TabHost.TabSpec spec;
        
        // Intent for reuse
        Intent intent;
        
        // Tab titles from resources
        String[] tabs = res.getStringArray(R.array.tabs);

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, Playlist.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        
        // Playlist
        spec = tabHost.newTabSpec("playlist")
        			  .setIndicator(tabs[0], res.getDrawable(R.drawable.ic_tab_playlist))
                      .setContent(intent);
        tabHost.addTab(spec);

        //
        // Library
        //
        
        // Artists
        intent = new Intent().setClass(this, Library.class);
        intent.putExtra("view", Library.ARTIST_VIEW);
        spec = tabHost.newTabSpec("library_artists")
        			  .setIndicator(tabs[1], res.getDrawable(R.drawable.ic_tab_playlist))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        // Albums
        intent = new Intent().setClass(this, Library.class);
        intent.putExtra("view", Library.ALBUM_VIEW);
        spec = tabHost.newTabSpec("library_albums")
        			  .setIndicator(tabs[2], res.getDrawable(R.drawable.ic_tab_playlist))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        // Songs
        intent = new Intent().setClass(this, Library.class);
        intent.putExtra("view", Library.SONG_VIEW);
        spec = tabHost.newTabSpec("library_songs")
        			  .setIndicator(tabs[3], res.getDrawable(R.drawable.ic_tab_playlist))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Set current tab to Artists tab
        tabHost.setCurrentTab(1);
	}
	
    private void bind() {
    	Log.d(TAG, "bind: Attempting to bind to Player" );
    	bindService(new Intent("com.uid.DroidDoesMusic.player.Player"), mConnection, Context.BIND_AUTO_CREATE);
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


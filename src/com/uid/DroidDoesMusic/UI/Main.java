package com.uid.DroidDoesMusic.UI;

import java.io.IOException;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

import com.uid.DroidDoesMusic.R;
import com.uid.DroidDoesMusic.player.Player;

public class Main extends TabActivity {      
	protected static final String TAG = "DroidDoesMusic";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, getClass().getSimpleName() + ": onCreate");
        
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

       // startService(new Intent("com.uid.DroidDoesMusic.player.Player"));
        setupTabs();
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

        // Library
        intent = new Intent().setClass(this, Library.class);
        spec = tabHost.newTabSpec("library")
        			  .setIndicator(tabs[1], res.getDrawable(R.drawable.ic_tab_playlist))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Now Playing
        intent = new Intent().setClass(this, NowPlaying.class);
        spec = tabHost.newTabSpec("now_playing")
                      .setIndicator(tabs[2], res.getDrawable(R.drawable.ic_tab_playlist))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Set current tab to Library
        tabHost.setCurrentTab(1);
	}
}


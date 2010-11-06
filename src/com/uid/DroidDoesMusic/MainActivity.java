package com.uid.DroidDoesMusic;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity {      
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setupTabs();
    }
	
	public void setupTabs() {
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab
        String[] tabs =  res.getStringArray(R.array.tabs);

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, PlaylistActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("playlist")
        			  .setIndicator(tabs[0], res.getDrawable(R.drawable.ic_tab_playlist))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, PlaylistActivity.class);
        spec = tabHost.newTabSpec("library")
        			  .setIndicator(tabs[1], res.getDrawable(R.drawable.ic_tab_playlist))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, PlaylistActivity.class);
        spec = tabHost.newTabSpec("now_playing")
                      .setIndicator(tabs[2], res.getDrawable(R.drawable.ic_tab_playlist))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(1);
	}
}


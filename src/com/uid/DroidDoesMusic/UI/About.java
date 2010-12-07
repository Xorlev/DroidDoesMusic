package com.uid.DroidDoesMusic.UI;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import com.uid.DroidDoesMusic.R;

public class About extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        TextView tv;
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        tv = (TextView)findViewById(R.id.about_version);
        
        ComponentName comp = new ComponentName(this, About.class);
        PackageInfo pinfo;
        
		try {
			pinfo = this.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
	        tv.setText("v" + pinfo.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
    	finish();
    	return true;
    }
}
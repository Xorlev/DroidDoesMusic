package com.uid.DroidDoesMusic.UI;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

import com.uid.DroidDoesMusic.R;
import com.uid.DroidDoesMusic.player.Player;

public class ControlView extends FrameLayout implements OnClickListener, OnDrawerOpenListener, OnDrawerCloseListener, OnSeekBarChangeListener {
	protected static final String TAG = "DroidDoesMusic";
	
	protected Player mPlayer;
    protected boolean isPlayerBound = false;
    
    private ImageButton prev;
    private ImageButton play;
    private ImageButton next;
    private SeekBar seek;
    private TextView text;
    private TextView lengthText;
	
	public ControlView(Context context) {
		super(context);
	}

	private void init() {
		ViewGroup.inflate(getContext(), R.layout.listen, this);
		
	    SlidingDrawer drawer = (SlidingDrawer) findViewById(R.id.drawer);
	    drawer.setOnDrawerOpenListener(this);
	    drawer.setOnDrawerCloseListener(this);
	    
	    prev = (ImageButton)findViewById(R.id.StreamPrevButton);
	    play = (ImageButton)findViewById(R.id.StreamPlayButton);
	    next = (ImageButton)findViewById(R.id.StreamNextButton);
	    seek = (SeekBar)findViewById(R.id.StreamProgressBar);
	    text = (TextView)findViewById(R.id.StreamTextView);
	    lengthText = (TextView) findViewById(R.id.StreamLengthText);
	    lengthText.setText("");
	    
	    prev.setOnClickListener(this);
	    play.setOnClickListener(this);
	    next.setOnClickListener(this);
	    seek.setOnSeekBarChangeListener(this);
	    
	    resetView();
	    bind();
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		init();
	}

	@Override
	protected void onDetachedFromWindow() {
		Log.d(TAG, "ListenBar: Window Detached");
		
		super.onDetachedFromWindow();

		getContext().unregisterReceiver(trackChangeReceiver);
		getContext().unregisterReceiver(trackUpdateReceiver);
		getContext().unregisterReceiver(trackStopReceiver);
		resetView();
	}
	
	private void resetView() {
	    prev.setEnabled(false);
	    play.setEnabled(false);
	    next.setEnabled(false);
	    seek.setEnabled(false);
	    seek.setProgress(0);
	    text.setText(R.string.msg_listen_nothing);
	    
	    play.setImageResource(android.R.drawable.ic_media_play);
	}
	
	public void onDrawerOpened() {
		ImageView arrow = (ImageView) findViewById(R.id.DrawerArrowImage);
		arrow.setImageResource(R.drawable.arrow_down);
	}
	
	public void onDrawerClosed() {
		ImageView arrow = (ImageView) findViewById(R.id.DrawerArrowImage);
		arrow.setImageResource(R.drawable.arrow_up);
	}

	public void onClick(View v) {
		ImageView iv = (ImageView)v;
		
		switch(v.getId()) {
		case R.id.StreamPlayButton:
			if (isPlayerBound && mPlayer.isSongStarted()) {
				if (mPlayer.isPlaying()) {
					mPlayer.pauseMusic();
					iv.setImageResource(android.R.drawable.ic_media_play);
				} else {
					mPlayer.startMusic();
					iv.setImageResource(android.R.drawable.ic_media_pause);
				}
			}
			return;
		}
	}
	
    private void bind() {
    	Log.d(TAG, "bind: Attempting to bind to Player" );
    	this.getContext().bindService(new Intent("com.uid.DroidDoesMusic.player.Player"), mConnection, Context.BIND_AUTO_CREATE);
    }
    
    private ServiceConnection mConnection = new ServiceConnection() {
    	public void onServiceConnected(ComponentName classname, IBinder service){
    		Log.d(TAG, "onServiceConnected: Player Service Connected" + classname.toShortString());
    		
    		Player player = ((Player.DataBinder)service).getService();
    		mPlayer = player;
    		    		
    		isPlayerBound = true;
    		
    	    getContext().registerReceiver(trackChangeReceiver, new IntentFilter(com.uid.DroidDoesMusic.player.Player.SERVICE_CHANGE_NAME));
    	    getContext().registerReceiver(trackUpdateReceiver, new IntentFilter(com.uid.DroidDoesMusic.player.Player.SERVICE_UPDATE_NAME));
    	    getContext().registerReceiver(trackStopReceiver,   new IntentFilter(com.uid.DroidDoesMusic.player.Player.SERVICE_STOP_NAME));
    	}
    	public void onServiceDisconnected(ComponentName classname){
    		Log.d(TAG, "onServiceDisconnected: Player Service Disconnected");
    		
    		isPlayerBound = false;
    	}
    };
    
	private BroadcastReceiver trackChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context content, Intent intent) {
	        
		    prev.setEnabled(true);
		    play.setEnabled(true);
		    next.setEnabled(true);
		    seek.setEnabled(true);
		    
		    if (mPlayer.isPlaying()) {
		    	play.setImageResource(android.R.drawable.ic_media_pause);
		    }
		    
		    String artist = intent.getExtras().getString("artist");
		    String album = intent.getExtras().getString("album");
		    String title = intent.getExtras().getString("title");
		    
		    text.setText(artist + " - " + title);
		}
	};
	
	private BroadcastReceiver trackUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context content, Intent intent) {	        
		    int duration = intent.getExtras().getInt("duration");
		    int position = intent.getExtras().getInt("position");
		    
		    seek.setMax(duration);
		    seek.setProgress(position);
		}
	};
	
	private BroadcastReceiver trackStopReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context content, Intent intent) {
	        Log.d(TAG, getClass().getSimpleName() + ": onReceive: " + intent.getData());
	        
		    resetView();

		}
	};

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			if (progress <= seekBar.getMax()) {
				mPlayer.seek(progress);
			} else {
				mPlayer.seek(seekBar.getMax());
			}
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
}

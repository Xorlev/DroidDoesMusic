package com.uid.DroidDoesMusic.UI;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;

import com.uid.DroidDoesMusic.R;

public class ListenBar extends FrameLayout implements OnClickListener, OnDrawerOpenListener, OnDrawerCloseListener {
	public ListenBar(Context context) {
		super(context);
	}

	private void init() {
		ViewGroup.inflate(getContext(), R.layout.listen, this);
		
	    SlidingDrawer drawer = (SlidingDrawer) findViewById(R.id.drawer);
	    drawer.setOnDrawerOpenListener(this);
	    drawer.setOnDrawerCloseListener(this);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		init();
	}
	
	public void onDrawerOpened() {
	  ImageView arrow = (ImageView) findViewById(R.id.DrawerArrowImage);
	  arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
	}
	
	public void onDrawerClosed() {
	  ImageView arrow = (ImageView) findViewById(R.id.DrawerArrowImage);
	  arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}

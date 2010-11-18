package com.uid.DroidDoesMusic.UI;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.uid.DroidDoesMusic.R;

public class ListenBar extends FrameLayout {
	public ListenBar(Context context) {
		super(context);
	}

	private void init() {
		ViewGroup.inflate(getContext(), R.layout.listen, this);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		init();
	}
	
	public boolean meh() {
		return true;
	}
}

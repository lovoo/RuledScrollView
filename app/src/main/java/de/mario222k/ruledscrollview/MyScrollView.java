package de.mario222k.ruledscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
	private ScrollView mScrollParent;
	private float oldy;


	public MyScrollView(Context context) {
		super(context);
		init();
	}

	public MyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {

	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				oldy = event.getY(); // set start y pos
				Log.d("XXX", "inner down");
				mScrollParent.requestDisallowInterceptTouchEvent(true);
				break;

			case MotionEvent.ACTION_MOVE:
				// get direction of touch
				int dir = (int) (oldy - event.getY());
				Log.d("XXX", "dir: " + dir);

				if (dir > 1) { // <= scroll down
					Log.d("XXX", "inner move: scroll down");
					// scroll down: always scroll parent down, then child
					if (mScrollParent.canScrollVertically(dir)) { // if parent can scroll down, let it. don't intercept.
						mScrollParent.requestDisallowInterceptTouchEvent(false);
					} else {
						if (canScrollVertically(dir)) {
							mScrollParent.requestDisallowInterceptTouchEvent(true); // if parent is at bottom, request intercept touch (so we can handle it)
						} else {
							mScrollParent.requestDisallowInterceptTouchEvent(false);
						}
					}
				} else if (dir < 1) { // <= scroll up
					Log.d("XXX", "inner move: scroll up");
					// scroll up : always scroll child up first
					if (canScrollVertically(dir)) {
						mScrollParent.requestDisallowInterceptTouchEvent(true);
					} else {
						mScrollParent.requestDisallowInterceptTouchEvent(false);
					}

				}
				oldy = event.getY(); // update old y
				break;

			case MotionEvent.ACTION_UP:
				Log.d("XXX", "inner up");
				mScrollParent.requestDisallowInterceptTouchEvent(false);
				break;

		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getActionMasked()) {
			// to have move actions always consumed (not delivered to childs, buttons, etc.)
			case MotionEvent.ACTION_MOVE:
				return true;
		}

		return super.onInterceptTouchEvent(ev);
	}

	public void setScrollParent(ScrollView scrollParent) {
		mScrollParent = scrollParent;
	}
}

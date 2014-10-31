package de.mario222k.ruledscrollview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;


public class VerticalScrollActivity extends Activity {

	private View.OnTouchListener mTouchListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_example);
		init();
	}

	private void init() {
		final ScrollView outer = (ScrollView) findViewById(R.id.outerScrollView);
		final ScrollView inner1 = (ScrollView) findViewById(R.id.innerScrollView1);
		final ScrollView inner2 = (ScrollView) findViewById(R.id.innerScrollView2);

		mTouchListener = new View.OnTouchListener() {
			public float oldy;
			private int dir;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {

					case MotionEvent.ACTION_DOWN:
						oldy = event.getY(); // set start y pos
						Log.d("XXX", "inner down");
						break;

					case MotionEvent.ACTION_MOVE:
						Log.d("XXX", "inner move");
						// get direction of touch
						dir = (int) (oldy - event.getY());

						// if we can scroll in this direction, please parent to not intercept event
						if (v.canScrollVertically(dir)) {
							outer.requestDisallowInterceptTouchEvent(true);
						} else {
							outer.requestDisallowInterceptTouchEvent(false);
						}
						break;

					case MotionEvent.ACTION_UP:
						Log.d("XXX", "inner up");
						break;
				}
				return false;
			}
		};

		inner1.setOnTouchListener(mTouchListener);
		inner2.setOnTouchListener(mTouchListener);
	}


	public void onButtonClick(View button) {
		Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
	}

}

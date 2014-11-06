package de.mario222k.ruledscrollview;

import android.app.Activity;
import android.os.Bundle;
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
		final MyScrollView inner1 = (MyScrollView) findViewById(R.id.innerScrollView1);
		inner1.setScrollParent(outer);

	}


	public void onButtonClick(View button) {
		Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
	}

}

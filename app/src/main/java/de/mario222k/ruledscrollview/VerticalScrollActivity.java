package de.mario222k.ruledscrollview;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class VerticalScrollActivity extends Activity {

    private RuledScrollView mScrollView;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_scroll);
        mScrollView = (RuledScrollView) findViewById(R.id.ruled_scroll_view);
    }


    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_example, menu);

        MenuItem item = menu.findItem(R.id.action_rule_overtakeTouch);
        item.setTitle(getString(R.string.action_rule_overtakeTouch, mScrollView.getRule().overtakeTouchAgain));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_rule_overtakeTouch) {
            mScrollView.setRule(new Rule(!mScrollView.getRule().overtakeTouchAgain));
            item.setTitle(getString(R.string.action_rule_overtakeTouch, mScrollView.getRule().overtakeTouchAgain));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

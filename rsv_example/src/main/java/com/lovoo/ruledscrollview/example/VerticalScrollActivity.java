package com.lovoo.ruledscrollview.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.lovoo.ruledscrollview.lib.Rule;
import com.lovoo.ruledscrollview.lib.RuledScrollView;

/**
 * Activity for Library-Example-Showcase.
 */
public class VerticalScrollActivity extends Activity {

    private RuledScrollView mScrollView;

    private View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick ( View v ) {
            showRuleConfigDialog((View) v.getParent());
            return true;
        }
    };

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_scroll);
        mScrollView = (RuledScrollView) findViewById(R.id.ruled_scroll_view);

        mScrollView.getChildAt(0).setOnLongClickListener(mLongClickListener);

        findViewById(R.id.inner_scroll_view_child).setOnLongClickListener(mLongClickListener);
    }


    @Override
    public boolean onCreateOptionsMenu ( Menu menu ) {
        getMenuInflater().inflate(R.menu.menu_example, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item ) {
        int id = item.getItemId();

        if (id == R.id.action_rule) {
            showRuleConfigDialog(mScrollView);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showRuleConfigDialog ( final View target ) {
        if (target != null) {
            @SuppressLint("InflateParams")
            final ViewGroup layout = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.layout_rule_dialog, null);

            Rule activeRule = Rule.getRuleFromView(target);
            final RadioGroup radioLeft = (RadioGroup) layout.findViewById(R.id.radio_group_left);
            radioLeft.check(getIdFromRule(activeRule.getRuleForDirection(Rule.DIRECTION.LEFT)));

            final RadioGroup radioTop = (RadioGroup) layout.findViewById(R.id.radio_group_top);
            radioTop.check(getIdFromRule(activeRule.getRuleForDirection(Rule.DIRECTION.UP)));

            final RadioGroup radioRight = (RadioGroup) layout.findViewById(R.id.radio_group_right);
            radioRight.check(getIdFromRule(activeRule.getRuleForDirection(Rule.DIRECTION.RIGHT)));

            final RadioGroup radioBottom = (RadioGroup) layout.findViewById(R.id.radio_group_bottom);
            radioBottom.check(getIdFromRule(activeRule.getRuleForDirection(Rule.DIRECTION.DOWN)));

            final AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("set Rule for: " + target.getClass().getSimpleName());
            b.setView(layout);
            b.setCancelable(false);
            b.setNeutralButton("Cancel", null);
            b.setPositiveButton("Activate", new DialogInterface.OnClickListener() {
                @Override
                public void onClick ( DialogInterface dialog, int which ) {
                    int left = getRuleFromId(radioLeft.getCheckedRadioButtonId());
                    int top = getRuleFromId(radioTop.getCheckedRadioButtonId());
                    int right = getRuleFromId(radioRight.getCheckedRadioButtonId());
                    int bottom = getRuleFromId(radioBottom.getCheckedRadioButtonId());

                    Rule.setRuleForView(target, new Rule(left, top, right, bottom));
                }
            });
            b.create().show();
        }
    }

    private int getIdFromRule ( int ruleHandle ) {
        switch (ruleHandle) {
            case Rule.RULE_HANDLE_NEVER:
                return R.id.radio_never;
            case Rule.RULE_HANDLE_ALWAYS:
                return R.id.radio_always;
            case Rule.RULE_HANDLE_IF_SCROLLABLE:
            default:
                return R.id.radio_scroll;
        }
    }

    private int getRuleFromId ( int id ) {
        switch (id) {
            case R.id.radio_never:
                return Rule.RULE_HANDLE_NEVER;
            case R.id.radio_always:
                return Rule.RULE_HANDLE_ALWAYS;
            case R.id.radio_scroll:
            default:
                return Rule.RULE_HANDLE_IF_SCROLLABLE;
        }
    }
}

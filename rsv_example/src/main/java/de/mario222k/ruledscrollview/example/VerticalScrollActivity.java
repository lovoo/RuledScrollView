package de.mario222k.ruledscrollview.example;

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

import de.mario222k.ruledscrollview.lib.Rule;
import de.mario222k.ruledscrollview.lib.RuledScrollView;

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

    private void showRuleConfigDialog( final View target) {
        if(target != null) {
            final ViewGroup layout = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.layout_rule_dialog, null);

            Rule activeRule = Rule.getRuleFromView(target);
            ((RadioGroup) layout.findViewById(R.id.radio_group_left)).check(getIdFromRule(activeRule.getRuleForDirection(Rule.RULE_DIRECTION_LEFT)));
            ((RadioGroup) layout.findViewById(R.id.radio_group_top)).check(getIdFromRule(activeRule.getRuleForDirection(Rule.RULE_DIRECTION_UP)));
            ((RadioGroup) layout.findViewById(R.id.radio_group_right)).check(getIdFromRule(activeRule.getRuleForDirection(Rule.RULE_DIRECTION_RIGHT)));
            ((RadioGroup) layout.findViewById(R.id.radio_group_bottom)).check(getIdFromRule(activeRule.getRuleForDirection(Rule.RULE_DIRECTION_DOWN)));

            final AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("set Rule for: " + target.getClass().getSimpleName());
            b.setView(layout);
            b.setCancelable(false);
            b.setNeutralButton("Cancel", null);
            b.setPositiveButton("Activate", new DialogInterface.OnClickListener() {
                @Override
                public void onClick ( DialogInterface dialog, int which ) {
                    int left = getRuleFromId(((RadioGroup) layout.findViewById(R.id.radio_group_left)).getCheckedRadioButtonId());
                    int top = getRuleFromId(((RadioGroup) layout.findViewById(R.id.radio_group_top)).getCheckedRadioButtonId());
                    int right = getRuleFromId(((RadioGroup) layout.findViewById(R.id.radio_group_right)).getCheckedRadioButtonId());
                    int bottom = getRuleFromId(((RadioGroup) layout.findViewById(R.id.radio_group_bottom)).getCheckedRadioButtonId());

                    Rule.setRuleForView(target, new Rule(left, top, right, bottom));
                }
            });
            b.create().show();
        }
    }

    private int getIdFromRule (int ruleHandle) {
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

    private int getRuleFromId (int id) {
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

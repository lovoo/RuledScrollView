package de.mario222k.ruledscrollview;

/**
 * Created by mariokreussel on 29.10.14.
 */

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class RuledScrollView extends ScrollView {

    private int mTouchSlop;

    public RuledScrollView ( Context context ) {
        this(context, null);
    }

    public RuledScrollView ( Context context, AttributeSet attrs ) {
        this(context, attrs, android.R.attr.scrollViewStyle);
    }

    public RuledScrollView ( Context context, AttributeSet attrs, int defStyle ) {
        super(context, attrs, defStyle);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private PointF mDownPoint = null;
    private int mActivePointerId = 0;
    @Override
    public boolean onInterceptTouchEvent ( MotionEvent ev ) {

        switch(ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // store event initial values
                mDownPoint = new PointF(ev.getX(), ev.getY());
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;

            case MotionEvent.ACTION_MOVE:
                if(mActivePointerId == -1) {
                    // invalid pointer id
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    // invalid touch event
                    return false;
                }
                if(mDownPoint != null) {
                    final float x = MotionEventCompat.getX(ev, pointerIndex);
                    final float y = MotionEventCompat.getY(ev, pointerIndex);
                    int dX = (int) (mDownPoint.x - x); // inverted delta (old - new value)
                    int dY = (int) (mDownPoint.y - y); // inverted delta (old - new value)
                    if(Math.abs(dY) > Math.abs(dX)) {
                        if(Math.abs(dY) > mTouchSlop) {
                            // intercept event only if scrollable
                            // TODO: lock if child is scrollable as well
                            return canScrollVertically(dY) && !oneChildCanScroll(getChildAt(0), 0, dY);
                        }
                    } else {
                        if(Math.abs(dX) > mTouchSlop) {
                            // intercept event only if scrollable
                            // TODO: lock if child is scrollable as well
                            return canScrollHorizontally(dX) && !oneChildCanScroll(getChildAt(0), 1, dX);
                        }
                    }

                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // check if a second pointer is active
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // reset event values
                mActivePointerId = -1;
                mDownPoint = null;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
//            mLastMotionY = MotionEventCompat.getY(ev, newPointerIndex);
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    private boolean oneChildCanScroll (View v, int axis, int direction) {
        if(v == null) {
            // invalid argument
            return false;

        } else if(axis == 0 && !v.canScrollVertically(direction)) {
            // check all view children
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for(int i=0; i<vg.getChildCount(); i++) {
                    if(vg.getChildAt(i).canScrollVertically(direction)) {
                        return true;
                    }
                }
                for(int i=0; i<vg.getChildCount(); i++) {
                    if(oneChildCanScroll(vg.getChildAt(i), axis, direction)) {
                        return true;
                    }
                }
            }
            return false;
        } else if(axis == 1 && !v.canScrollHorizontally(direction)) {
            // check all view children
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for(int i=0; i<vg.getChildCount(); i++) {
                    if(vg.getChildAt(i).canScrollHorizontally(direction)) {
                        return true;
                    }
                }
                for(int i=0; i<vg.getChildCount(); i++) {
                    if(oneChildCanScroll(vg.getChildAt(i), axis, direction)) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            // view can scroll
            return true;
        }
    }
}

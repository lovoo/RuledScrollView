package de.mario222k.ruledscrollview;

/**
 * Created by mariokreussel on 29.10.14.
 */

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class RuledScrollView extends ScrollView {

    //region members
    /**
     * member to store touch config information
     */
    private int mTouchSlop;
    /**
     * <0 : from up to down / from left to right
     * >0 : from down to up / from right to left
     */
    private int mTouchDirection = 0;
    /**
     * <0 : x-axis
     * >0 : y-axis
     */
    private int mTouchAxis = 0;
    private boolean mHasConsumedDown = false;
    /**
     * 0: unknown
     * +1: intercepted
     * -1: not intercepted
     */
    private int mInterceptMode = 0;

    /**
     * stored down event position
     */
    private PointF mDownPoint = null;

    /**
     * stored pointer id to support "finger walk scrolling"
     */
    private int mActivePointerId = 0;

    private Rule mRule = new Rule();;
    //endregion

    //region constructors
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
    //endregion

    //region touch handling

    /**
     * dispatchTouchEvent will cause an faked {@code ACTION_DOWN} event
     * if this view has:
     * - {@link Rule}.overtakeTouchAgain is {@code true}
     * - not intercepted move event
     * - not handled down event
     * - can scroll in dispatched direction
     * - no other child can scroll in dispatched direction
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public boolean dispatchTouchEvent ( @NonNull MotionEvent ev ) {

        if (mRule.overtakeTouchAgain && ev.getActionMasked() == MotionEvent.ACTION_MOVE && mInterceptMode < 0 && !mHasConsumedDown) {
            updateTouchDirection(ev);
            if (mTouchAxis > 0) {
                // intercept event only if scrollable
                if (canScrollVertically(mTouchDirection) && !oneChildCanScroll(getChildAt(0))) {
                    Log.w("TEST", "dispatch fake down event: " + ev.getX() + ", " + ev.getY());
                    MotionEvent fakeEvent = MotionEvent.obtain(ev);
                    fakeEvent.setAction(MotionEvent.ACTION_DOWN);
                    return super.dispatchTouchEvent(fakeEvent);
                }

            } else {
                // intercept event only if scrollable
                if (canScrollHorizontally(mTouchDirection) && !oneChildCanScroll(getChildAt(0))) {
                    Log.w("TEST", "dispatch fake down event: " + ev.getX() + ", " + ev.getY());
                    MotionEvent fakeEvent = MotionEvent.obtain(ev);
                    fakeEvent.setAction(MotionEvent.ACTION_DOWN);
                    return super.dispatchTouchEvent(fakeEvent);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * intercept touch when no view (children), except this can scroll
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public boolean onInterceptTouchEvent ( @NonNull MotionEvent ev ) {

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // store event initial values
                mInterceptMode = 0;
                mDownPoint = new PointF(ev.getX(), ev.getY());
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                Log.i("TEST", "intercept down even: " + ev.getX() + ", " + ev.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == -1) {
                    // invalid pointer id
                    mInterceptMode = 0;
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    // invalid touch event
                    mInterceptMode = 0;
                    return false;
                }
                if (mDownPoint != null) {
                    updateTouchDirection(ev);
                    if (mTouchAxis > 0) {
                        if (Math.abs(mTouchDirection) > mTouchSlop) {
                            // intercept event only if scrollable
                            mInterceptMode = (canScrollVertically(mTouchDirection) && !oneChildCanScroll(getChildAt(0))) ? 1 : -1;
                            Log.d("TEST", "intercept move event: " + mInterceptMode + "(" + ev.getX() + ", " + ev.getY() + ")");
                            return mInterceptMode > 0;
                        }
                    } else {
                        if (Math.abs(mTouchDirection) > mTouchSlop) {
                            // intercept event only if scrollable
                            mInterceptMode = (canScrollHorizontally(mTouchDirection) && !oneChildCanScroll(getChildAt(0))) ? 1 : -1;
                            Log.d("TEST", "intercept move event: " + mInterceptMode + "(" + ev.getX() + ", " + ev.getY() + ")");
                            return mInterceptMode > 0;
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
                mInterceptMode = 0;
                mActivePointerId = -1;
                mDownPoint = null;
                Log.e("TEST", "cancel");
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * overridden to remember if touch down event was originally handled by this view
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public boolean onTouchEvent ( @NonNull MotionEvent ev ) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mHasConsumedDown = super.onTouchEvent(ev);
                Log.d("TEST", "touch event consumedDown: " + mHasConsumedDown);
                return mHasConsumedDown;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mHasConsumedDown = false;
                //noinspection ConstantConditions
                Log.d("TEST", "touch event consumedDown: " + mHasConsumedDown);
        }
        return super.onTouchEvent(ev);
    }
    //endregion

    //region public methods
    public void setRule (Rule rule) {
        if(rule == null) {
            rule = new Rule();
        }
        mRule = rule;
    }
    public Rule getRule() {
        return mRule;
    }
    //endregion

    //region helper methods

    private void updateTouchDirection ( MotionEvent ev ) {
        if(mActivePointerId != -1) {
            final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
            if (pointerIndex >= 0) {
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                int dX = (int) (mDownPoint.x - x); // inverted delta (old - new value)
                int dY = (int) (mDownPoint.y - y); // inverted delta (old - new value)
                mTouchAxis = Math.abs(dY) - Math.abs(dX);
                if (mTouchAxis > 0) {
                    Log.v("TEST", "direction: " + mTouchDirection + " --> " + dY);
                    mTouchDirection = dY;
                } else {
                    Log.v("TEST", "direction: " + mTouchDirection + " --> " + dX);
                    mTouchDirection = dX;
                }
            }
        }
    }

    /**
     * store new primary pointer if first pointer was removed
     *
     * @param ev triggered POINTER_UP event
     */
    private void onSecondaryPointerUp ( @NonNull MotionEvent ev ) {
        if (mActivePointerId != -1) {
            final int pointerIndex = MotionEventCompat.getActionIndex(ev);
            final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
            if (pointerId == mActivePointerId) {
                // This was our active pointer going up. Choose a new
                // active pointer and adjust accordingly.
                final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            }
        }
    }

    /**
     * recursive method to determine if an child view can scroll
     *
     * @param v view and its children that will be checked
     * @return true if child or one of its children can scroll
     */
    protected boolean oneChildCanScroll ( View v ) {
        if (v == null) {
            // invalid argument
            return false;

        } else if (mTouchAxis > 0 && !v.canScrollVertically(mTouchDirection)) {
            // check all view children
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    if (vg.getChildAt(i).canScrollVertically(mTouchDirection)) {
                        return true;
                    }
                }
                for (int i = 0; i < vg.getChildCount(); i++) {
                    if (oneChildCanScroll(vg.getChildAt(i))) {
                        return true;
                    }
                }
            }
            return false;
        } else if (mTouchAxis < 0 && !v.canScrollHorizontally(mTouchDirection)) {
            // check all view children
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    if (vg.getChildAt(i).canScrollHorizontally(mTouchDirection)) {
                        return true;
                    }
                }
                for (int i = 0; i < vg.getChildCount(); i++) {
                    if (oneChildCanScroll(vg.getChildAt(i))) {
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
    //endregion
}

package de.mario222k.ruledscrollview.lib;

/**
 *
 * Created by mariokreussel on 29.10.14.
 */

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.ArrayList;

public class RuledScrollView extends ScrollView {

    private static final boolean LOG_ENABLED = false;

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

    /**
     * determine if isVisible() should do a parent check as well
     */
    private boolean mDoVisibleParentCheck = false;
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

    //region configuration

    /**
     * Configure how view visibility will be checked: only view or view and all its parents.
     * Set this to true if a ViewPager or ViewSwitcher is on of your children. Be aware that these classes wont switch View visibility!!!
     *
     * @param enableVisibleParentCheck {@code true} if parents should be checked as well, {@code false} if only view visible state is needed
     */
    public void setParentVisibleCheckEnabled ( boolean enableVisibleParentCheck ) {
        mDoVisibleParentCheck = enableVisibleParentCheck;
    }

    //endregion

    //region touch handling

    /**
     * dispatchTouchEvent will cause an faked {@code ACTION_DOWN} event
     * if this view has:
     * - not intercepted move event
     * - not handled down event
     * - can scroll in dispatched direction
     * - no other child can scroll in dispatched direction {@link Rule}
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public boolean dispatchTouchEvent ( @NonNull MotionEvent ev ) {

        if (ev.getActionMasked() == MotionEvent.ACTION_MOVE && mInterceptMode < 0 && !mHasConsumedDown) {
            updateTouchDirection(ev);
            // intercept event only if scrollable
            if (getInterceptionMode(ev) > 0) {
                return super.dispatchTouchEvent(getFakeDownEvent(ev));
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * create an fake down event with an touch offset equals to touch slop
     * to make touch handling seem less
     *
     * @param ev current event
     * @return faked down event
     */
    private MotionEvent getFakeDownEvent ( @NonNull MotionEvent ev ) {
        MotionEvent fakeEvent = MotionEvent.obtain(ev);
        fakeEvent.setAction(MotionEvent.ACTION_DOWN);
        int offset = (mTouchDirection > 0) ? mTouchSlop : -mTouchSlop;
        int offsetX = (mTouchAxis < 0) ? offset : 0;
        int offsetY = (mTouchAxis > 0) ? offset : 0;
        fakeEvent.offsetLocation(offsetX, offsetY);
        if (LOG_ENABLED) {
            Log.w(RuledScrollView.class.getSimpleName(), "dispatch fake down event: " + ev.getX() + " (" + offsetX + "), " + ev.getY() + " (" + offsetY + ")");
        }
        return fakeEvent;
    }

    /**
     * intercept touch move when no view (children) is allowed to scroll {@link Rule}
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
                if (LOG_ENABLED) {
                    Log.i(RuledScrollView.class.getSimpleName(), "intercept down even: " + ev.getX() + ", " + ev.getY());
                }
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
                    if (Math.abs(mTouchDirection) > mTouchSlop) {
                        // intercept event only if scrollable
                        mInterceptMode = getInterceptionMode(ev);
                        if (LOG_ENABLED) {
                            Log.d(RuledScrollView.class.getSimpleName(), "intercept move event: " + mInterceptMode + "(" + ev.getX() + ", " + ev.getY() + ")");
                        }
                        return mInterceptMode > 0;
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
                if (LOG_ENABLED) {
                    Log.e(RuledScrollView.class.getSimpleName(), "cancel");
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * helper method to determine if event should be intercepted
     *
     * @param ev current event
     * @return {@code -1} for no interception and {@code +1} for intercept
     */
    private int getInterceptionMode ( MotionEvent ev ) {
        int ruleDirection;

        if (mTouchAxis > 0) {
            boolean canSelfScroll = Rule.canViewScrollVertical(this, mTouchDirection);
            ruleDirection = (mTouchDirection > 0) ? Rule.RULE_DIRECTION_DOWN : Rule.RULE_DIRECTION_UP;
            if (canSelfScroll && Rule.ignoreChildrenForDirection(this, ruleDirection)) {
                return 1;
            } else if (oneChildCanScroll(this, (int) ev.getRawX(), (int) ev.getRawY())) {
                return -1;
            } else {
                return 1;
            }
        } else {
            boolean canSelfScroll = Rule.canViewScrollHorizontal(this, mTouchDirection);
            ruleDirection = (mTouchDirection > 0) ? Rule.RULE_DIRECTION_RIGHT : Rule.RULE_DIRECTION_LEFT;
            if (canSelfScroll && Rule.ignoreChildrenForDirection(this, ruleDirection)) {
                return 1;
            } else if (oneChildCanScroll(this, (int) ev.getRawX(), (int) ev.getRawY())) {
                return -1;
            } else {
                return 1;
            }

        }
    }

    Rect outRect = new Rect();

    /**
     * helper method to determine if event coordinates are in boundary of an view
     *
     * @param view that can handle event and should be checked
     * @param x    event raw x coordinate
     * @param y    event raw y coordinate
     * @return {@code true} if view bounds match coordinates
     */
    private boolean isInViewBounds ( View view, int x, int y ) {
        view.getGlobalVisibleRect(outRect);
        return outRect.contains(x, y);
    }

    /**
     * helper method to determine if view is visible
     *
     * @param view that should be checked
     * @return {@code true} if view is visible
     */
    private boolean isVisible ( View view ) {
        if (mDoVisibleParentCheck) {
            return view.isShown();
        } else {
            return view.getVisibility() == View.VISIBLE;
        }
    }

    /**
     * overridden to remember if touch down event was originally handled by this view and
     * to ignore touch move if rule is set {@link Rule}
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public boolean onTouchEvent ( @NonNull MotionEvent ev ) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mHasConsumedDown = super.onTouchEvent(ev);
                if (LOG_ENABLED) {
                    Log.d(RuledScrollView.class.getSimpleName(), "touch event begin consumedDown: " + mHasConsumedDown);
                }
                return mHasConsumedDown;

            case MotionEvent.ACTION_MOVE:
//                if(mHasConsumedDown) {
                updateTouchDirection(ev);
                boolean canScroll;
                if (mTouchAxis > 0) {
                    if (Math.abs(mTouchDirection) > mTouchSlop) {
                        canScroll = Rule.canViewScrollVertical(this, mTouchDirection);
                        if (LOG_ENABLED) {
                            Log.d(RuledScrollView.class.getSimpleName(), "touch event move vertical: " + canScroll);
                        }
                        if (!canScroll) {
                            dispatchTouchEvent(getFakeDownEvent(ev));
                        }
                        return canScroll && super.onTouchEvent(ev);
                    }
                } else {
                    if (Math.abs(mTouchDirection) > mTouchSlop) {
                        canScroll = Rule.canViewScrollHorizontal(this, mTouchDirection);
                        if (LOG_ENABLED) {
                            Log.d(RuledScrollView.class.getSimpleName(), "touch event move horizontal: " + canScroll);
                        }
                        if (!canScroll) {
                            dispatchTouchEvent(getFakeDownEvent(ev));
                        }
                        return canScroll && super.onTouchEvent(ev);
                    }
                }
//                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mHasConsumedDown = false;
                //noinspection ConstantConditions
                if (LOG_ENABLED) {
                    Log.d(RuledScrollView.class.getSimpleName(), "touch event finish consumedDown: " + mHasConsumedDown);
                }
        }
        return super.onTouchEvent(ev);
    }
    //endregion

    //region helper methods

    private void updateTouchDirection ( MotionEvent ev ) {
        if (mActivePointerId != -1) {
            final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
            if (pointerIndex >= 0) {
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                int dX = (int) (mDownPoint.x - x); // inverted delta (old - new value)
                int dY = (int) (mDownPoint.y - y); // inverted delta (old - new value)
                mTouchAxis = Math.abs(dY) - Math.abs(dX);
                if (mTouchAxis > 0) {
                    if (LOG_ENABLED) {
                        Log.v(RuledScrollView.class.getSimpleName(), "direction: " + mTouchDirection + " --> " + dY);
                    }
                    mTouchDirection = dY;
                } else {
                    if (LOG_ENABLED) {
                        Log.v(RuledScrollView.class.getSimpleName(), "direction: " + mTouchDirection + " --> " + dX);
                    }
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
     * recursive method to determine if an child view can scroll and event coordinates are withing its view boundaries
     *
     * @param vg   the ViewGroup's children will be checked
     * @param rawX event raw x coordinate
     * @param rawY event raw y coordinate
     * @return true if child or one of its children can scroll
     */
    protected boolean oneChildCanScroll ( ViewGroup vg, int rawX, int rawY ) {
        if (LOG_ENABLED) {
            Log.d(RuledScrollView.class.getSimpleName(), "vg: " + vg);
        }
        View child;
        ArrayList<ViewGroup> parents = new ArrayList<>();
        if (vg == null) {
            // invalid argument
            return false;

        } else {
            // check all view children as siblings
            for (int i = 0; i < vg.getChildCount(); i++) {
                child = vg.getChildAt(i);
                if (LOG_ENABLED) {
                    Log.d(RuledScrollView.class.getSimpleName(), " --> child: " + child);
                }
                if (isInViewBounds(child, rawX, rawY) && isVisible(child)) {
                    if((mTouchAxis < 0 && Rule.canViewScrollHorizontal(child, mTouchDirection)) ||
                       (mTouchAxis >= 0 && Rule.canViewScrollVertical(child, mTouchDirection))) {
                        return true;
                    } else if(child instanceof ViewGroup && ((ViewGroup) child).getChildCount() > 0) {
                        // only check parents that are visible and within view bounds
                        parents.add((ViewGroup) child);
                    }
                }
            }
        }

        // check all view children and their children (start recursion)
        for (ViewGroup parent : parents) {
            if(oneChildCanScroll(parent, rawX, rawY)) {
                return true;
            }
        }
        parents.clear();

        return false;
    }

//    private boolean viewHitsDownPoint (View view) {
//        if(view != null && mDownPoint != null) {
//
//        }
//    }
    //endregion
}

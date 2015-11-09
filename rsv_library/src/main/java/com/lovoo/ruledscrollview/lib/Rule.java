package com.lovoo.ruledscrollview.lib;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Model to determine how {@link RuledScrollView} will handle {@link android.view.MotionEvent} for one {@link View}.
 * Created by mariokreussel on 01.11.14.
 */
public class Rule {

    /**
     * Touch directions for rules.
     */
    public enum DIRECTION {
        LEFT, UP, RIGHT, DOWN
    }

    /**
     * If set to an child of {@link RuledScrollView}: child will never get an touch move event.
     * If set to {@link RuledScrollView}: normal intercept handling.
     */
    public static final int RULE_HANDLE_NEVER = 0x0000;
    /**
     * If set to an child of {@link RuledScrollView}: child will keep touch event even if border reached.
     * If set to {@link RuledScrollView}: children will never get an touch move event.
     */
    public static final int RULE_HANDLE_ALWAYS = 0x0001;
    /**
     * If set to an child of {@link RuledScrollView}: child will delegate touch event if its border is reached.
     * If set to {@link RuledScrollView}: normal intercept handling.
     */
    public static final int RULE_HANDLE_IF_SCROLLABLE = 0x0002;
    /**
     * If set to an child of {@link RuledScrollView}: child will delegate touch event if its border is reached.
     * If set to {@link RuledScrollView}: normal intercept handling.
     */
    public static final int RULE_HANDLE_IGNORE_CHILDREN = 0x0004;

    private static final int RULE_CONFIG_SHIFT = 3;
    private static final int RULE_CONFIG_MASK = 0x0007;

    private int[] mDirectionFlags;

    /**
     * Method to setup a view with a rule.
     *
     * @param view target view that will receive this rule
     * @param rule rule that will be used
     */
    public static void setRuleForView ( View view, Rule rule ) {
        if (view != null && rule != null) {
            view.setTag(R.id.ruled_scroll_view_config_tag, rule.exportConfig());
        }
    }

    /**
     * Method to get a rule from a view.
     *
     * @param view target view
     * @return rule that was set or an empty rule
     */
    public static Rule getRuleFromView ( View view ) {
        int config = 0;
        if (view != null) {
            Object value = view.getTag(R.id.ruled_scroll_view_config_tag);
            if (value != null) {
                config = (Integer) value;
            }
        }
        return new Rule(config);
    }

    /**
     * Check if deeper layout checks can be skipped.
     *
     * @param view      target view that rule will be checked
     * @param direction currently used move direction
     * @return {@code true} if current rule is {@code RULE_HANDLE_ALWAYS} || {@code RULE_HANDLE_IGNORE_CHILDREN}, {@code false} otherwise
     */
    public static boolean ignoreChildrenForDirection ( View view, DIRECTION direction ) {
        Rule rule = getRuleFromView(view);
        return (rule.getRuleForDirection(direction) & RULE_HANDLE_ALWAYS) > 0
                || (rule.getRuleForDirection(direction) & RULE_HANDLE_IGNORE_CHILDREN) > 0;
    }

    /**
     * Check if this {@link View} can scroll left or right.
     *
     * @param view                target view that will be checked
     * @param leftRightDifference currently used move direction (startPoint.X - currentPosition.X)
     * @return {@code true} if current rule allows scroll in this direction, {@code false} otherwise
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public static boolean canViewScrollHorizontal ( View view, int leftRightDifference ) {
        Rule rule = getRuleFromView(view);

        DIRECTION direction = null;
        if (leftRightDifference < 0) {
            direction = DIRECTION.LEFT;
        } else if (leftRightDifference > 0) {
            direction = DIRECTION.RIGHT;
        }

        int mode = rule.getRuleForDirection(direction);
        if ((mode & RULE_HANDLE_ALWAYS) > 0) {
            return true;
        } else if ((mode & RULE_HANDLE_ALWAYS) == 0) {
            // RULE_HANDLE_NEVER active
            return false;
        }

        return view.canScrollHorizontally(leftRightDifference);
    }

    /**
     * Check if this {@link View} can scroll up or down.
     *
     * @param view             target view that will be checked
     * @param upDownDifference currently used move direction (startPoint.Y - currentPosition.Y)
     * @return {@code true} if current rule allows scroll in this direction, {@code false} otherwise
     */
    public static boolean canViewScrollVertical ( View view, int upDownDifference ) {
        Rule rule = getRuleFromView(view);

        DIRECTION direction = null;
        if (upDownDifference < 0) {
            direction = DIRECTION.UP;
        } else if (upDownDifference > 0) {
            direction = DIRECTION.DOWN;
        }

        int mode = rule.getRuleForDirection(direction);
        switch (mode) {
            case RULE_HANDLE_ALWAYS:
                return true;
            case RULE_HANDLE_NEVER:
                return false;
            case RULE_HANDLE_IF_SCROLLABLE:
            default:
                return view.canScrollVertically(upDownDifference);
        }
    }

    /**
     * Default constructor.
     */
    public Rule () {
        clear();
    }

    /**
     * Constructor for exported values.
     *
     * @param exportedRule value from {@code Rule.exportConfig()}
     */
    public Rule ( int exportedRule ) {
        this();
        importConfig(exportedRule);
    }

    /**
     * Constructor to setup each direction.
     *
     * @param left  rule for left
     * @param up    rule for up
     * @param right rule for right
     * @param down  rule for down
     */
    public Rule ( int left, int up, int right, int down ) {
        this();
        mDirectionFlags[DIRECTION.LEFT.ordinal()] = left;
        mDirectionFlags[DIRECTION.UP.ordinal()] = up;
        mDirectionFlags[DIRECTION.RIGHT.ordinal()] = right;
        mDirectionFlags[DIRECTION.DOWN.ordinal()] = down;
    }

    /**
     * Helps to store current rule.
     *
     * @return integer with active rule
     */
    public int exportConfig () {
        int config = 0;
        for (int i = 0; i < 4; i++) {
            config |= ((mDirectionFlags[i] & RULE_CONFIG_MASK) << (i * RULE_CONFIG_SHIFT));
        }
        return config;
    }

    /**
     * Helps to restore an exported value.
     *
     * @param config value that was exported
     */
    public void importConfig ( int config ) {
        for (int i = 0; i < 4; i++) {
            mDirectionFlags[i] = (config >> (i * RULE_CONFIG_SHIFT)) & RULE_CONFIG_MASK;
        }
    }

    /**
     * Setter for one direction.
     *
     * @param ruleHandle    new rule handle
     * @param ruleDirection direction that will be overwritten
     */
    @SuppressWarnings("unused")
    public void setRule ( int ruleHandle, @NonNull DIRECTION ruleDirection ) {
        mDirectionFlags[ruleDirection.ordinal()] = ruleHandle;
    }

    /**
     * Setter for all direction.
     *
     * @param ruleHandle new rule handle for all directions
     */
    @SuppressWarnings("unused")
    public void setRuleForAllDirections ( int ruleHandle ) {
        for (int i = 0; i < 4; i++) {
            mDirectionFlags[i] = ruleHandle;
        }
    }

    /**
     * Get configuration for specified scroll direction.
     *
     * @param direction requested direction
     * @return active rule for given direction or {@code RULE_HANDLE_NEVER}
     */
    public int getRuleForDirection ( @Nullable DIRECTION direction ) {
        if (direction == null) {
            return 0;
        }
        int ordinal = direction.ordinal();
        if (ordinal < 0 || ordinal >= 4) {
            return 0;
        }

        return mDirectionFlags[ordinal];
    }

    /**
     * Reset all directions with {@code RULE_HANDLE_NEVER}.
     */
    public void clear () {
        mDirectionFlags = new int[4];
    }
}

package de.mario222k.ruledscrollview.lib;

import android.view.View;

/**
 * Created by mariokreussel on 01.11.14.
 */
public class Rule {

    /**
     * touch direction left
     */
    public final static int RULE_DIRECTION_LEFT   = 0;
    /**
     * touch direction up
     */
    public final static int RULE_DIRECTION_UP = 1;
    /**
     * touch direction right
     */
    public final static int RULE_DIRECTION_RIGHT  = 2;
    /**
     * touch direction down
     */
    public final static int RULE_DIRECTION_DOWN = 3;

    /**
     * if set to an child of {@link RuledScrollView}: child will never get an touch move event
     * if set to {@link RuledScrollView}: normal intercept handling
     */
    public final static int RULE_HANDLE_NEVER = 0x0000;
    /**
     * if set to an child of {@link RuledScrollView}: child will keep touch event even if border reached
     * if set to {@link RuledScrollView}: children will never get an touch move event
     */
    public final static int RULE_HANDLE_ALWAYS = 0x0001;
    /**
     * if set to an child of {@link RuledScrollView}: child will delegate touch event if its border is reached
     * if set to {@link RuledScrollView}: normal intercept handling
     */
    public final static int RULE_HANDLE_IF_SCROLLABLE = 0x0002;
    /**
     * if set to an child of {@link RuledScrollView}: child will delegate touch event if its border is reached
     * if set to {@link RuledScrollView}: normal intercept handling
     */
    public final static int RULE_HANDLE_IGNORE_CHILDREN = 0x0004;

    private final static int RULE_CONFIG_SHIFT  = 3;
    private final static int RULE_CONFIG_MASK   = 0x0007;

    /**
     * method to setup a view with a rule
     * @param view target view that will receive this rule
     * @param rule rule that will be used
     */
    public static void setRuleForView(View view, Rule rule) {
        if(view != null && rule != null) {
            view.setTag(R.id.ruled_scroll_view_config_tag, rule.exportConfig());
        }
    }

    /**
     * method to get a rule from a view
     * @param view target view
     * @return rule that was set or an empty rule
     */
    public static Rule getRuleFromView(View view) {
        int config = 0;
        if(view != null) {
            Object value = view.getTag(R.id.ruled_scroll_view_config_tag);
            if(value != null) {
                config = (Integer) value;
            }
        }
        return new Rule(config);
    }

    /**
     * @param view target view that rule will be checked
     * @param direction currently used move direction
     * @return {@code true} if current role is {@code RULE_HANDLE_ALWAYS} || {@code RULE_HANDLE_IGNORE_CHILDREN} , {@code false} otherwise
     */
    public static boolean ignoreChildrenForDirection ( View view, int direction ) {
        Rule rule = getRuleFromView(view);
        return (rule.getRuleForDirection(direction) & RULE_HANDLE_ALWAYS) > 0 ||
                (rule.getRuleForDirection(direction) & RULE_HANDLE_IGNORE_CHILDREN) > 0;
    }

    /**
     * @param view target view that will be checked
     * @param leftRight currently used move direction
     * @return {@code true} if current role allows scroll in this direction, {@code false} otherwise
     */
    public static boolean canViewScrollHorizontal(View view, int leftRight) {
        Rule rule = getRuleFromView(view);

        int direction = -1;
        if(leftRight < 0) {
            direction = RULE_DIRECTION_LEFT;
        } else if(leftRight > 0) {
            direction = RULE_DIRECTION_RIGHT;
        }

        int mode = rule.getRuleForDirection(direction);
        if((mode & RULE_HANDLE_ALWAYS) > 0) {
            return true;
        } else if((mode & RULE_HANDLE_ALWAYS) == 0) {
            // RULE_HANDLE_NEVER active
            return false;
        } else {
            return view.canScrollHorizontally(leftRight);
        }
    }

    /**
     * @param view target view that will be checked
     * @param upDown currently used move direction
     * @return {@code true} if current role allows scroll in this direction, {@code false} otherwise
     */
    public static boolean canViewScrollVertical(View view, int upDown) {
        Rule rule = getRuleFromView(view);

        int direction = -1;
        if(upDown < 0) {
            direction = RULE_DIRECTION_UP;
        } else if(upDown > 0) {
            direction = RULE_DIRECTION_DOWN;
        }

        int mode = rule.getRuleForDirection(direction);
        switch(mode) {
            case RULE_HANDLE_ALWAYS:
                return true;
            case RULE_HANDLE_NEVER:
                return false;
            case RULE_HANDLE_IF_SCROLLABLE:
            default:
                return view.canScrollVertically(upDown);
        }
    }

    private int[] mDirectionFlags;

    /**
     * default constructor
     */
    public Rule () {
        clear();
    }

    /**
     * constructor for exported values
     * @param exportedRule value from {@code Rule.exportConfig()}
     */
    public Rule (int exportedRule) {
        this();
        importConfig(exportedRule);
    }

    /**
     * constructor to setup each direction
     * @param left rule for left
     * @param up rule for up
     * @param right rule for right
     * @param down rule for down
     */
    public Rule (int left, int up, int right, int down) {
        this();
        mDirectionFlags[RULE_DIRECTION_LEFT] = left;
        mDirectionFlags[RULE_DIRECTION_UP] = up;
        mDirectionFlags[RULE_DIRECTION_RIGHT] = right;
        mDirectionFlags[RULE_DIRECTION_DOWN] = down;
    }

    /**
     * helps to store current role
     * @return integer with active role
     */
    public int exportConfig () {
        int config = 0;
        for(int i=0; i<4; i++) {
            config |= ((mDirectionFlags[i] & RULE_CONFIG_MASK) << (i * RULE_CONFIG_SHIFT));
        }
        return config;
    }

    /**
     * helps to restore an exported value
     * @param config value that was exported
     */
    public void importConfig (int config) {
        for(int i=0; i<4; i++) {
            mDirectionFlags[i] = (config >> (i * RULE_CONFIG_SHIFT)) & RULE_CONFIG_MASK;
        }
    }

    /**
     * setter for one direction
     * @param ruleHandle new rule handle
     * @param ruleDirection direction that will be overwritten
     */
    public void setRule (int ruleHandle, int ruleDirection) {
        mDirectionFlags[ruleDirection] = ruleHandle;
    }

    /**
     * setter for all direction
     * @param ruleHandle new rule handle for all directions
     */
    public void setRuleForAllDirections (int ruleHandle) {
        for(int i=0; i<4; i++) {
            mDirectionFlags[i] = ruleHandle;
        }
    }

    /**
     * @param direction requested direction
     * @return active rule for given direction or {@code 0}
     */
    public int getRuleForDirection (int direction) {
        if(direction >= 0 && direction < 4) {
            return mDirectionFlags[direction];
        }
        return 0;
    }

    /**
     * reset all directions with {@code 0}
     */
    public void clear () {
        mDirectionFlags = new int[4];
    }
}

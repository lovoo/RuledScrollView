package de.mario222k.ruledscrollview;

/**
 * Created by mariokreussel on 01.11.14.
 */
public class Rule {
    final boolean overtakeTouchAgain;

    public Rule () {
        this.overtakeTouchAgain = false;
    }

    public Rule (boolean overtakeTouchAgain) {
        this.overtakeTouchAgain = overtakeTouchAgain;
    }
}

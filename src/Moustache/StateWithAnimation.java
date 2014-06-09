package Moustache;

import Common.RootMachine;
import Common.State;
import processing.core.PApplet;

public class StateWithAnimation extends StateWithInterface {

    StateWithAnimation(PApplet applet, String name) {
        super(applet, name);
    }

    public void onStateChange(State state) {

        if (state != null && state.getStateMachine() != RootMachine.getInstance()) {
            RootMachine.getInstance().find("animation").invokeSetMethod(this, state);
        }
    }
}

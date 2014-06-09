package Common;

import processing.core.PApplet;
import processing.core.PGraphics;

public class State implements StateListener {

    private String name;
    private StateMachine stateMachine;

    protected PApplet applet;

    public StateMachine getStateMachine() { return stateMachine; }
    public void setStateMachine(StateMachine stateMachine) { this.stateMachine = stateMachine; }
    public String getName() { return name; }

    public State(PApplet applet, String name) {

        this.applet = applet;
        this.name = name;
    }

    public void onInternalStateRender(PGraphics g) {}
    public void invokeSetMethod(State left, State right) {}

    public void onStateAttach() {}
    public void onStateDetach() {}
    public void onStateRender(PGraphics g) {}
    public void onStateTick() {}
    public void onStateChange(State state) {}
    public void onStateLoad() {}
    public void onStateUnLoad() {}
    public void onStateMouseClick() {}
    public void onStateMouseDown() {}
    public void onStateMouseUp() {}
    public void onStateMouseMove() {}
    public void onStateMouseDrag() {}
    public void onStateMouseEnter() {}
    public void onStateMouseLeave() {}
    public void onStateKeyDown() {}
    public void onStateKeyUp() {}
    public void onStateKeyType() {}
    public void onStateWheelRotate() {}
}

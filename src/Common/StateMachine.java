package Common;

import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class StateMachine {

    private ArrayList<State> stateList = new ArrayList<State>();
    private Stack<State> stateStack = new Stack<State>();

    private State currentState;
    private boolean isStatePaused = false;
    private PApplet delegate;

    public ArrayList<State> getStateList() { return stateList; }
    public State getCurrentState() { return currentState; }
    public State getLastState() { return stateStack.lastElement(); }
    public PApplet getApplet() { return delegate; }
    public State popLastState() { return stateStack.pop(); }

    public StateMachine(PApplet applet) {
        delegate = applet;
    }

    public State attach(State state) {

        if (stateList.contains(state)) {
            return state;
        }

        state.setStateMachine(this);

        stateList.add(state);

        if (currentState == null) {
            change(state);
        }

        state.onStateAttach();

        return state;
    }

    public State detach(State state) {

        if (!stateList.contains(state) || state == null) {
            return state;
        }

        state.onStateDetach();
        stateList.remove(state);

        return state;
    }

    public State detach(String name) {
        return detach(find(name));
    }

    public State find(String name) {

        for (State s : stateList) {
            if (s.getName() == name) {
                return s;
            }
        }

        return null;
    }

    public void pause() {
        isStatePaused = true;
    }

    public void resume() {
        isStatePaused = false;
    }

    public boolean invokeRenderMethod(PGraphics g) {

        if (currentState == null || isStatePaused) {
            return false;
        }

        currentState.onStateTick();
        currentState.onStateRender(g);
        currentState.onInternalStateRender(g);

        return true;
    }

    public boolean change(String name) {
        return change(find(name));
    }

    public boolean change(State state) {

        if (currentState != null) {

            currentState.onStateChange(state);
            currentState.onStateUnLoad();

            if (this != RootMachine.getInstance()) {
                stateStack.push(currentState);
            }
        }

        currentState = state;

        if (currentState != null) {
            currentState.onStateLoad();
        }

        return true;
    }

    public boolean next() {

        if (currentState == null) {
            return false;
        }

        State state = null;
        Iterator<State> i = stateList.iterator();

        while (i.hasNext ()) {

            if ((state = i.next()) == currentState && i.hasNext()) {
                state = i.next();
                break;
            }
        }

        if (state == currentState) {
            return false;
        }

        return change(state);
    }

    public boolean prev() {

        if (currentState == null) {
            return false;
        }

        State state = null;
        State prev = null;

        Iterator<State> i = stateList.iterator();

        while (i.hasNext ()) {

            if ((state = i.next()) == currentState) {
                if (prev == null) {
                    state = currentState;
                } else {
                    state = prev;
                }
                break;
            }

            prev = state;
        }

        if (state == currentState) {
            return false;
        }

        return change(state);
    }

    public void onMouseClick() {

        if (currentState != null) {
            currentState.onStateMouseClick();
        }
    }

    public void onMouseDown() {

        if (currentState != null) {
            currentState.onStateMouseDown();
        }
    }

    public void onMouseUp() {

        if (currentState != null) {
            currentState.onStateMouseUp();
        }
    }

    public void onMouseMove() {

        if (currentState != null) {
            currentState.onStateMouseMove();
        }
    }

    public void onMouseDrag() {

        if (currentState != null) {
            currentState.onStateMouseDrag();
        }
    }

    public void onMouseEnter() {

        if (currentState != null) {
            currentState.onStateMouseEnter();
        }
    }

    public void onMouseLeave() {

        if (currentState != null) {
            currentState.onStateMouseLeave();
        }
    }

    public void onKeyDown() {

        if (currentState != null) {
            currentState.onStateKeyDown();
        }
    }

    public void onKeyUp() {

        if (currentState != null) {
            currentState.onStateKeyUp();
        }
    }

    public void onKeyType() {

        if (currentState != null) {
            currentState.onStateKeyType();
        }
    }

    public void onWheelRotate() {

        if (currentState != null) {
            currentState.onStateWheelRotate();
        }
    }
}

package Common;

import processing.core.PGraphics;

public interface StateListener {

    public void invokeSetMethod(State left, State right);
    public void onInternalStateRender(PGraphics g);

    public void onStateAttach();
    public void onStateDetach();
    public void onStateRender(PGraphics g);
    public void onStateTick();
    public void onStateChange(State state);
    public void onStateLoad();
    public void onStateUnLoad();
    public void onStateMouseClick();
    public void onStateMouseDown();
    public void onStateMouseUp();
    public void onStateMouseMove();
    public void onStateMouseDrag();
    public void onStateMouseEnter();
    public void onStateMouseLeave();
    public void onStateKeyDown();
    public void onStateKeyUp();
    public void onStateKeyType();
    public void onStateWheelRotate();
}

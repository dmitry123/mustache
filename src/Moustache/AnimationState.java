package Moustache;

import processing.core.PGraphics;
import Common.State;
import Common.Vertex;
import processing.core.PApplet;

public class AnimationState extends State {

    public AnimationState(PApplet applet) {
        super(applet, "animation");
    }

    // left and right animated states
    private State leftState;
    private State rightState;

    // center position
    private Vertex position = new Vertex();

    // velocity and animation flag
    private float velocity;
    private Direction type;

    public enum Direction {
        Left,
        Right,
        Top,
        Bottom
    }

    public void invokeSetMethod(State left, State right) {
        invokeSetMethod(left, right, Direction.values() [(int) applet.random(0, Direction.values().length)]);
    }

    public void invokeSetMethod(State left, State right, Direction type) {

        // copy states
        leftState = left;
        rightState = right;

        // set default position
        position.x = 0;
        position.y = 0;

        // copy type
        this.type = type;

        // compute velocity
        if (type == Direction.Left || type == Direction.Right) {
            velocity = applet.width / 20;
        } else {
            velocity = applet.height / 20;
        }

        // change state to current (animation)
        getStateMachine().change(this);
    }

    public void onStateTick() {

        final float friction = 0.952f;

        // compute position for 4 variants
        // left, right, bottom and top
        switch (type)
        {
            case Left:
                position.x += velocity;
                break;
            case Right:
                position.x -= velocity;
                break;
            case Top:
                position.y -= velocity;
                break;
            case Bottom:
                position.y += velocity;
                break;
        }

        velocity *= friction;

        // check animation type and check for
        // end, if true, then goto main state
        if (type == Direction.Left || type == Direction.Right) {
            if (Math.abs(position.x) >= applet.width) {
                getStateMachine().change(getStateMachine().find("main"));
            }
        } else {
            if (Math.abs(position.y) >= applet.height) {
                getStateMachine().change(getStateMachine().find("main"));
            }
        }
    }

    public void onStateRender(PGraphics g) {

        int clr;

        if (type == Direction.Left || type == Direction.Right) {
            clr = Math.abs((int)((position.x / applet.width) * 0xff));
        } else {
            clr = Math.abs((int)(position.y / applet.height * 0xff));
        }

        // invoke onStateRender event or
        // catch an exception (for left state)
        g.pushMatrix();
        g.translate(position.x, position.y);
        leftState.onStateRender(g);
        leftState.onInternalStateRender(g);
        g.popMatrix();
        g.noStroke();
        g.fill(0xcc, 0xcc, 0xcc, clr);
        g.rect(0, 0, applet.width, applet.height);

        // translate matrix for second state
        // and render it or catch an exception
        g.pushMatrix();
        switch (type)
        {
            case Left:
                g.translate(position.x - applet.width, position.y);
                break;
            case Right:
                g.translate(position.x + applet.width, position.y);
                break;
            case Top:
                g.translate(position.x, position.y + applet.height);
                break;
            case Bottom:
                g.translate(position.x, position.y - applet.height);
                break;
        }
        rightState.onStateRender(g);
        rightState.onInternalStateRender(g);
        g.popMatrix();

        g.fill(0xcc, 0xcc, 0xcc, 0xff - clr);
        g.rect(0, 0, applet.width, applet.height);
    }
}

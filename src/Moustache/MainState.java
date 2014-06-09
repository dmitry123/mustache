package Moustache;

import Common.*;
import processing.core.PApplet;
import processing.core.PGraphics;

public class MainState extends State {

    public MainState(PApplet applet) {
        super(applet, "main");
    }

    public StateMachine machine;

    public void onStateAttach() {

        machine = new StateMachine(applet);

        // attach this state, which shows
        // main menu and checks mouse events
        machine.attach(new MenuState(applet));
        machine.attach(new GameState(applet));
        machine.attach(new EditorState(applet));
        machine.attach(new TakePictureState(applet, new UserInfo(applet)));
    }

    public void onStateRender(PGraphics g) {
        machine.invokeRenderMethod(g);
    }

    public void onStateMouseClick() { machine.onMouseClick(); }
    public void onStateMouseDown() { machine.onMouseDown(); }
    public void onStateMouseUp() { machine.onMouseUp(); }
    public void onStateMouseMove() { machine.onMouseMove(); }
    public void onStateMouseDrag() { machine.onMouseDrag(); }
    public void onStateMouseEnter() { machine.onMouseEnter(); }
    public void onStateMouseLeave() { machine.onMouseLeave(); }
    public void onStateKeyDown() { machine.onKeyDown(); }
    public void onStateKeyUp() { machine.onKeyUp(); }
    public void onStateKeyType() { machine.onKeyType(); }
}

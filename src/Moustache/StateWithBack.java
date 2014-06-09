package Moustache;

import Interface.InterfaceDelegate;
import Interface.InterfaceMenu;
import processing.core.PApplet;
import processing.core.PGraphics;

public class StateWithBack extends StateWithAnimation {

    private InterfaceMenu backInterfaceMenu;

    public StateWithBack(PApplet applet, String name) {

        super(applet, name);

        backInterfaceMenu = new InterfaceMenu(applet, getInterfaceEventListener(), 0, 0, 150, 40);

        backInterfaceMenu.attach("Back").setInterfaceListener(new InterfaceDelegate() {
            public void onInterfaceMouseClick() {
                getStateMachine().change(getStateMachine().popLastState());
            }
        });
    }

    public void onInternalStateRender(PGraphics g) {
        backInterfaceMenu.invokeRenderMethod(g);
    }
}

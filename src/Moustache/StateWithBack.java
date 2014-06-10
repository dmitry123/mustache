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

        backInterfaceMenu.attach("Exit").setInterfaceListener(new InterfaceDelegate() {
            public void onInterfaceMouseClick() {
                System.exit(0);
            }
        });
    }

    public void onInternalStateRender(PGraphics g) {
        backInterfaceMenu.invokeRenderMethod(g);
    }
}

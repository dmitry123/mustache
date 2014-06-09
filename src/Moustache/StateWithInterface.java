package Moustache;

import Common.RootMachine;
import Common.State;
import Interface.InterfaceEventListener;
import processing.core.PApplet;

public class StateWithInterface extends State {

    private InterfaceEventListener interfaceEventListener;

    public StateWithInterface(PApplet applet, String name) {

        super(applet, name);

        interfaceEventListener = new InterfaceEventListener(applet);
    }

    public InterfaceEventListener getInterfaceEventListener() {
        return interfaceEventListener;
    }

    public void onStateLoad() {

        if (getStateMachine() != RootMachine.getInstance()) {
            interfaceEventListener.unLock();
        }
    }

    public void onStateUnLoad() {

        if (getStateMachine() != RootMachine.getInstance()) {
            interfaceEventListener.lock();
        }
    }
}

package Moustache;

import Common.Resource;
import Common.State;
import Common.StateMachine;
import Interface.InterfaceDelegate;
import Interface.InterfaceMenu;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;

public class MenuState extends StateWithAnimation {

    public InterfaceMenu mainInterfaceMenu;

    public MenuState(PApplet applet) {

        super(applet, "main-this");

        mainInterfaceMenu = new InterfaceMenu(applet, getInterfaceEventListener(), 150, 40);

        mainInterfaceMenu.attach("Game").setInterfaceListener(new InterfaceDelegate() {
            public void onInterfaceMouseClick() {
                getStateMachine().change("main-game");
            }
        });

        mainInterfaceMenu.attach("Restart").setInterfaceListener(new InterfaceDelegate() {
            public void onInterfaceMouseClick() {

                StateMachine thisMachine = getStateMachine();
                State gameState = thisMachine.find("main-game");

                gameState.onStateAttach();
                thisMachine.change(gameState);
            }
        });

        mainInterfaceMenu.attach("Editor").setInterfaceListener(new InterfaceDelegate() {
            public void onInterfaceMouseClick() {
                getStateMachine().change("main-editor");
            }
        });

        mainInterfaceMenu.attach("Take a Picture").setInterfaceListener(new InterfaceDelegate() {
            public void onInterfaceMouseClick() {
                getStateMachine().change("main-pic");
            }
        });

        mainInterfaceMenu.attach("Exit").setInterfaceListener(new InterfaceDelegate() {
            public void onInterfaceMouseClick() {
                System.exit(0);
            }
        });

        getInterfaceEventListener().unLock();
    }

    private ArrayList<CloudInfo> cloudList = new ArrayList<CloudInfo>();

    public void onStateAttach() {

        for (int i = 0; i < 10; i++) {
            cloudList.add(new CloudInfo(applet, Resource.getInstance().imageWorldCloud));
        }
    }

    public void onStateRender(PGraphics g) {

        for (CloudInfo ci : cloudList) {
            ci.invokeRenderMethod(g);
        }

        mainInterfaceMenu.invokeRenderMethod(g);
    }
}

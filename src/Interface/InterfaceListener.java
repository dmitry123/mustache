package Interface;

import processing.core.PGraphics;

interface InterfaceListener {

    public void onInterfaceRender(PGraphics g);
    public void onInterfaceTick();
    public void onInterfaceMouseClick();
    public void onInterfaceMouseDown();
    public void onInterfaceMouseUp();
    public void onInterfaceMouseMove();
    public void onInterfaceMouseDrag();
    public void onInterfaceMouseEnter();
    public void onInterfaceMouseLeave();
}

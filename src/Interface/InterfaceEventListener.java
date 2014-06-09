package Interface;

import processing.core.PApplet;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class InterfaceEventListener implements MouseListener, MouseMotionListener {

    private ArrayList<InterfaceObject> interfaceObjects;
    private boolean isLocked = true;

    public InterfaceEventListener(PApplet applet) {

        interfaceObjects = new ArrayList<InterfaceObject>();

        if (applet != null) {
            applet.addMouseListener(this);
        }
    }

    public void lock() { isLocked = true; }
    public void unLock() { isLocked = false; }

    public void addInterfaceListener(InterfaceObject interfaceObject) {

        if (!interfaceObjects.contains(interfaceObject)) {
            interfaceObjects.add(interfaceObject);
        }
    }

    public void mouseClicked(MouseEvent e) {

        if (isLocked) {
            return;
        }

        for (InterfaceObject io : interfaceObjects) {
            io.onMouseClick();
        }
    }

    public void mousePressed(MouseEvent e) {

        if (isLocked) {
            return;
        }

        for (InterfaceObject io : interfaceObjects) {
            io.onMouseDown();
        }
    }

    public void mouseReleased(MouseEvent e) {

        if (isLocked) {
            return;
        }

        for (InterfaceObject io : interfaceObjects) {
            io.onMouseUp();
        }
    }

    public void mouseDragged(MouseEvent e) {

        if (isLocked) {
            return;
        }

        for (InterfaceObject io : interfaceObjects) {
            io.onMouseDrag();
        }
    }

    public void mouseMoved(MouseEvent e) {

        if (isLocked) {
            return;
        }

        for (InterfaceObject io : interfaceObjects) {

            if (io.isMouseOver() && io instanceof InterfaceButton) {
                System.out.print(((InterfaceButton) io).text);
            }

            io.onMouseMove();
        }
    }

    public void mouseEntered(MouseEvent e) {
        // ignore
    }

    public void mouseExited(MouseEvent e) {
        // ignore
    }
}

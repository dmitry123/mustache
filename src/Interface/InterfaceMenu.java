package Interface;

import processing.core.PApplet;
import processing.core.PGraphics;

import java.awt.*;
import java.util.ArrayList;

public class InterfaceMenu extends InterfaceObject {

    public enum Align {
        Default,
        Center,
        Left,
        Right,
        Top,
        Bottom,
        LeftTop,
        LeftBottom,
        RightTop,
        RightBottom
    }

    public Align autoAlign = Align.Default;

    public InterfaceMenu(PApplet applet, InterfaceEventListener listener, int width, int height) {

        this(applet, listener, 0, 0, width, height);
        autoAlign = Align.Center;
    }

    public InterfaceMenu(PApplet applet, InterfaceEventListener listener, int x, int y, int width, int height) {

        super(applet, listener, x, y, width, height);

        buttonWidth = width;
        buttonHeight = height;

        lastTranslate.setLocation(x, y);

        setButtonInterval(5);
        setFillColor(applet.color(0xac, 0xac, 0xac));
    }

    public InterfaceMenu(PApplet applet, InterfaceEventListener listener, int x, int y, int width, int height, String[] list) {

        this(applet, listener, x, y, width, height);

        for (String b : list) {
            attach(b);
        }
    }

    private int buttonWidth;
    private int buttonHeight;
    private int buttonInterval;

    private Point lastTranslate = new Point();

    public ArrayList<InterfaceButton> getList() {
        return interfaceButtonList;
    }

    public int getButtonInterval() {
        return buttonInterval;
    }

    public void setButtonInterval(int interval) {

        buttonInterval = interval;
        setWidth(buttonWidth + buttonInterval * 2);
    }

    private ArrayList<InterfaceButton> interfaceButtonList = new ArrayList<InterfaceButton>();

    public void onInterfaceRender(PGraphics g) {

        g.pushMatrix();
        g.translate(-positionX, -positionY);

        for (InterfaceButton b : interfaceButtonList) {
            b.invokeRenderMethod(g);
        }

        g.popMatrix();
    }

    public InterfaceButton attach(String text) {

        InterfaceButton interfaceInterfaceButton = new InterfaceButton(
            applet,
            interfaceEventListener,
            positionX + buttonInterval,
            positionY + buttonHeight * interfaceButtonList.size() + buttonInterval * interfaceButtonList.size() + buttonInterval,
            buttonWidth,
            buttonHeight,
            text);

        interfaceInterfaceButton.id = interfaceButtonList.size();

        interfaceButtonList.add(interfaceInterfaceButton);

        setHeight(interfaceButtonList.size() * (buttonHeight + buttonInterval) + buttonInterval);

        if (autoAlign != Align.Default) {
            alignTo(autoAlign);
        }

        return interfaceInterfaceButton;
    }

    public void detach(InterfaceButton interfaceInterfaceButton) {

        if (interfaceButtonList.contains(interfaceInterfaceButton)) {
            interfaceButtonList.remove(interfaceInterfaceButton);
        }
    }

    public InterfaceButton getActiveButton() {

        for (InterfaceButton b : interfaceButtonList) {
            if (b.isMouseOver()) {
                return b;
            }
        }

        return null;
    }

    public int getActiveButtonID() {

        InterfaceButton b;

        if ((b = getActiveButton()) != null) {
            return b.id;
        }

        return -1;
    }

    public InterfaceButton getButtonByID(int id) {

        if (id >= 0 && id <= interfaceButtonList.size() - 1) {
            return interfaceButtonList.get(id);
        }

        return null;
    }

    public InterfaceButton getButtonByName(String name) {

        for (InterfaceButton b : interfaceButtonList) {
            if (b.text.equals(name)) {
                return b;
            }
        }

        return null;
    }

    public void translateMenu(int x, int y) {

        lastTranslate.setLocation(x, y);

        for (InterfaceButton b : interfaceButtonList) {
            b.translate(x, y);
        }
    }
    public void moveMenu(int x, int y) {

        translateMenu(-lastTranslate.x, -lastTranslate.y);
        translateMenu(x, y);
        setPosition(x, y);
    }

    public void alignTo(Align align) {

        int menuHeight = getHeight();

        switch (align)
        {
            case Left: moveMenu(0, applet.height / 2 - menuHeight / 2); break;
            case Right: moveMenu(applet.width - width, applet.height / 2 - menuHeight / 2); break;
            case Bottom: moveMenu(applet.width / 2 - width / 2, applet.height - menuHeight); break;
            case Top: moveMenu(applet.width / 2 - width / 2, 0); break;
            case Default: moveMenu(0, 0); break;
            case Center: moveMenu(applet.width / 2 - width / 2, applet.height / 2 - menuHeight / 2); break;
            case LeftBottom: moveMenu(0, applet.height - menuHeight); break;
            case LeftTop: moveMenu(0, 0); break;
            case RightBottom: moveMenu(applet.width - width, applet.height - menuHeight); break;
            case RightTop: moveMenu(applet.width - width, 0); break;
        }
    }

    private void buildMenu() {

    }
}

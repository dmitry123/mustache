package Interface;

import processing.core.PApplet;
import processing.core.PGraphics;

public class InterfaceButton extends InterfaceObject {

    protected String text = null;

    protected int basicColor;
    protected int activeColor;

    public int id;

    private void setDefaults() {

        basicColor = fillColor;
        activeColor = applet.color(0x39, 0x6c, 0x75);
        id = 0;
    }

    public InterfaceButton(PApplet applet, InterfaceEventListener listener, int x, int y, int width, int height) {
        super(applet, listener, x, y, width, height); setDefaults();
    }

    public InterfaceButton(PApplet applet, InterfaceEventListener listener, int x, int y, int width, int height, Object userData) {
        super(applet, listener, x, y, width, height, userData); setDefaults();
    }

    public InterfaceButton(PApplet applet, InterfaceEventListener listener, int x, int y, int width, int height, String text) {
        super(applet, listener, x, y, width, height); this.text = text; setDefaults();
    }

    public InterfaceButton(PApplet applet, InterfaceEventListener listener, int x, int y, int width, int height, String text, Object userData) {
        super(applet, listener, x, y, width, height, userData); this.text = text; setDefaults();
    }

    public void onInterfaceRender(PGraphics g) {

        if (isMouseOver()) {
            setFillColor(activeColor);
        } else {
            setFillColor(basicColor);
        }

        if (text != null) {

            g.textSize(fontSize);
            g.fill(0xffffffff);
            g.text(text, width / 2 - applet.textWidth(text) / 2, height / 2 - 10, width, height);
        }
    }

    public void onInterfaceMouseEnter() {
        setFillColor(activeColor);
    }

    public void onInterfaceMouseLeave() {
        setFillColor(basicColor);
    }
}

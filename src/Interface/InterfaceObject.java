package Interface;

import processing.core.PApplet;
import processing.core.PGraphics;

public class InterfaceObject implements InterfaceListener {

    public boolean isVisible = true;
    public boolean isTickable = true;
    public boolean isActive = true;

    protected int positionX;
    protected int positionY;
    protected int strokeSize;
    protected int strokeColor;
    protected int fillColor;
    protected int textColor;
    protected int width;
    protected int height;
    protected int fontSize;

    protected PApplet applet;
    protected Object userData;
    protected InterfaceEventListener interfaceEventListener;
    protected InterfaceListener interfaceListener;

    public void setPosition(int x, int y) { positionX = x; positionY = y; }
    public void setStrokeSize(int size) { strokeSize = size; }
    public void setStrokeColor(int color) { strokeColor = color; }
    public void setFillColor(int color) { fillColor = color; }
    public void setWidth(int w) { width = w; }
    public void setHeight(int h) { height = h; }
    public void setFontSize(int size) { fontSize = size; }
    public void setUserData(Object ud) { userData = ud; }

    public int getPositionX() { return positionX; }
    public int getPositionY() { return positionY; }
    public int getStrokeSize() { return strokeSize; }
    public int getStrokeColor() { return strokeColor; }
    public int getFillColor() { return fillColor; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getFontSize() { return fontSize; }
    public PApplet getApplet() { return applet; }
    public Object getUserData() { return userData; }

    public void translate(int x, int y) {

        positionX += x;
        positionY += y;
    }

    public InterfaceObject(PApplet applet, InterfaceEventListener listener, int x, int y, int width, int height, Object userData) {
        this(applet, listener, x, y, width, height); setUserData(userData);
    }

    public InterfaceObject(PApplet applet, InterfaceEventListener listener, int x, int y, int width, int height) {

        this.applet = applet;
        this.positionX = x;
        this.positionY = y;
        this.width = width;
        this.height = height;

        this.fillColor = applet.color(0x41, 0x95, 0xa4);
        this.strokeColor = applet.color(0x00, 0x00, 0x00);
        this.textColor = applet.color(0xff, 0xff, 0xff);
        this.strokeSize = 2;
        this.fontSize = 20;

        this.interfaceListener = null;

        if (listener != null) {
            listener.addInterfaceListener(this);
        }

        interfaceEventListener = listener;
    }

    public void setInterfaceListener(InterfaceListener interfaceListener) {
        this.interfaceListener = interfaceListener;
    }

    public void invokeRenderMethod(PGraphics g) {

        if (!isVisible) {
            return;
        }

        g.pushMatrix();
        g.pushStyle();
        g.translate(positionX, positionY);
        g.clip(-strokeSize, -strokeSize, width + 2 * strokeSize, height + 2 * strokeSize);
        g.strokeWeight(strokeSize);
        g.fill(fillColor);
        g.stroke(strokeColor);
        g.rect(0, 0, width, height, 10);

        onInterfaceRender(g);

        if (interfaceListener != null) {
            interfaceListener.onInterfaceRender(g);
        }

        g.noClip();
        g.popStyle();
        g.popMatrix();
    }

    public void invokeTickMethod() {

        if (isTickable) {

            onInterfaceTick();

            if (interfaceListener != null) {
                interfaceListener.onInterfaceTick();
            }
        }
    }

    public boolean isMouseOver() {

        if (!isActive) {
            return false;
        }

        return
            applet.mouseX > positionX && applet.mouseX < positionX + width &&
            applet.mouseY > positionY && applet.mouseY < positionY + height;
    }

    public void onMouseClick() {

        if (isMouseOver()) {

            onInterfaceMouseClick();

            if (interfaceListener != null) {
                interfaceListener.onInterfaceMouseClick();
            }
        }
    }

    public void onMouseDown() {

        if (isMouseOver()) {

            onInterfaceMouseDown();

            if (interfaceListener != null) {
                interfaceListener.onInterfaceMouseDown();
            }
        }
    }

    public void onMouseUp() {

        if (isMouseOver()) {

            onInterfaceMouseUp();

            if (interfaceListener != null) {
                interfaceListener.onInterfaceMouseUp();
            }
        }
    }

    public void onMouseMove() {

        isMouseHoverCurrent = isMouseOver();

        if (isMouseHoverLast && !isMouseHoverCurrent) {

            onInterfaceMouseLeave();

            if (interfaceListener != null) {
                interfaceListener.onInterfaceMouseLeave();
            }
        }
        else if (!isMouseHoverLast && isMouseHoverCurrent) {

            onInterfaceMouseEnter();

            if (interfaceListener != null) {
                interfaceListener.onInterfaceMouseEnter();
            }
        }
        else if (isMouseHoverCurrent) {
            onInterfaceMouseMove();

            if (interfaceListener != null) {
                interfaceListener.onInterfaceMouseMove();
            }
        }

        isMouseHoverLast = isMouseHoverCurrent;
    }

    public void onMouseDrag() {

        isMouseHoverCurrent = isMouseOver();

        if (isMouseHoverLast && !isMouseHoverCurrent) {

            onInterfaceMouseLeave();

            if (interfaceListener != null) {
                interfaceListener.onInterfaceMouseLeave();
            }
        }
        else if (!isMouseHoverLast && isMouseHoverCurrent) {

            onInterfaceMouseEnter();

            if (interfaceListener != null) {
                interfaceListener.onInterfaceMouseEnter();
            }
        }
        else if (isMouseHoverCurrent) {

            onInterfaceMouseDrag();

            if (interfaceListener != null) {
                interfaceListener.onInterfaceMouseDrag();
            }
        }

        isMouseHoverLast = isMouseHoverCurrent;
    }

    private boolean isMouseHoverCurrent = false;
    private boolean isMouseHoverLast = false;

    public void onInterfaceRender(PGraphics g) {}
    public void onInterfaceTick() {}
    public void onInterfaceMouseClick() {}
    public void onInterfaceMouseDown() {}
    public void onInterfaceMouseUp() {}
    public void onInterfaceMouseMove() {}
    public void onInterfaceMouseDrag() {}
    public void onInterfaceMouseEnter() {}
    public void onInterfaceMouseLeave() {}
}
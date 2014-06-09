package Common;

import processing.core.PGraphics;
import processing.core.PImage;

import java.lang.reflect.Method;

public class TextureAtlas {

    public int width = 0;
    public int height = 0;

    private PImage image;
    private int interval = 0;
    private long lastTime = 0;
    private int index = 0;
    private Object delegate = null;
    private boolean isNoLoop = false;

    private Method onAnimationFinished;

    public TextureAtlas(Object delegate, PImage image, int w, int h, int delay) {

        this.delegate = delegate;
        this.image = image;

        width = w;
        height = h;

        interval = delay;
        lastTime = System.currentTimeMillis();

        try {
            onAnimationFinished = delegate.getClass().getMethod("onAnimationFinished", TextureAtlas.class);
        }
        catch (NoSuchMethodException e) {
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void noLoop() {
        isNoLoop = true;
    }

    public void withLoop() {
        isNoLoop = false;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void render(PGraphics g) {

        long currentTime = System.currentTimeMillis();

        if (interval != 0 && currentTime - lastTime > interval && (lastTime = currentTime) != 0) {

            if (++index * width >= image.width) {

                if (onAnimationFinished != null && delegate != null) {
                    try {
                        onAnimationFinished.invoke(delegate, this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (!isNoLoop) {
                    index = 0;
                }
            }
        }

        if (image != null) {
            g.image(image, 0, 0, width, height, index * width, 0, width + index * width, height);
        }
    }
}

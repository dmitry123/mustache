package Moustache;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class CloudInfo {

    private PImage image;
    private PVector position;
    private float velocity;
    private PApplet applet;

    public CloudInfo(PApplet applet, PImage image) {

        this.applet = applet;
        this.image = image;

        if ((int)(applet.random(0, 2)) == 1) {
            position = new PVector(applet.random(0, applet.width), 0);
            velocity = -applet.random(0.1f, 0.5f);
        } else {
            position = new PVector(applet.random(0, applet.width), 0);
            velocity = +applet.random(0.1f, 0.5f);
        }

        position.y = applet.random(0, applet.height);
    }

    public void invokeRenderMethod(PGraphics g) {

        boolean isRandomize = false;

        position.x += velocity;

        if (position.x > applet.width  && velocity > 0) {
            position.x -= applet.width + image.width; isRandomize = true;
        } else if (position.x + image.width < 0 && velocity < 0) {
            position.x += applet.width + image.width; isRandomize = true;
        }

        if (isRandomize) {

            if ((int)(applet.random(0, 2)) == 1) {
                position = new PVector(applet.width, 0);
                velocity = -applet.random(0.1f, 0.5f);
            } else {
                position = new PVector(-image.width, 0);
                velocity = +applet.random(0.1f, 0.5f);
            }

            position.y = applet.random(0, applet.height);
        }

        g.pushMatrix();
        g.translate(position.x, position.y);
        g.scale(0.5f);
        g.image(image, 0, 0);
        g.popMatrix();
    }
}

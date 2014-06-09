package Game;

import processing.core.PGraphics;
import processing.core.PImage;

public class Platform extends GameObject {

    final static int PLATFORM_CLOUD = 0;
    final static int PLATFORM_GRASS = 1;

    private World world = null;
    private PImage image = null;

    public Platform(World world, String fileName, float x, float y) {
        this(world, world.getApplet().loadImage(fileName), x, y);
    }

    public Platform(World world, PImage image, float x, float y) {

        super("platform", world);

        this.world = world;
        this.image = image;

        world.createStatic(this, x, y, image.width - image.width / 20, image.height / 2);
    }

    public void onGameObjectRender(PGraphics g) {

        g.pushMatrix();
        g.translate(-image.width / 2, -image.height / 2 - image.height / 10);
        g.image(image, 0, 0);
        g.popMatrix();
    }
}

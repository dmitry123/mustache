package Game;

import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;
import processing.core.PApplet;
import processing.core.PVector;

public class Box2D {

    public PApplet parent;
    public Body groundBody;
    public World world;

    public float transX;
    public float transY;
    public float scaleFactor;
    public float yFlip;

    public Box2D(PApplet p) {
        this(p, 10);
    }

    public Box2D(PApplet p, float sf) {

        parent = p;

        transX = parent.width / 2;
        transY = parent.height / 2;

        scaleFactor = sf;
        yFlip = -1;
    }

    public void setScaleFactor(float scale) {
        scaleFactor = scale;
    }

    public void step() {

        step(1.0f / 60f, 10, 8);

        world.clearForces();
    }

    public void step(float timeStep, int velocityIterations, int positionIterations) {
        world.step(timeStep, velocityIterations, positionIterations);
    }

    public void setWarmStarting(boolean b) {
        world.setWarmStarting(b);
    }

    public void setContinuousPhysics(boolean b) {
        world.setContinuousPhysics(b);
    }

    public void createWorld() {

        Vec2 gravity = new Vec2(0.0f, -10.0f);

        createWorld(gravity);

        setWarmStarting(true);
        setContinuousPhysics(true);
    }

    public void createWorld(Vec2 gravity) {
        createWorld(gravity, true, true);
    }

    public void createWorld(Vec2 gravity, boolean warmStarting, boolean continous) {

        world = new World(gravity);

        setWarmStarting(warmStarting);
        setContinuousPhysics(continous);

        BodyDef bodyDef = new BodyDef();
        groundBody = world.createBody(bodyDef);
    }

    public Body getGroundBody() {
        return groundBody;
    }

    public void setGravity(float x, float y) {
        world.setGravity(new Vec2(x, y));
    }

    public Vec2 coordWorldToPixels(Vec2 world) {
        return coordWorldToPixels(world.x, world.y);
    }

    public PVector coordWorldToPixelsPVector(Vec2 world) {

        Vec2 v = coordWorldToPixels(world.x, world.y);

        return new PVector(v.x, v.y);
    }

    public Vec2 coordWorldToPixels(float worldX, float worldY) {

        float pixelX = PApplet.map(worldX, 0f, 1f, transX, transX + scaleFactor);
        float pixelY = PApplet.map(worldY, 0f, 1f, transY, transY + scaleFactor);

        if (yFlip == -1.0f) {
            pixelY = PApplet.map(pixelY, 0f, parent.height, parent.height, 0f);
        }

        return new Vec2(pixelX, pixelY);
    }

    // convert Coordinate from pixel space to box2d world
    public Vec2 coordPixelsToWorld(Vec2 screen) {
        return coordPixelsToWorld(screen.x, screen.y);
    }

    public Vec2 coordPixelsToWorld(PVector screen) {
        return coordPixelsToWorld(screen.x, screen.y);
    }

    public Vec2 coordPixelsToWorld(float pixelX, float pixelY) {

        float worldX = PApplet.map(pixelX, transX, transX + scaleFactor, 0f, 1f);
        float worldY = pixelY;

        if (yFlip == -1.0f) {
            worldY = PApplet.map(pixelY, parent.height, 0f, 0f, parent.height);
        }

        worldY = PApplet.map(worldY, transY, transY + scaleFactor, 0f, 1f);

        return new Vec2(worldX, worldY);
    }

    // Scale scalar quantity between worlds
    public float scalarPixelsToWorld(float val) {
        return val / scaleFactor;
    }

    public float scalarWorldToPixels(float val) {
        return val * scaleFactor;
    }

    // Scale vector between worlds
    public Vec2 vectorPixelsToWorld(Vec2 v) {

        Vec2 u = new Vec2(v.x / scaleFactor, v.y / scaleFactor);

        u.y *= yFlip;

        return u;
    }

    public Vec2 vectorPixelsToWorld(PVector v) {

        Vec2 u = new Vec2(v.x / scaleFactor, v.y / scaleFactor);

        u.y *= yFlip;

        return u;
    }

    public Vec2 vectorPixelsToWorld(float x, float y) {

        Vec2 u = new Vec2(x / scaleFactor, y / scaleFactor);

        u.y *= yFlip;

        return u;
    }

    public Vec2 vectorWorldToPixels(Vec2 v) {

        Vec2 u = new Vec2(v.x * scaleFactor, v.y * scaleFactor);

        u.y *= yFlip;

        return u;
    }

    public PVector vectorWorldToPixelsPVector(Vec2 v) {

        PVector u = new PVector(v.x * scaleFactor, v.y * scaleFactor);

        u.y *= yFlip;

        return u;
    }

    public Body createBody(BodyDef bd) {
        return world.createBody(bd);
    }

    public Joint createJoint(JointDef jd) {
        return world.createJoint(jd);
    }

    public Vec2 getBodyPixelCoord(Body b) {

        Transform xf = b.getTransform();
        return coordWorldToPixels(xf.p);
    }

    public PVector getBodyPixelCoordPVector(Body b) {
        Transform xf = b.getTransform();
        return coordWorldToPixelsPVector(xf.p);
    }

    public void destroyBody(Body b) {
        world.destroyBody(b);
    }
}

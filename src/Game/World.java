package Game;

import Common.Vertex;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class World implements ContactListener {

    private Box2D box2d;
    private ArrayList<GameObject> gameObjectList;

    public int width = 0;
    public int height = 0;

    public Vertex backgroundOffset = new Vertex();

    private int lowestPosition = 0;
    private PApplet applet;

    public PApplet getApplet() {
        return applet;
    }

    public World(PApplet applet) {

        this.applet = applet;

        box2d = new Box2D(applet);
        gameObjectList = new ArrayList<GameObject>();

        box2d.createWorld();
        box2d.setGravity(0, -20);
        box2d.world.setContactListener(this);

        this.width = applet.width;
        this.height = applet.height;
    }

    public int getLowestPosition() { return lowestPosition + 100; }
    public Vertex getBackgroundOffset() { return backgroundOffset; }
    public Box2D getBox2D() { return box2d; }
    public ArrayList<GameObject> getList() { return gameObjectList;}

    public void invokeRenderMethod(PGraphics g) {

        box2d.step();
        box2d.step();

        g.pushMatrix();

        try {
            for (GameObject go : gameObjectList) {
                go.invokeRenderMethod(g);
            }
        } catch (ConcurrentModificationException e) {
            // ignore
        }

        g.popMatrix();
    }

    public GameObject attach(GameObject gameObject) {

        if (gameObjectList.contains(gameObject)) {
            return gameObject;
        }

        gameObjectList.add(gameObject);
        gameObject.onGameObjectAttach();

        if (gameObject.getName().equals("platform") && gameObject.getPosition().y > lowestPosition) {
            lowestPosition = (int) gameObject.getPosition().y;
        }

        return gameObject;
    }

    public GameObject detach(GameObject gameObject) {

        if (!gameObjectList.contains(gameObject)) {
            return gameObject;
        }

        if (!box2d.world.isLocked()) {
            box2d.destroyBody(gameObject.getBody());
        }

        gameObject.onGameObjectDetach();
        gameObjectList.remove(gameObject);

        return gameObject;
    }

    public GameObject find(String name) {

        for (GameObject go : gameObjectList) {
            if (go.getName().equals(name)) {
                return go;
            }
        }

        return null;
    }

    public void clear() {

        for (GameObject go : gameObjectList) {
            detach(go);
        }

        box2d = new Box2D(applet);
    }

    public GameObject createDynamic(GameObject object, float x, float y, float w, float h) {

        PolygonShape sd = new PolygonShape();
        FixtureDef fd = new FixtureDef();
        BodyDef bd = new BodyDef();

        float box2dW = box2d.scalarPixelsToWorld(w / 2);
        float box2dH = box2d.scalarPixelsToWorld(h / 2);

        sd.setAsBox(box2dW, box2dH);

        fd.shape = sd;
        fd.density = 0;
        fd.friction = 10;
        fd.restitution = 0.0f;

        bd.type = BodyType.DYNAMIC;
        bd.position.set(box2d.coordPixelsToWorld(x, y));

        object.setBody(box2d.createBody(bd));
        object.getBody().createFixture(fd);

        object.width = w;
        object.height = h;

        return attach(object);
    }

    public GameObject createStatic(GameObject object, float x, float y, float w, float h) {

        PolygonShape sd = new PolygonShape();
        BodyDef bd = new BodyDef();

        float box2dW = box2d.scalarPixelsToWorld(w / 2);
        float box2dH = box2d.scalarPixelsToWorld(h / 2);

        sd.setAsBox(box2dW, box2dH);

        bd.type = BodyType.STATIC;
        bd.position.set(box2d.coordPixelsToWorld(x, y));

        object.setBody(box2d.createBody(bd));
        object.getBody().createFixture(sd, 1);

        object.width = w;
        object.height = h;

        return attach(object);
    }

    public Vertex getRandomizedPosition(int height) {

        ArrayList<GameObject> platformList = new ArrayList<GameObject>();

        // looking for all platforms in the world
        for (GameObject go : getList ()) {
            if (go.getName() == "platform") {
                platformList.add(go);
            }
        }

        if (platformList.size() > 0) {

            // get random platform from list
            GameObject p = platformList.get((int) applet.random(0, platformList.size() - 1));

            // set player's position with random width
            return new Vertex(p.x + applet.random(0, p.width), p.y + 2 * height);
        }

        return null;
    }

    public void preSolve(org.jbox2d.dynamics.contacts.Contact contact, org.jbox2d.collision.Manifold manifold) {}
    public void postSolve(org.jbox2d.dynamics.contacts.Contact contact, org.jbox2d.callbacks.ContactImpulse contactImpulse) {}

    public void beginContact(Contact cp) {

        Fixture f1 = cp.getFixtureA();
        Fixture f2 = cp.getFixtureB();

        Body b1 = f1.getBody();
        Body b2 = f2.getBody();

        GameObject o1 = (GameObject) b1.getUserData();
        GameObject o2 = (GameObject) b2.getUserData();

        if (!(o1 instanceof Platform)) {
            o1.setLinearVelocity(o1.getLinearVelocity().x, -1);
        }

        if (!(o1 instanceof Platform)) {
            o2.setLinearVelocity(o2.getLinearVelocity().x, -1);
        }

        if (o1 != null && o2 != null && o1 != o2) {
            o1.onGameObjectCollide(o2);
            o2.onGameObjectCollide(o1);
        }
    }

    public void endContact(Contact cp) {

        Fixture f1 = cp.getFixtureA();
        Fixture f2 = cp.getFixtureB();

        Body b1 = f1.getBody();
        Body b2 = f2.getBody();

        GameObject o1 = (GameObject) b1.getUserData();
        GameObject o2 = (GameObject) b2.getUserData();

        if (o1 != null && o2 != null && o1 != o2) {
            o1.onGameObjectLeave(o2);
            o2.onGameObjectLeave(o1);
        }
    }
}

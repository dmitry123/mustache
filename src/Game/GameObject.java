package Game;

import Common.Vertex;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import processing.core.PGraphics;

public class GameObject implements GameListener {

    // object's name
    private String name;

    // box2d's body and world
    private Body body = null;
    private World world = null;

    // position
    float x;
    float y;

    // scale
    public float width;
    public float height;

    // movable state
    public boolean isMovable = true;

    public GameObject(String name, World world) {

        // copy basic attributes
        this.name = name;
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public String getName() {
        return name;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {

        if (this.body == null) {
            body.setUserData(this);
        }

        this.body = body;
    }

    public Vertex getPosition() {
        return new Vertex(
                world.getBox2D().getBodyPixelCoord(body).x,
                world.getBox2D().getBodyPixelCoord(body).y);
    }

    public void setPosition(float x, float y) {
        body.setTransform(world.getBox2D().coordPixelsToWorld(x, y), body.getAngle());
    }

    public Vertex getLinearVelocity() {
        return new Vertex(
                body.getLinearVelocity().x,
                body.getLinearVelocity().y);
    }

    public float getAngle() {
        return body.getAngle();
    }

    public void setAngle(float angle) {
        body.setTransform(body.getPosition(), angle);
    }

    public void setLinearVelocity(float x, float y) {
        body.setLinearVelocity(new Vec2(x, y));
    }

    public void setAngularVelocity(float velocity) {
        body.setAngularVelocity(velocity);
    }

    public float getDistance(GameObject body) {

        return (float)Math.sqrt(
                Math.pow(getPosition().x - body.getPosition().x, 2.0f) +
                        Math.pow(getPosition().y - body.getPosition().y, 2.0f));
    }

    public void invokeRenderMethod(PGraphics g) {

        Vertex position = this.getPosition();
        float angle = this.getAngle();

        if (position.y > world.getLowestPosition()) {
            onGameObjectOutOfWorld();
        }

        g.pushMatrix();
        g.translate(position.x, position.y);
        g.rotate(-angle);

        onGameObjectRender(g);

        g.noFill();
        g.stroke(0xffffffff);
        g.rectMode(g.CENTER);
//        g.rect(0, 0, width, height);
        g.rectMode(g.CORNER);
        g.popMatrix();
    }

    public void onGameObjectAttach() {}
    public void onGameObjectDetach() {}
    public void onGameObjectRender(PGraphics g) {}
    public void onGameObjectCollide(GameObject object) {}
    public void onGameObjectOutOfWorld() {}
    public void onGameObjectLeave(GameObject object) {}
}

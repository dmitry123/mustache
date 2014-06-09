package Game;

import Common.Resource;
import Common.TextureAtlas;
import Common.Vertex;
import org.jbox2d.common.Vec2;
import processing.core.PGraphics;

import java.io.IOException;
import java.net.Socket;

public class Player extends GameObject {

    public static final int PLAYER_WIDTH = 120;
    public static final int PLAYER_HEIGHT = 120;
    public static final int PLAYER_INTERVAL = 50;
    public static final int PLAYER_VELOCITY_X = 8;
    public static final int PLAYER_VELOCITY_Y = 25;
    public static final int PLAYER_DIE_DELAY = 1000;
    public static final int PLAYER_ATTACK_DELAY = 350;

    private TextureAtlas atlasIdle;
    private TextureAtlas atlasRun;
    private TextureAtlas atlasJump;
    private TextureAtlas atlasActive;
    private TextureAtlas atlasFall;
    private TextureAtlas atlasDie;
    private TextureAtlas atlasFight;

    private GameObject enemy = null;
    private Socket socket = null;
    private Thread thread = null;
    private long killedTime = 0;
    private long attackTime = 0;

    public boolean isReversed = false;
    public boolean isJump = true;
    public boolean isKilled = false;
    public boolean isLeft = false;
    public boolean isRight = false;
    public boolean isBackground = false;

    public Player(World world, float x, float y, boolean locked) {

        this(world, x, y);

        isMovable = false;
    }

    public Player(World world, float x, float y, Socket socket) {

        // construct this
        this(world, x, y);

        // start player's session
        startSocketSession(socket);
    }

    public Player(World world, float x, float y) {

        // construct this
        super("player", world);

        Resource r = Resource.getInstance();

        // create new atlas textures
        atlasIdle = new TextureAtlas(this, r.imagePlayerIdle, PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_INTERVAL);
        atlasRun = new TextureAtlas(this, r.imagePlayerRun, PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_INTERVAL);
        atlasJump = new TextureAtlas(this, r.imagePlayerJump, PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_INTERVAL);
        atlasFall = new TextureAtlas(this, r.imagePlayerFall, PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_INTERVAL);
        atlasDie = new TextureAtlas(this, r.imagePlayerDie, PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_INTERVAL);
        atlasFight = new TextureAtlas(this, r.imagePlayerFight, PLAYER_WIDTH, PLAYER_HEIGHT, 100);

        // disable atlas's loop
        atlasDie.noLoop();

        // set active atlas
        atlasActive = atlasIdle;

        Vertex rp;

        if (getWorld().find("player") == null) {
            isBackground = true;
        }

        if ((rp = getRandomizedPosition()) != null) {
            world.createDynamic(this, rp.x, rp.y, 50, PLAYER_HEIGHT);
        }
        else {
            world.createDynamic(this, x, y, 50, PLAYER_HEIGHT);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public Vertex getRandomizedPosition() {
        return getWorld().getRandomizedPosition(PLAYER_HEIGHT);
    }

    public void startSocketSession(Socket socket) {

        closeSocketSession();

        if (thread == null) {

            // copy socket object
            this.socket = socket;

            // create new thread
            thread = new Thread(new PlayerSession(this));

            // detach thread
            thread.start();
        }
    }

    public void closeSocketSession() {

        if (thread != null) {
            thread.interrupt();
        }

        if (socket != null) {
            try {
                socket.close();
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        thread = null;
        socket = null;
    }

    public void setIdleAnimation() {

        if (atlasActive != atlasFight &&
            atlasActive != atlasJump &&
            atlasActive != atlasFall &&
            atlasActive != atlasDie
        ) {
            atlasActive = atlasIdle;
        }
    }

    public void doJump() {

        if (isKilled || !isMovable) {
            return;
        }

        getBody().setLinearVelocity(new Vec2(getBody().getLinearVelocity().x, PLAYER_VELOCITY_Y));
        atlasActive = atlasJump;

        isJump = false;
    }

    public void doLeft() {

        if (isKilled || !isMovable) {
            return;
        }

        setPosition(getPosition().x - PLAYER_VELOCITY_X, getPosition().y);

        if (isJump) {
            atlasActive = atlasRun;
        }

        isReversed = true;
    }

    public void doRight() {

        if (isKilled || !isMovable) {
            return;
        }

        setPosition(getPosition().x + PLAYER_VELOCITY_X, getPosition().y);

        if (isJump) {
            atlasActive = atlasRun;
        }

        isReversed = false;
    }

    public void doFight() {

        if (isKilled || !isMovable) {
            return;
        }

        if (atlasActive == atlasIdle || atlasActive == atlasRun) {
            atlasActive = atlasFight;
            isMovable = false;
        }
    }

    public void showMessage(PGraphics g, String message) {

        // show message with error
        // on user's information
        g.textSize(8);
        g.fill(0xff000000);
        g.text(message, width / 2 - g.textWidth(message) / 2, height / 2 - 4, width, height);
    }

    public void killPlayer() {

        killedTime = System.currentTimeMillis();
        isKilled = true;
        atlasActive = atlasDie;
        atlasActive.setIndex(0);
    }

    public void onGameObjectDetach() {

        if (thread != null) {

            // terminate thread
            thread.interrupt();

            // close socket
            try {
                socket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onGameObjectRender(PGraphics g) {

        // string constants
        final String TEXT_NO_IMAGE = "No Image";

        isJump = (getBody().getLinearVelocity().y == 0);

        if (isKilled && System.currentTimeMillis() - killedTime > PLAYER_DIE_DELAY) {

            isKilled = false;
            atlasActive = atlasIdle;

            Vertex rp;

            if ((rp = getRandomizedPosition()) != null) {
                setPosition(rp.x, rp.y);
                setLinearVelocity(0, -20);
            }
        }

        if (attackTime != 0 && System.currentTimeMillis() - attackTime > PLAYER_ATTACK_DELAY) {
            enemy = null;
            attackTime = 0;
        }

        if ((atlasActive != atlasJump || atlasActive != atlasFall) && getBody().getLinearVelocity().y < 0) {
            if (!isKilled) {
                atlasActive = atlasFall;
            }
        }
        else if ((atlasActive == atlasFall || atlasActive == atlasIdle) && getBody().getLinearVelocity().y == 0) {

            atlasActive = atlasIdle;

            isMovable = true;
            isJump = true;
        }

        if (atlasActive == atlasFight && enemy != null) {

            if (atlasActive.getIndex() == 2 ||
                    atlasActive.getIndex() == 3 ||
                    atlasActive.getIndex() == 6
                    ) {
                if (enemy instanceof Enemy) {
                    ((Enemy) enemy).killEnemy();
                }
                else {
                    ((Player) enemy).killPlayer();
                }

                enemy = null;
            }
        }

        if (getLinearVelocity().y == 0) {
            setLinearVelocity(0, -1);
        }

        g.pushMatrix();
        g.translate(-atlasActive.width / 2, -atlasActive.height / 2);
        if (isReversed) {
            g.pushMatrix();
            g.scale(-1, 1);
            g.translate(-atlasActive.width, 0);
            atlasActive.render(g);
            g.popMatrix();
        }
        else {
            atlasActive.render(g);
        }
        g.popMatrix();
    }

    public void onGameObjectCollide(GameObject go) {

        isJump = true;

        if (go instanceof Player || go instanceof Enemy) {

            attackTime = System.currentTimeMillis();
            enemy = go;
        }
    }

    public void onGameObjectLeave(GameObject go) {

    }

    public void onGameObjectOutOfWorld() {

        if (!isKilled) {
            killPlayer();
        }
    }

    public void onAnimationFinished(TextureAtlas atlas) {

        if (atlas != atlasDie) {
            atlas.setIndex(0);
        }
        else {
            atlas.setIndex(3);
        }

        if (!isKilled) {
            if (atlas == atlasJump) {
                atlasActive = atlasFall;
            }
            else if (atlas == atlasFall) {
                atlasActive.setIndex(atlasActive.getIndex() + 1);
            }
            else if (atlas == atlasFight) {
                atlasActive = atlasIdle;
                isMovable = true;
            }
        }
    }
}

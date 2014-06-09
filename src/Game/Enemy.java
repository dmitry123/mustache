package Game;

import Common.Resource;
import Common.TextureAtlas;
import processing.core.PGraphics;

public class Enemy extends GameObject {

    public static final int ENEMY_WIDTH = 120;
    public static final int ENEMY_HEIGHT = 120;
    public static final int ENEMY_WIDTH_2 = 150;
    public static final int ENEMY_HEIGHT_2 = 150;
    public static final int ENEMY_INTERVAL = 100;
    public static final int ENEMY_VELOCITY_X = 1;
    public static final int ENEMY_VELOCITY_Y = 20;
    public static final int ENEMY_ATTACK_DELAY = 250;

    private TextureAtlas atlasWalk;
    private TextureAtlas atlasFight;
    private TextureAtlas atlasDie;
    private TextureAtlas atlasActive;

    private GameObject platform;
    private boolean goLeft = false;
    private boolean goManual = false;
    private Player player = null;
    private boolean isKilled = false;
    private long attackTime = 0;

    public Enemy(World world, float x, float y) {

        super("enemy", world);

        Resource r = Resource.getInstance();

        atlasWalk = new TextureAtlas(this, r.imageEnemyWalk, ENEMY_WIDTH, ENEMY_HEIGHT, ENEMY_INTERVAL);
        atlasFight = new TextureAtlas(this, r.imageEnemyFight, ENEMY_WIDTH_2, ENEMY_HEIGHT_2, ENEMY_INTERVAL);
        atlasDie = new TextureAtlas(this, r.imageEnemyDie, 200, ENEMY_HEIGHT, ENEMY_INTERVAL);

        atlasActive = atlasWalk;

        atlasDie.noLoop();

        world.createDynamic(this, x, y, 50, ENEMY_HEIGHT);
    }

    public void killEnemy() {

        isKilled = true;
        atlasActive = atlasDie;
    }

    public void onGameObjectRender(PGraphics g) {

        if (attackTime != 0 && System.currentTimeMillis() - attackTime > ENEMY_ATTACK_DELAY) {

//            atlasActive = atlasWalk;
//            attackTime = 0;
//            player = null;
        }

        if (!isKilled && isMovable) {

            if (goLeft) {
                setPosition(getPosition().x - ENEMY_VELOCITY_X, getPosition().y);
            } else {
                setPosition(getPosition().x + ENEMY_VELOCITY_X, getPosition().y);
            }

            if (platform != null) {

                if (getPosition().x >= platform.getPosition().x + platform.width / 2 - width / 2) {
                    goLeft = true;
                } else if (getPosition().x <= platform.getPosition().x - platform.width / 2 + width / 2) {
                    goLeft = false;
                }
            }

            if (player != null) {

                float distance = player.getDistance(this);

                if (distance < 100 && !player.isKilled && (
                        getPosition().x - player.getPosition().x > 0 && goLeft ||
                                getPosition().x - player.getPosition().x < 0 && !goLeft
                )) {

                    atlasActive = atlasFight;

                    if (distance < 75) {
                        if (atlasActive.getIndex() == 9) {

                            player.killPlayer();

                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep(150);
                                    }
                                    catch (InterruptedException e) {
                                        // ignore
                                    }
                                    finally {
                                        goLeft = !goLeft;
                                    }
                                }
                            }
                            ).start();
                        }
                    }
                } else {
                    atlasActive = atlasWalk;
                }
            }
        }

        g.pushMatrix();
        g.translate(-width, -height / 2);

        if (atlasActive != null) {

            if (!goLeft) {

                g.pushMatrix();
                g.translate(width + width + 25, 0);
                g.scale(-1, 1);
                atlasActive.render(g);
                g.popMatrix();
            } else {
                atlasActive.render(g);
            }
        }

        g.popMatrix();
    }

    public void onAnimationFinished(TextureAtlas atlas) {

        if (atlas == atlasDie) {
            getWorld().detach(this);
        }
    }

    public void onGameObjectOutOfWorld() {
        killEnemy();
    }

    public void onGameObjectLeave(GameObject go) {

        if (go == player) {
            player = null;
        }

        atlasActive = atlasWalk;
    }

    public void onGameObjectCollide(GameObject go) {

        if (!(go instanceof Player || go instanceof Enemy)) {

            if (Math.abs(getPosition().y - go.getPosition().y) < go.height / 2) {

                if (getPosition().x < go.getPosition().x) {
                    setPosition(getPosition().x - ENEMY_VELOCITY_X, getPosition().y);
                } else {
                    setPosition(getPosition().x + ENEMY_VELOCITY_X, getPosition().y);
                }

                goLeft = !goLeft;

            } else {
                platform = go;
            }
        }
        else if (go instanceof Player) {
            player = (Player) go;
        }
    }
}

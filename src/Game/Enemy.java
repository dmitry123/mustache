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

        world.createDynamic(this, x, y, 100, ENEMY_HEIGHT);
    }

    public void killEnemy() {

        isKilled = true;
        atlasActive = atlasDie;
    }



    public void changeDirectionToLeft() {

        if (!goLeft) {
            setPosition(getPosition().x - width, getPosition().y);
        }

        goLeft = true;
    }

    public void changeDiretionToRight() {

        if (goLeft) {
            setPosition(getPosition().x + width, getPosition().y);
        }

        goLeft = false;
    }

    public void swapDirection() {

        if (goLeft) {
            changeDiretionToRight();
        } else {
            changeDirectionToLeft();
        }
    }

    public void onGameObjectRender(PGraphics g) {

        if (attackTime != 0 && System.currentTimeMillis() - attackTime > ENEMY_ATTACK_DELAY) {
            // emh :(
        }

        if (!isKilled && isMovable) {

            if (goLeft) {
                setPosition(getPosition().x - ENEMY_VELOCITY_X, getPosition().y);
            } else {
                setPosition(getPosition().x + ENEMY_VELOCITY_X, getPosition().y);
            }

            if (platform != null) {

                if (getPosition().x >= platform.getPosition().x + platform.width / 2 + width / 2) {
                    changeDirectionToLeft();
                } else if (getPosition().x <= platform.getPosition().x - platform.width / 2 - width / 2) {
                    changeDiretionToRight();
                }
            }

            if (player != null) {

                if (!player.isKilled && (
                    getPosition().x - player.getPosition().x > 0 && goLeft ||
                    getPosition().x - player.getPosition().x < 0 && !goLeft
                )) {
                    atlasActive = atlasFight;

                    if (atlasActive.getIndex() == 9) {
                        player.killPlayer();
                        swapDirection();
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
                g.translate(width * 2 - 100, 0);
                g.scale(-1, 1);
                atlasActive.render(g);
                g.popMatrix();

            } else {

                g.pushMatrix();
                g.translate(100, 0);
                atlasActive.render(g);
                g.popMatrix();
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

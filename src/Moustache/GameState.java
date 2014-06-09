package Moustache;

import Common.ColorChanger;
import Common.Vertex;
import Game.GameObject;
import Game.Player;
import Game.World;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.Iterator;

public class GameState extends StateWithBack {

    private Player player;
    private World world;
    private ArrayList<Player> playerList;
    private ColorChanger colorChanger;

    public GameState(PApplet applet) {

        super(applet, "main-game");

        playerList = new ArrayList<Player>();
        colorChanger = new ColorChanger(0.2f);

        colorChanger.setFromColor(0xcccccc);

        new Thread(new GameStateSession(this)).start();
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public World getWorld() {
        return world;
    }

    public void onStateAttach() {

        EditorState editorState;

        world = null;

        if ((editorState = (EditorState) getStateMachine().find("main-editor")) != null) {
            editorState.loadLevel("level/level.txt");
        }
    }

    public void onStateLoad() {

        super.onStateLoad();

        if (world == null) {
            world = ((EditorState) getStateMachine().find("main-editor")).getWorld();
        }

        if  (world != null && player == null) {
            player = (Player) world.find("player");
        }

        if (world != null) {
            for (GameObject go : world.getList ()) {
                go.isMovable = true;
            }
        }
    }

    boolean isLeftPressed = false;
    boolean isRightPressed = false;
    boolean isJumpPressed = false;
    boolean isFightPressed = false;

    public void onStateKeyDown() {

        if (applet.key == ' ') {
            isJumpPressed = true;
        }
        if (applet.key == 'f') {
            isFightPressed = true;
        }
        if (applet.key == 'a') {
            isLeftPressed = true;
        }
        if (applet.key == 'd') {
            isRightPressed = true;
        }
    }

    public void onStateKeyUp() {

        if (applet.key == ' ') {
            isJumpPressed = false;
        }
        if (applet.key == 'f') {
            isFightPressed = false;
        }
        if (applet.key == 'a') {
            isLeftPressed = false;
        }
        if (applet.key == 'd') {
            isRightPressed = false;
        }
    }

    public void onStateTick() {

        player = (Player) world.find("player");

        if (player != null) {

            if (isJumpPressed) {
                player.doJump();
            } else if (isFightPressed) {
                player.doFight();
            }
            if (isLeftPressed || player.isLeft) {
                player.doLeft();
            } else if (isRightPressed || player.isRight) {
                player.doRight();
            } else {
                player.setIdleAnimation();
            }
        }

        for (Iterator<Player> i = playerList.iterator(); i.hasNext(); ) {

            Player p = i.next();

            if (p.getSocket() == null) {
                i.remove();
            }
        }
    }

    public void onStateRender(PGraphics g) {

        if (world != null && player != null) {

            float centerX = player.getPosition().x;
            float centerY = player.getPosition().y;

            float distanceX = applet.width;
            float distanceY = applet.height;

            if (playerList.size() > 1) {

                Player rightPlayer = player;
                Player leftPlayer = player;
                Player topPlayer = player;
                Player bottomPlayer = player;

                for (Player p : playerList) {
                    if (p.getPosition().x >= rightPlayer.getPosition().x) {
                        rightPlayer = p;
                    }
                    if (p.getPosition().x <= leftPlayer.getPosition().x) {
                        leftPlayer = p;
                    }
                    if (p.getPosition().y <= topPlayer.getPosition().y) {
                        topPlayer = p;
                    }
                    if (p.getPosition().y >= bottomPlayer.getPosition().y) {
                        bottomPlayer = p;
                    }
                }

                for (Iterator<Player> i = playerList.iterator (); i.hasNext(); ) {

                    Player p = i.next();

                    if (p.getSocket() == null && p != player) {
                        i.remove();
                    }
                }

                if (player == null) {
                    player = playerList.get(0);
                }

                distanceX = Math.abs(rightPlayer.getPosition().x - leftPlayer.getPosition().x);
                distanceX += distanceX / 2;

                distanceY = Math.abs(topPlayer.getPosition().y - bottomPlayer.getPosition().y);
                distanceY += distanceY / 2;

                centerX = (leftPlayer.getPosition().x + rightPlayer.getPosition().x) / 2;
                centerY = (bottomPlayer.getPosition().y + topPlayer.getPosition().y) / 2;
            }

            float scaleFactor = (g.width / distanceX) > (g.height / distanceY) ? (g.height / distanceY) : (g.width / distanceX);

            if (scaleFactor < 1.0) {
                g.scale(scaleFactor);
            } else {
                scaleFactor = 1.0f;
            }

            if (distanceX < g.width) {
                distanceX = g.width;
            }

            if (distanceY < g.height) {
                distanceY = g.height;
            }

            centerX = -centerX + distanceX / 2;
            centerY = -centerY + distanceY / 2;

            Vertex bo = world.getBackgroundOffset();

            g.background(
                (int) ((bo.x + 10) / 20.0 * 0xff),
                (int) ((bo.y + 10) / 20.0 * 0xff),
                (int) ((bo.z + 10) / 20.0 * 0xff)
            );

//            colorChanger.setToColor(
//                (int) ((bo.x + 10) / 20.0 * 0xff),
//                (int) ((bo.y + 10) / 20.0 * 0xff),
//                (int) ((bo.z + 10) / 20.0 * 0xff)
//            );
//            colorChanger.tickColor();
//
//            g.background(colorChanger.getCurrentColor());

            g.pushMatrix();

            if (player != null) {
                g.translate(centerX, centerY);
            }

            if (world != null) {
                world.invokeRenderMethod(g);
            }

            g.popMatrix();
        }
    }
}

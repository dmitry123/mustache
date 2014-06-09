import Common.*;

import Moustache.AnimationState;
import Moustache.MainState;
import processing.core.PApplet;

import java.net.Inet4Address;

public class Main extends PApplet implements ShutDownListener {

    WaitManager waitManager;
    StateMachine stateMachine;
    RootMachine rootMachine;
    AudioPlayer audioPlayer;
    Resource resource;

    public void onShutdown() {

        if (waitManager != null) {
            waitManager.interrupt();
        }
    }

    public void setup() {

        size(1024, 768);

        try {
            System.out.println(dataPath(""));
            System.out.println(Inet4Address.getLocalHost().getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(
                new Thread(
                        new ShutdownRunnable(this)));

        resource = Resource.getInstance();
        rootMachine = RootMachine.getInstance();

        waitManager = new WaitManager(this);
        stateMachine = new StateMachine(this);
//        audioPlayer = new AudioPlayer(this);

        resource.imagePlayerFall = loadImage("texture/player-fall.png");
        resource.imagePlayerIdle = loadImage("texture/player-idle.png");
        resource.imagePlayerJump = loadImage("texture/player-jump.png");
        resource.imagePlayerRun = loadImage("texture/player-run.png");
        resource.imagePlayerDie = loadImage("texture/player-die.png");
        resource.imagePlayerFight = loadImage("texture/player-fight.png");
        resource.imageWorldCloud = loadImage("texture/world-cloud.png");
        resource.imageWorldCloud2 = loadImage("texture/world-cloud-2.png");
        resource.imageEditorPlayer = loadImage("texture/editor-player.png");
        resource.imageEditorEraser = loadImage("texture/editor-eraser.png");
        resource.imageEditorEnemy = loadImage("texture/editor-enemy.png");
        resource.imageEnemyWalk = loadImage("texture/enemy-walk.png");
        resource.imageEnemyFight = loadImage("texture/enemy-fight.png");
        resource.imageEnemyDie = loadImage("texture/enemy-die.png");
        resource.imageAudioControl = loadImage("texture/audio-control.png");
        resource.imageAudioControl2 = loadImage("texture/audio-control-2.png");

        // attach basic states to root machine
        // main state will render menu's and
        // implement all application mechanics
        // animation state will implement
        // all animation processes which
        // depends on states
        rootMachine.attach(new MainState(this));
        rootMachine.attach(new AnimationState(this));

        frameRate(60);

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    }

    public void draw() {

        background(0xcc);

        rootMachine.invokeRenderMethod(g);

        String s = "FPS : " + (int) frameRate;

        if (frame != null) {
            frame.setTitle(s);
        }

//        fill(0x00);
//        text(s, width / 2 - textWidth(s) / 2, 40);
    }

    public void mouseClicked() { rootMachine.onMouseClick(); }
    public void mousePressed() { rootMachine.onMouseDown(); }
    public void mouseReleased() { rootMachine.onMouseUp(); }
    public void mouseEntered() { rootMachine.onMouseEnter(); }
    public void mouseExited() { rootMachine.onMouseLeave(); }
    public void mouseDragged() { rootMachine.onMouseDrag(); }
    public void mouseMoved() { rootMachine.onMouseMove(); }
    public void keyTyped() { rootMachine.onKeyType(); }
    public void keyPressed() { rootMachine.onKeyDown(); }
    public void keyReleased() { rootMachine.onKeyUp(); }

    private class ShutdownRunnable implements Runnable {

        ShutDownListener appletEx;

        ShutdownRunnable(ShutDownListener appletEx) {
            this.appletEx = appletEx;
        }

        public void run() {
            appletEx.onShutdown();
        }
    }

    public static void main(String[] stringList) {

        int length = 1;

        if (stringList != null) {
            length += stringList.length;
        }

        String[] appletArgs = new String[length];

        appletArgs[0] = "Main";

        if (stringList != null) {
            System.arraycopy(stringList, 0, appletArgs, 1, stringList.length);
        }

        PApplet.main(appletArgs);
    }
}
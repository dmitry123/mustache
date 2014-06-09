package Moustache;

import Common.*;
import Game.*;
import Interface.InterfaceButton;
import Interface.InterfaceEventListener;
import Interface.InterfaceMenu;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class EditorState extends StateWithBack {

    static final int EDITOR_GRID = 50;
    static final int EDITOR_PLAYER = 0;
    static final int EDITOR_CLOUD = 1;
    static final int EDITOR_GROUND = 2;
    static final int EDITOR_BOSS = 3;

    private Vertex center = new Vertex();
    private Vertex offset = new Vertex();

    private InterfaceMenu interfaceMenu;
    private InterfaceMenu saveInterfaceMenu;
    private InterfaceButton interfaceButton;
    private World world;
    private Player player;
    private InterfaceEventListener interfaceEventListener;

    private int active = 0;

    public EditorState(PApplet applet) {

        super(applet, "main-editor");

        interfaceEventListener = new InterfaceEventListener(applet);
        interfaceMenu = new InterfaceMenu(applet, interfaceEventListener, 0, 0, 100, 25);
        saveInterfaceMenu = new InterfaceMenu(applet, interfaceEventListener, 0, 0, 150, 40);

        world = new World(applet);

        if ((player = (Player) world.find("player")) != null) {
            player.isMovable = false;
        }
    }

    public void onStateAttach() {

        Resource r = Resource.getInstance();

        interfaceMenu.attach("Player").setUserData(r.imageEditorPlayer);
        interfaceMenu.attach("Enemy").setUserData(r.imageEditorEnemy);
        interfaceMenu.attach("Cloud").setUserData(r.imageWorldCloud);
        interfaceMenu.attach("Cloud 2").setUserData(r.imageWorldCloud2);
        interfaceMenu.attach("Boss").setUserData(r.imageEditorPlayer);
        interfaceMenu.attach("Eraser").setUserData(r.imageEditorEraser);

        saveInterfaceMenu.attach("Save");
        saveInterfaceMenu.attach("Load");

        interfaceMenu.alignTo(InterfaceMenu.Align.LeftBottom);
        saveInterfaceMenu.alignTo(InterfaceMenu.Align.RightTop);
    }

    public void onStateLoad() {

        super.onStateLoad();

        active = 0;
        interfaceButton = null;

        for (InterfaceButton b : interfaceMenu.getList()) {
            b.isActive = true;
        }

        for (GameObject go : world.getList()) {
            go.isMovable = false;
        }
    }

    public void onStateUnLoad() {

        super.onStateUnLoad();

        for (GameObject go : world.getList()) {
            go.isMovable = true;
        }
    }

    public void onStateTick() {

        if (applet.mousePressed && applet.mouseButton == PApplet.RIGHT) {
            center.set(applet.mouseX, applet.mouseY).sub(offset);
        }
    }

    public void onStateRender(PGraphics g) {

        g.pushMatrix();
        g.translate(center.x, center.y);
        world.invokeRenderMethod(g);
        g.popMatrix();

        g.pushMatrix();
        g.fill(0xff000000);
        g.stroke(0xff000000);
        g.strokeWeight(1);
        g.translate(center.x % EDITOR_GRID, center.y % EDITOR_GRID);

        for (int i = 0; i <= applet.width; i += EDITOR_GRID) {
            g.line(i, -EDITOR_GRID, i, applet.height + EDITOR_GRID);
        }
        for (int i = 0; i <= applet.height; i += EDITOR_GRID) {
            g.line(-EDITOR_GRID, i, applet.width + EDITOR_GRID, i);
        }

        g.popMatrix();

        if (interfaceButton != null && interfaceButton.getUserData() != null) {

            PImage image = (PImage) interfaceButton.getUserData();

            g.pushMatrix();
            g.translate(applet.mouseX - image.width / 2, applet.mouseY - image.height / 2);
            g.image(image, 0, 0);
            g.popMatrix();
        }

        saveInterfaceMenu.invokeRenderMethod(g);
        interfaceMenu.invokeRenderMethod(g);
    }

    public void onStateMouseDown() {

        if (applet.mouseButton == PApplet.RIGHT) {
            offset.set(applet.mouseX - center.x, applet.mouseY - center.y);
        }
    }

    boolean doBoxesIntersect(GameObject a, float x, float y, int width, int height) {

        return
            (Math.abs(a.getPosition().x - x) * 2 < (a.width + width)) &&
            (Math.abs(a.getPosition().y - y) * 2 < (a.height + height));
    }

    public void onStateMouseClick() {

        InterfaceButton activeInterfaceButton;

        if ((activeInterfaceButton = interfaceMenu.getActiveButton()) != null) {

            if (interfaceButton != null) {
                interfaceButton.isActive = true;
            }

            interfaceButton = activeInterfaceButton;
            active = activeInterfaceButton.id;

            interfaceButton.isActive = false;
        }
        else if ((activeInterfaceButton = saveInterfaceMenu.getActiveButton()) != null) {

            if (activeInterfaceButton.id == 0) {
                saveLevel("data/level/level.txt");
            }
            else {
                loadLevel("data/level/level.txt");
            }
        }
        else if (applet.mouseButton == PApplet.LEFT && interfaceButton != null) {

            if (active == 0) {

                if (player == null) {
                    player = new Player(world, -center.x + applet.mouseX, -center.y + applet.mouseY, true);
                }

                player.setPosition(-center.x + applet.mouseX, -center.y + applet.mouseY);
            }
            else if (active == 1) {

                new Enemy(world, -center.x + applet.mouseX, -center.y + applet.mouseY);
            }
            else if (active == 5) {

                GameObject minGo = null;

                for (GameObject go : world.getList ()) {
                    if (doBoxesIntersect(go, -center.x + applet.mouseX, -center.y + applet.mouseY, 100, 100)) {
                        minGo = go;
                        break;
                    }
                }

                if (minGo != null) {

                    if (player == minGo) {
                        player = null;
                    }

                    world.detach(minGo);
                }
            }
            else {

                new Platform(world, (PImage) interfaceButton.getUserData(), -center.x + applet.mouseX, -center.y + applet.mouseY);
            }
        }
    }

    public World getWorld() {
        return this.world;
    }

    private static final int PLAYER = 0;
    private static final int ENEMY = 1;
    private static final int PLATFORM = 2;

    public void loadLevel(String fileName) {

        JSONObject json = applet.loadJSONObject(fileName);
        JSONArray array;

        Resource r = Resource.getInstance();

        int amount = json.getInt("amount");
        array = json.getJSONArray("data");

        world = new World(applet);

        for (int i = 0; i < array.size (); i++) {

            JSONObject value = array.getJSONObject(i);

            int id = value.getInt("id");
            float x = value.getFloat("x");
            float y = value.getFloat("y");
            float width = value.getFloat("width");
            float height = value.getFloat("width");

            if (id == PLAYER) {
                new Player(world, x, y);
            }
            else if (id == ENEMY) {
                new Enemy(world, x, y);
            }
            else if (id == PLATFORM) {
                if (Math.abs(width - r.imageWorldCloud.width) < Math.abs(width - r.imageWorldCloud2.width)) {
                    new Platform(world, r.imageWorldCloud, x, y);
                }
                else {
                    new Platform(world, r.imageWorldCloud2, x, y);
                }
            }
        }

        for (GameObject go : world.getList()) {
            go.isMovable = false;
        }
    }

    public void saveLevel(String fileName) {

        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();

        json.setInt("amount", world.getList().size());

        int counter = 0;

        for (GameObject go : world.getList ()) {

            int id = -1;

            if (go instanceof Player) {
                id = PLAYER;
            }
            else if (go instanceof Enemy) {
                id = ENEMY;
            }
            else if (go instanceof Platform) {
                id = PLATFORM;
            }

            array.setJSONObject(counter++, new JSONObject()
                    .setInt("id", id)
                    .setFloat("x", go.getPosition().x)
                    .setFloat("y", go.getPosition().y)
                    .setFloat("width", go.width)
                    .setFloat("height", go.height));
        }

        json.setJSONArray("data", array);

        applet.saveJSONObject(json, fileName);
    }
}

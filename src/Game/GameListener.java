package Game;

import processing.core.PGraphics;

interface GameListener {

    public void onGameObjectAttach();
    public void onGameObjectDetach();
    public void onGameObjectRender(PGraphics g);
    public void onGameObjectCollide(GameObject object);
    public void onGameObjectOutOfWorld();
    public void onGameObjectLeave(GameObject object);
}
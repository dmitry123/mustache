package Common;

import processing.core.PImage;

public class Resource {

    private static Resource resource = new Resource();

    public PImage imagePlayerFall;
    public PImage imagePlayerIdle;
    public PImage imagePlayerJump;
    public PImage imagePlayerRun;
    public PImage imagePlayerDie;
    public PImage imagePlayerFight;
    public PImage imageWorldCloud;
    public PImage imageWorldCloud2;
    public PImage imageEditorPlayer;
    public PImage imageEditorEraser;
    public PImage imageEditorEnemy;
    public PImage imageEnemyWalk;
    public PImage imageEnemyFight;
    public PImage imageEnemyDie;
    public PImage imageAudioControl;
    public PImage imageAudioControl2;

    private Resource() {
    }

    public static Resource getInstance() {
        return resource;
    }
}

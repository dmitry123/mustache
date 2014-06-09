package Common;

import ddf.minim.Minim;
import processing.core.PApplet;

import java.io.File;
import java.util.ArrayList;

public class AudioPlayer {

    private Minim minim;
    private ArrayList<String> playList;
    private ddf.minim.AudioPlayer audioPlayer = null;
    private int index = 0;

    public AudioPlayer(PApplet applet) {

        playList = new ArrayList<String>();
        File folder = new File(applet.dataPath("audio"));
        minim = new Minim(applet);

        String[] listOfFiles = folder.list();

        if (listOfFiles != null) {
            for (String fileName : listOfFiles) {
                if (fileName.toLowerCase().endsWith(".mp3")) {
                    playList.add(applet.dataPath("audio/" + fileName));
                }
            }
        }

        System.out.println("\nAudio Files (" + playList.size() + ") : {");
        for (String s : playList) {
            System.out.println(" - " + s);
        }
        System.out.println("}");

        if (playList.size() != 0) {
            audioPlayer = minim.loadFile(playList.get(0));
        }

        if (audioPlayer != null) {
            audioPlayer.play();
        }
    }

    public void next() {

        if (index >= playList.size()) {
            index = 0;
        }

        if (playList.size() > 1 || audioPlayer == null) {
            audioPlayer = minim.loadFile(playList.get(index));
        }
        else if (audioPlayer != null) {
            audioPlayer.rewind();
        }
    }

    public void render() {

        if (audioPlayer.position() >= audioPlayer.length()) {
            next();
        }
    }
}

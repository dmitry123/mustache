package Moustache;

import Common.UserInfo;
import Interface.InterfaceButton;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import gab.opencv.OpenCV;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.video.Capture;

import java.awt.*;

public class TakePictureState extends StateWithBack {

    final String TEXT_WAIT = "Waiting for Camera";
    final String TEXT_CANT_FIND = "Can't find face";
    final String TEXT_ONE_FACE = "Only one face should be in camera";
    final String TEXT_DEVICE_ERROR = "Camera device is not detected";
    final String TEXT_CAPTURE_DEFAULT = "Look at the camera";
    final String TEXT_CAPTURE_LEFT = "Turn your head a bit left";
    final String TEXT_CAPTURE_RIGHT = "Turn you head a bit right";
    final String TEXT_CAPTURE_SMILE = "Make smiled face";
    final String TEXT_CAPTURE_SORROW = "Make sorrowed face";

    // basic attributes
    private Capture video;
    private OpenCV cvFace;
    private Minim minim;
    private AudioPlayer player;
    private UserInfo user;
    private InterfaceButton nextInterfaceButton;
    private Thread thread;

    // camera sub-window scale
    final private int CAMERA_WIDTH = applet.width - applet.width / 2;
    final private int CAMERA_HEIGHT = applet.height - applet.height / 2;

    // boolean states
    public boolean isVideoStarted = false;
    public boolean isClosingState = false;
    public boolean isVideoSuspended = false;

    // detected faces
    private Rectangle[] faces;
    private Rectangle[] eyes;

    // current image array index
    private int imageIndex = 0;

    // take picture button
    private InterfaceButton takePictureInterfaceButton;

    public TakePictureState(PApplet applet, UserInfo user) {

        // initialize state
        super(applet, "main-pic");

        // copy user
        this.user = user;

        // create OpenCV object
        cvFace = new OpenCV(applet, CAMERA_WIDTH, CAMERA_HEIGHT);

        // try to create Capture object
        try {
            video = new Capture(applet, CAMERA_WIDTH, CAMERA_HEIGHT);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // create minim object and load shot sound
        minim = new Minim(applet);
        player = minim.loadFile("sound/shot.wav");

        // load haar's cascades
        cvFace.loadCascade("haarcascade_frontalface_alt.xml");

        // take a shot button
        takePictureInterfaceButton = new InterfaceButton(applet, getInterfaceEventListener(), applet.width / 2 - 150 - 4, applet.height / 2 + CAMERA_HEIGHT / 2 + 50, 150, 40, "Take a Shot");
        nextInterfaceButton = new InterfaceButton(applet, getInterfaceEventListener(), applet.width / 2 + 4, applet.height / 2 + CAMERA_HEIGHT / 2 + 50, 150, 40, "Next");
        takePictureInterfaceButton.isActive = false;
        nextInterfaceButton.isActive = false;

        // create new thread for capture device
        if (video != null) {
            thread = new Thread(new TakePictureRunnable(this));
            thread.start();
        }
    }

    private class TakePictureRunnable implements Runnable {

        TakePictureState takePictureState;

        TakePictureRunnable(TakePictureState takePictureState) {
            this.takePictureState = takePictureState;
        }

        public void run() {

            while (takePictureState.video != null) {

                try {
                    Thread.currentThread().sleep(250);
                } catch (InterruptedException e) {
                    // ignore
                }

                if (!takePictureState.isVideoSuspended) {
                    takePictureState.cvFace.loadImage(takePictureState.video);
                    takePictureState.faces = takePictureState.cvFace.detect();
                }
            }
        }
    }

    public void onStateLoad() {

        super.onStateLoad();

        // default state
        isVideoStarted = false;

        // if we have capture object, then create new thread
        // and start camera detecting with 1 second
        // delay to avoid slow animation process
        if (video != null) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.currentThread().sleep(1000);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    video.start();
                    isVideoStarted = true;
                    nextInterfaceButton.isActive = true;
                }
            }
            ).start();
        } else {
            isVideoStarted = true;
            nextInterfaceButton.isActive = true;
        }

        // i know, sorry :(
        if (thread != null) {
            thread.resume();
        }
    }

    public void onStateUnLoad() {

        super.onStateUnLoad();

        // if we have started capture then
        // stop it and disable flags
        if (isVideoStarted && video != null) {
            video.stop(); isVideoStarted = false;
        }

        // i know, sor±ry :(
        if (thread != null) {
            thread.suspend();
        }
    }

    public void playCameraSound() {

        // create new thread to start sound
        // i do it in another thread cuz that
        // method calls from mouse click event
        // and i don't know where to stop sound,
        // so it starts async
        new Thread(new Runnable() {
            public void run() {
                player.play();
                try {
                    Thread.currentThread().sleep(player.length());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                player.pause();
                player.rewind();
            }
        }
        ).start();
    }

    public void onStateMouseClick() {

        // check is button active and we've pressed take picture button
        if (takePictureInterfaceButton.isMouseOver()) {

            playCameraSound();

            // if we have capture object,
            // load pixels
            if (video != null) {
                video.loadPixels();
            }

            // get rectangle object, flag
            // isButtonActive won't be true if
            // we have 0 or above then 1 faces
            Rectangle r = faces[0];

            // extra scale for image
            int extra = (int)(1.0 * r.width / 5.0);

            // applying extra scale
            r.x -= extra;
            r.width += extra * 2;
            r.y -= extra * 1.5;
            r.height += extra * 2;

            int w = r.width;
            int h = r.width;

            // create image with new scale
            PImage image = applet.createImage(w, h, PApplet.RGB);

            // check captured face for window's overflow
            if (r.x + r.width > CAMERA_WIDTH) {
                r.x -= (r.x + r.width) - CAMERA_WIDTH;
            } else if (r.x < 0) {
                r.x = 0;
            }
            if (r.y + r.height > CAMERA_HEIGHT) {
                r.y -= (r.y + r.height) - CAMERA_HEIGHT;
            } else if (r.y < 0) {
                r.y = 0;
            }

            // copy pixels from capture frame to new image
            for (int x = 0, i = 0; x < image.pixels.length; x++, i++) {
                image.pixels[i] = video.pixels[x + r.x + CAMERA_WIDTH * r.y + (CAMERA_WIDTH - r.width) * i / r.width];
            }

            // set new user's image
            user.setImage(image, imageIndex);
        }

        if (nextInterfaceButton.isMouseOver()) {

            if (++imageIndex >= user.imageArray.length) {
                imageIndex = 0;
            }
        }
    }

    public void showMessage(String message) {

        if (message.length() == 0) {
            switch (imageIndex) {
                case 0:
                    message = TEXT_CAPTURE_DEFAULT;
                    break;
                case 1:
                    message = TEXT_CAPTURE_LEFT;
                    break;
                case 2:
                    message = TEXT_CAPTURE_RIGHT;
                    break;
                case 3:
                    message = TEXT_CAPTURE_SMILE;
                    break;
                case 4:
                    message = TEXT_CAPTURE_SORROW;
                    break;
            }
        }

        // show message at the top of the window
        if (message.length() != 0) {
            applet.fill(0xffffffff);
            applet.textSize(30);
            applet.text(message, applet.width / 2 - applet.textWidth(message) / 2, 20, applet.width, applet.height);
        }
    }

    public void onStateTick() {

    }

    public void onStateRender(PGraphics g) {

        nextInterfaceButton.isActive = (user.getImage(imageIndex) != null);

        if (!isVideoStarted) {

            showMessage(TEXT_WAIT);

        } else if (!isClosingState) {

            // check capture object
            if (video != null && video.available()) {
                video.read();
            } else {
                showMessage(TEXT_DEVICE_ERROR);
            }

            // button not active (as default)
            takePictureInterfaceButton.isActive = false;

            g.pushMatrix();
            g.translate(g.width / 2 - CAMERA_WIDTH / 2, g.height / 2 - CAMERA_HEIGHT / 2);

            // display captured frame, if we can
            if (video != null) {
                g.image(video, 0, 0);
            }

            g.noFill();
            g.stroke(0xffffffff);
            g.strokeWeight(20);
            g.rect(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
            g.stroke(0xff7b1010);
            g.strokeWeight(4);

            if (video != null && !isVideoSuspended && faces != null) {

                // display all faces
                for (Rectangle face : faces) {
                    applet.rect(face.x, face.y, face.width, face.height);
                }
            }
            g.popMatrix();

            // show error messages
            if (faces != null && !isVideoSuspended) {
                if (faces.length == 1) {
                    takePictureInterfaceButton.isActive = true;
                } else if (faces.length == 0) {
                    showMessage(TEXT_CANT_FIND);
                } else {
                    showMessage(TEXT_ONE_FACE);
                }
            }
        }
        else if (isVideoStarted) {
            showMessage("");
        }

        takePictureInterfaceButton.invokeRenderMethod(g);
        nextInterfaceButton.invokeRenderMethod(g);

        user.render();

        user.drawImage(user.getImage(UserInfo.USER_LEFT), 1);
        user.drawImage(user.getImage(UserInfo.USER_RIGHT), 2);
        user.drawImage(user.getImage(UserInfo.USER_SMILE), 3);
        user.drawImage(user.getImage(UserInfo.USER_SORROW), 4);

        g.noFill();
        g.strokeWeight(3);
        g.stroke(0xff7b1010);
        g.rect(g.width - UserInfo.USER_WIDTH - UserInfo.USER_OFFSET - 6, UserInfo.USER_OFFSET + imageIndex * (UserInfo.USER_HEIGHT + UserInfo.USER_OFFSET) - 6, UserInfo.USER_WIDTH + 10, UserInfo.USER_HEIGHT + 10);
    }
}

package Common;

import processing.core.PApplet;
import processing.core.PImage;

public class UserInfo {

    public final static int USER_WIDTH = 100;
    public final static int USER_HEIGHT = 100;
    public final static int USER_OFFSET = 10;
    public final static int USER_MAX = 5;

    public final static int USER_DEFAULT = 0;
    public final static int USER_LEFT = 1;
    public final static int USER_RIGHT = 2;
    public final static int USER_SMILE = 3;
    public final static int USER_SORROW = 4;

    public PImage[] imageArray = new PImage[5];
    public String name = "";

    public PImage getDefaultImage() {
        return imageArray[0];
    }

    public PImage getImage(int index) {

        if (index > -1 && index < imageArray.length) {
            return imageArray[index];
        }

        return null;
    }

    public void setImage(PImage image, int index) {

        if (index > -1 && index < imageArray.length) {
            imageArray[index] = image;
        }
    }

    private PApplet applet;

    public UserInfo(PApplet applet) {
        this.applet = applet;
    }

    public void showMessage(String message) {

        // show text message
        applet.fill(0xff000000);
        applet.textSize(20);
        applet.text(message, USER_WIDTH / 2 - applet.textWidth(message) / 2, USER_HEIGHT / 2 - 10, USER_WIDTH, USER_HEIGHT);
    }

    public void drawImage(PImage image, int offsetY) {

        // string constants
        final String TEXT_NO_IMAGE = "No Image";

        // render information
        // about user with photo
        applet.pushMatrix();
        applet.translate(applet.width - USER_WIDTH - USER_OFFSET, USER_OFFSET + offsetY * (USER_HEIGHT + USER_OFFSET));
        applet.fill(0xffffffff);
        applet.strokeWeight(3);
        applet.stroke(0xff107b10);
        applet.rect(-2, -2, USER_WIDTH + 2, USER_HEIGHT + 2);

        // if we have self image
        // then render it else
        // show message with error
        if (image != null) {

            applet.pushMatrix();
            applet.scale((float) USER_WIDTH / (float) image.width);
            applet.image(image, 0, 0);
            applet.popMatrix();

        } else {
            showMessage(TEXT_NO_IMAGE);
        }

        applet.popMatrix();
    }

    public void render() {
        drawImage(getDefaultImage(), 0);
    }
}

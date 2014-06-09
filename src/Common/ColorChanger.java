package Common;

public class ColorChanger {

    class RGB {

        public int red;
        public int green;
        public int blue;

        public RGB(int r, int g, int b) {

            red = r;
            green = g;
            blue = b;
        }
    }

    RGB colorFrom;
    RGB colorTo;

    float interval;

    public ColorChanger(float interval) {
        this.interval = interval;
    }

    public void setFromColor(int color) {

        colorFrom = new RGB(
            (color >> 16) & 0xff,
            (color >>  8) & 0xff,
            (color >>  0) & 0xff);
    }

    public void setToColor(int color) {

        colorTo = new RGB(
            (color >> 16) & 0xff,
            (color >>  8) & 0xff,
            (color >>  0) & 0xff);
    }

    public void setToColor(int r, int g, int b) {

        colorTo = new RGB(r, g, b);
    }

    public int getCurrentColor() {

        return
            (colorFrom.red << 16) |
            (colorFrom.green << 8) |
            (colorFrom.blue << 0);
    }

    public void tickColor() {

        colorFrom.red = (int) (interval * (colorTo.red - colorFrom.red));
        colorFrom.green = (int) (interval * (colorTo.green - colorFrom.green));
        colorFrom.blue = (int) (interval * (colorTo.blue - colorFrom.blue));
    }
}

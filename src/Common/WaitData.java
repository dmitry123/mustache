package Common;

public class WaitData {

    public long interval = 0;
    public long id = 0;
    public long repeats = 0;
    public long lastTime = 0;
    public long iteration = 0;

    public WaitData(long interval, long id, long repeats) {

        this.interval = interval;
        this.id = id;
        this.repeats = repeats;

        lastTime = System.currentTimeMillis();
    }
}

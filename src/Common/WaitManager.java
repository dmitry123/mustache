package Common;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class WaitManager {

    private Object delegate = null;
    private Method onWaitTimeOut = null;
    private Thread thread = null;
    private Condition conditionVariable;
    private long lowestInterval = 0xffffffff;
    private ArrayDeque<WaitData> waitDataDeque = null;
    private ReentrantLock reentrantLock = null;

    public ReentrantLock getLock() { return reentrantLock; }
    public Condition getConditionVariable() { return conditionVariable; }
    public long getLowestInterval() { return lowestInterval; }
    public ArrayDeque<WaitData> getDataDeque() { return waitDataDeque; }
    public Method getOnWaitTimeOut() { return onWaitTimeOut; }
    public Object getDelegate() { return delegate; }

    public WaitManager(Object delegate) {

        waitDataDeque = new ArrayDeque<WaitData>();
        thread = new Thread(new WaitSession(this));
        reentrantLock = new ReentrantLock();
        conditionVariable = reentrantLock.newCondition();

        this.delegate = delegate;

        try {
            onWaitTimeOut = delegate.getClass().getMethod("onWaitTimeOut", WaitData.class);
        } catch (NoSuchMethodException e) {
            // ignoring exception
        }

        thread.start();
    }

    public void RemoveData(WaitData waitData) {

        if (waitData.interval == lowestInterval) {

            lowestInterval = 0xffffffff;

            for (WaitData d : waitDataDeque) {
                if (d != waitData && d.interval < lowestInterval) {
                    lowestInterval = d.interval;
                }
            }
        }

        waitDataDeque.remove(waitData);
    }

    public void waitFor(long id, long interval) {
        waitFor(id, interval, 1);
    }

    public void waitForInfinity(long id, long interval) {
        waitFor(id, interval, 0xffffffff);
    }

    public void waitFor(long id, long interval, long repeats) {

        if (lowestInterval == 0) {
            lowestInterval = interval;
        }
        else {
            if (interval < lowestInterval) {
                lowestInterval = interval;
            }
        }

        waitDataDeque.add(new WaitData(interval, id, repeats));

        reentrantLock.lock();
        conditionVariable.signal();
        reentrantLock.unlock();
    }

    public void interrupt() {

        if (thread != null) {
            thread.interrupt();
        }
    }
}

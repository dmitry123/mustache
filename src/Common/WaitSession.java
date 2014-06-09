package Common;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class WaitSession implements Runnable {

    private WaitManager waitWaitManager;

    WaitSession(WaitManager waitWaitManager) {
        this.waitWaitManager = waitWaitManager;
    }

    public void run() {

        Thread t = Thread.currentThread();
        Condition conditionVariable = waitWaitManager.getConditionVariable();
        Method method = waitWaitManager.getOnWaitTimeOut();
        Object delegate = waitWaitManager.getDelegate();

        while (t.isAlive()) {

            try {
                waitWaitManager.getLock().lock();
                conditionVariable.await(waitWaitManager.getLowestInterval(), TimeUnit.MILLISECONDS);
                waitWaitManager.getLock().unlock();
            }
            catch (InterruptedException e) {
                // ignoring interruption
            }

            long currentTime = System.currentTimeMillis();

            for (WaitData wmd : waitWaitManager.getDataDeque()) {

                if (currentTime - wmd.lastTime > wmd.interval) {

                    wmd.lastTime = currentTime;

                    try {
                        if (method != null && delegate != null) {
                            method.invoke(delegate, wmd);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (++wmd.iteration == wmd.repeats) {
                        waitWaitManager.RemoveData(wmd);
                    }
                }
            }
        }
    }
}

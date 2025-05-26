package main;

import java.util.function.Supplier;

abstract public class BaseAI implements Runnable {
    protected final Object lock;
    protected final Supplier<Boolean> isMovingGetter;

    public BaseAI(Object lock,  Supplier<Boolean> isMovingGetter) {
        this.lock = lock;
        this.isMovingGetter = isMovingGetter;
    }

    @Override
    public void run() {
        long time = 0;
        while (true) {
            synchronized (lock) {
                while (!isMovingGetter.get()) {
                    try {
                        time = 0;
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            movingAnts(time++);

            try {
                Thread.sleep(10);
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    protected abstract void movingAnts(long time);
}

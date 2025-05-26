package main;

import java.util.function.Supplier;

public class WorkerAI extends BaseAI {
    public WorkerAI(Object lock,  Supplier<Boolean> isMovingGetter) {
        super(lock, isMovingGetter);
    }

    @Override
    protected void movingAnts(long time) {

        synchronized (AntCollection.ants().antVector()) {
            for (AbstractAnt ant : AntCollection.ants().antVector()) {
                if (ant instanceof WorkerAnt) {
                    ant.move(time);
                }
            }
        }
    }
}

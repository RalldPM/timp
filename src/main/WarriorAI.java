package main;

import java.util.function.Supplier;

public class WarriorAI extends BaseAI {
    public WarriorAI(Object lock,  Supplier<Boolean> isMovingGetter) {
        super(lock, isMovingGetter);
    }

    @Override
    protected void movingAnts(long time) {

        synchronized (AntCollection.ants().antVector()) {
            for (AbstractAnt ant : AntCollection.ants().antVector()) {
                if (ant instanceof WarriorAnt) {
                    ant.move(time);
                }
            }
        }
    }
}

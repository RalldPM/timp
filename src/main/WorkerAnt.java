package main;

public class WorkerAnt extends AbstractAnt {

    public static int lifeTime = 10;

    public WorkerAnt(int posX, int posY, SpawnTimer st, int id) {
        super("Рабочий", posX, posY, st, id);
    }

    public static void setLifeTime(int lifeTime_) {
        lifeTime = lifeTime_;
    }
    public static int getLifeTime() {return lifeTime;}
}

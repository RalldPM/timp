package main;

public class WarriorAnt extends AbstractAnt {

    private static int lifeTime = 10;

    public WarriorAnt(int posX, int posY, SpawnTimer st, int id) {
        super("Военный", posX, posY, st, id);
    }

    public static void setLifeTime(int lifeTime_) {lifeTime = lifeTime_;}
    public static int getLifeTime() {return lifeTime;}
}

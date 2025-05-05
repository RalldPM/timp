package main;

import javafx.application.Platform;

public class WarriorAnt extends AbstractAnt {

    private static int lifeTime = 10;
    private static double R;
    private static double V;
    private double rotX;
    private double rotY;

    public WarriorAnt(double posX, double posY, SpawnTimer st, int id) {
        super(Professions.WARRIOR, posX, posY, st, id);
        rotX = posX;
        rotY = posY + R;
    }

    public static void setLifeTime(int lifeTime_) {
        lifeTime = lifeTime_;
    }

    public static int getLifeTime() {
        return lifeTime;
    }

    public static void setV(double v) {
        V = v;
    }

    public static void setR(double r) {
        R = r;
    }

    public static double getR() {
        return R;
    }

    public static double getV() {
        return V;
    }

    @Override
    public void move(long time) {
        if (!(R == 0)) {
            double fi = V / (3.14 * R) * time;
            double h = 2 * R * Math.sin(fi / 2);
            double x = R * Math.sin(fi);
            double y = Math.sqrt(h * h - x * x);
            Platform.runLater(() -> {
                visualObject.setCenterX(rotX + x);
                visualObject.setCenterY(rotY + y);
            });
        }
    }
}

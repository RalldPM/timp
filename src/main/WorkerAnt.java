package main;

import javafx.application.Platform;

public class WorkerAnt extends AbstractAnt {

    private static int lifeTime = 10;
    private static double V;
    private final double bornX;
    private final double gX, gY, S;
    private boolean isGoInCorner;

    public WorkerAnt(double posX, double posY, SpawnTimer st, int id) {
        super(Professions.WORKER, posX, posY, st, id);
        this.bornX = posX;
        this.gX = 1080 - posX;
        this.gY = 720 - posY;
        this.S = Math.sqrt(gX * gX + gY * gY);
        this.isGoInCorner = false;
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

    public static double getV() {
        return V;
    }

    @Override
    public void move(long time) {
        double x = V * gX / S;
        double y = V * gY / S;

        if (visualObject.getCenterX() > 1080) {
            isGoInCorner = false;
        }
        if (visualObject.getCenterX() < bornX) {
            isGoInCorner = true;
        }

        Platform.runLater(() -> {
            if (isGoInCorner) {
                visualObject.setCenterX(visualObject.getCenterX() + x);
                visualObject.setCenterY(visualObject.getCenterY() + y);
            }
            else {
                visualObject.setCenterX(visualObject.getCenterX() - x);
                visualObject.setCenterY(visualObject.getCenterY() - y);
            }
        });
    }
}

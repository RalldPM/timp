package main;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public abstract class AbstractAnt implements IBehaviour {

    private final Circle visualObject = new Circle();
    private int bornMoment;
    private int id;

    public AbstractAnt(Professions prof, double posX, double posY, SpawnTimer st, int id) {
        if (prof == Professions.WARRIOR) {
            this.visualObject.setFill(Color.RED);
        }
        else if (prof == Professions.WORKER) {
            this.visualObject.setFill(Color.BLUE);
        }
        this.visualObject.setRadius(10);
        this.visualObject.setCenterX(posX);
        this.visualObject.setCenterY(posY);
        this.bornMoment = st.getTime();
        this.id = id;
    }

    public Circle getVisualObject() {
        return visualObject;
    }

    public int getId() {
        return id;
    }

    public int getBornMoment() {
        return bornMoment;
    }

    public void setBornMoment(int bornMoment) {
        this.bornMoment = bornMoment;
    }
}

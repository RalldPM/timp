package main;

import javafx.scene.text.Text;

public abstract class AbstractAnt implements IBehaviour {

    private final Text profession;

    public AbstractAnt(String profession,
                       int posX, int posY) {
        this.profession = new Text(profession);
        this.profession.setX(posX);
        this.profession.setY(posY);
    }

    public Text getProfession() {
        return profession;
    }
}

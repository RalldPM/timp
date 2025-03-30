package main;

import javafx.scene.text.Text;

public abstract class AbstractAnt implements IBehaviour {

    private final Text profession;
    private int bornMoment;
    private int id;

    public AbstractAnt(String profession, int posX, int posY, SpawnTimer st, int id) {
        this.profession = new Text(profession);
        this.profession.setX(posX);
        this.profession.setY(posY);
        this.bornMoment = st.getTime();
        this.id = id;
    }

    public Text getProfession() {
        return profession;
    }

    public int getId() {
        return id;
    }

    public int getBornMoment() {
        return bornMoment;
    }
}

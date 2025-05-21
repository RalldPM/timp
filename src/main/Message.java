package main;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private final int idOfRecipient;
    private ArrayList<Integer[]> aboutAnts;

    public Message(int recip, ArrayList<Integer[]> aboutAnts) {
        this.idOfRecipient = recip;
        this.aboutAnts = aboutAnts;
    }

    public int getIdOfRecipient() {
        return idOfRecipient;
    }

    public ArrayList<Integer[]> getAboutAnts() {
        return aboutAnts;
    }
}

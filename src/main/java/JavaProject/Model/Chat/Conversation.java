package JavaProject.Model.Chat;

import JavaProject.Model.Database.IDGenerator;

import java.util.ArrayList;

public class Conversation {

    private String ID;
    private String firstSide;
    private String secondSide;
    private ArrayList<Message> mesaages = new ArrayList<>();

    public Conversation(String firstSide, String secondSide) {
        this.firstSide = firstSide;
        this.secondSide = secondSide;
        this.ID = IDGenerator.getGeneratedID("CONVERSATION");
    }

    public String getFirstSide() {
        return firstSide;
    }

    public void setFirstSide(String firstSide) {
        this.firstSide = firstSide;
    }

    public String getSecondSide() {
        return secondSide;
    }

    public void setSecondSide(String secondSide) {
        this.secondSide = secondSide;
    }

    public ArrayList<Message> getMesaages() {
        return mesaages;
    }

    public void setMesaages(ArrayList<Message> mesaages) {
        this.mesaages = mesaages;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

}

package JavaProject.Model.Log;

import JavaProject.Model.Database.IDGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SellLog {

    private String ID;
    private String buyerUsername;
    private String sellDate;
    private double gainedAmount;
    private double decreasedAmount;
    private ArrayList<ProductOnLog> soldProducts;

    public SellLog(String buyerUsername, double gainedAmount, double decreasedAmount, ArrayList<ProductOnLog> soldProducts) {
        this.buyerUsername = buyerUsername;
        this.gainedAmount = gainedAmount;
        this.decreasedAmount = decreasedAmount;
        this.soldProducts = soldProducts;
        ID = IDGenerator.getGeneratedID("SELLLOG");
        sellDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getBuyerUsername() {
        return buyerUsername;
    }

    public void setBuyerUsername(String buyerUsername) {
        this.buyerUsername = buyerUsername;
    }

    public String getSellDate() {
        return sellDate;
    }

    public void setSellDate(String sellDate) {
        this.sellDate = sellDate;
    }

    public double getGainedAmount() {
        return gainedAmount;
    }

    public void setGainedAmount(double gainedAmount) {
        this.gainedAmount = gainedAmount;
    }

    public double getDecreasedAmount() {
        return decreasedAmount;
    }

    public void setDecreasedAmount(double decreasedAmount) {
        this.decreasedAmount = decreasedAmount;
    }

    public ArrayList<ProductOnLog> getSoldProducts() {
        return soldProducts;
    }

    public void setSoldProducts(ArrayList<ProductOnLog> soldProducts) {
        this.soldProducts = soldProducts;
    }
}

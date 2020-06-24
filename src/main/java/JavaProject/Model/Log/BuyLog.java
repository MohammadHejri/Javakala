package JavaProject.Model.Log;

import JavaProject.Model.Database.IDGenerator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BuyLog {

    private String ID;
    private String buyDate;
    private double paidAmount;
    private double decreasedAmount;
    private ArrayList<ProductOnLog> boughtProducts;

    public BuyLog(double paidAmount, double decreasedAmount, ArrayList<ProductOnLog> boughtProducts) {
        this.paidAmount = paidAmount;
        this.decreasedAmount = decreasedAmount;
        this.boughtProducts = boughtProducts;
        ID = IDGenerator.getGeneratedID("BUYLOG");
        buyDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getDecreasedAmount() {
        return decreasedAmount;
    }

    public void setDecreasedAmount(double decreasedAmount) {
        this.decreasedAmount = decreasedAmount;
    }

    public ArrayList<ProductOnLog> getBoughtProducts() {
        return boughtProducts;
    }

    public void setBoughtProducts(ArrayList<ProductOnLog> boughtProducts) {
        this.boughtProducts = boughtProducts;
    }
}

package JavaProject.Model.Discount;

import JavaProject.Model.Database.Database;
import JavaProject.Model.Database.IDGenerator;
import JavaProject.Model.Status.Status;

import java.util.ArrayList;

public class Auction {

    private String ID;
    private String sellerUsername;
    private String startDate;
    private String endDate;
    private Status status = Status.PENDING;
    private double discountPercent;
    private ArrayList<String> productsID;

    public Auction(String sellerUsername, String startDate, String endDate, double discountPercent, ArrayList<String> productsID) {
        this.sellerUsername = sellerUsername;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountPercent = discountPercent;
        this.productsID = productsID;
        ID = IDGenerator.getGeneratedID("AUCTION");
    }

    public Auction(Auction other) {
        ID = other.ID;
        sellerUsername = other.sellerUsername;
        startDate = other.startDate;
        endDate = other.endDate;
        status = other.status;
        discountPercent = other.discountPercent;
        productsID = new ArrayList<>(other.productsID);
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public ArrayList<String> getProductsID() {
        return productsID;
    }

    public void setProductsID(ArrayList<String> productsID) {
        this.productsID = productsID;
    }

    @Override
    public String toString() {
        return "Auction:" + "\n" +
                "ID: " + ID + "\n" +
                "Seller: " + sellerUsername + "\n" +
                "Start date: " + startDate + "\n" +
                "End date: " + endDate + "\n" +
                "Discount percent: " + discountPercent + "\n" +
                "Products: " + "\n" +
                productsNameToString();
    }

    private String productsNameToString() {
        String string = "";
        for (String ID : productsID)
            string += "    " + Database.getInstance().getProductByID(ID).getName() + "\n";
        return string;
    }
}

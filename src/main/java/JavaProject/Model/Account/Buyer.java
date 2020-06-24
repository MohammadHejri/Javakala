package JavaProject.Model.Account;

import java.util.ArrayList;

public class Buyer extends Account {

    private double balance;
    private ArrayList<String> buyLogsID = new ArrayList<>();
    private ArrayList<String> discountCodes = new ArrayList<>();

    public Buyer(String username, String password, String firstName, String lastName, String emailAddress, String phoneNumber, String imagePath) {
        super(username, password, firstName, lastName, emailAddress, phoneNumber, imagePath);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ArrayList<String> getBuyLogsID() {
        return buyLogsID;
    }

    public void setBuyLogsID(ArrayList<String> buyLogsID) {
        this.buyLogsID = buyLogsID;
    }

    public ArrayList<String> getDiscountCodes() {
        return discountCodes;
    }

    public void setDiscountCodes(ArrayList<String> discountCodes) {
        this.discountCodes = discountCodes;
    }

    @Override
    public String toString() {
        return "Buyer{" +
                "balance=" + balance +
                ", buyLogsID=" + buyLogsID +
             "} " ;
    }
}

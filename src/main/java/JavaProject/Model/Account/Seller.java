package JavaProject.Model.Account;

import JavaProject.Model.Status.Status;

import java.util.ArrayList;

public class Seller extends Account {

    private double balance;
    private String companyName;
    private Status status = Status.PENDING;
    private ArrayList<String> sellLogsID = new ArrayList<>();
    private ArrayList<String> productsID = new ArrayList<>();
    private ArrayList<String> auctionsID = new ArrayList<>();
    private ArrayList<String> requestsID = new ArrayList<>();

    public Seller(String username, String password, String firstName, String lastName, String emailAddress, String phoneNumber, String imagePath, String companyName) {
        super(username, password, firstName, lastName, emailAddress, phoneNumber, imagePath);
        this.companyName = companyName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public ArrayList<String> getSellLogsID() {
        return sellLogsID;
    }

    public void setSellLogsID(ArrayList<String> sellLogsID) {
        this.sellLogsID = sellLogsID;
    }

    public ArrayList<String> getProductsID() {
        return productsID;
    }

    public void setProductsID(ArrayList<String> productsID) {
        this.productsID = productsID;
    }

    public ArrayList<String> getAuctionsID() {
        return auctionsID;
    }

    public void setAuctionsID(ArrayList<String> auctionsID) {
        this.auctionsID = auctionsID;
    }

    public ArrayList<String> getRequestsID() {
        return requestsID;
    }

    public void setRequestsID(ArrayList<String> requestsID) {
        this.requestsID = requestsID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Seller account:\n" +
                "Username: " + username + "\n" +
                "Password: " + password + "\n" +
                "First name: " + firstName + "\n" +
                "Last name: " + lastName + "\n" +
                "Email Address: " + emailAddress + "\n" +
                "Phone number: " + phoneNumber + "\n" +
                "Company name: " + companyName + "\n";
    }
}

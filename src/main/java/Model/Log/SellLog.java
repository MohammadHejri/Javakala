package Model.Log;

import Model.ProductsOrganization.Product;

import java.util.ArrayList;
import java.util.Date;

public class SellLog extends Log {
    private int receivedCredit;
    private int decreasedPriceAtAuction;
    private ArrayList<Product> allSoldProducts;
    private String customerName;
    private enum Status {SENT, TO_BE_SENT}
    private Status status;

    public SellLog(int id, Date date, int receivedCredit, int decreasedPriceAtAuction, ArrayList<Product> allSoldProducts, String customerName, Status status) {
        super(id, date);
        this.receivedCredit = receivedCredit;
        this.decreasedPriceAtAuction = decreasedPriceAtAuction;
        this.allSoldProducts = allSoldProducts;
        this.customerName = customerName;
        this.status = status;
    }

    @Override
    public String toString() {
        return "SellLog{}";
    }
}

package Model.Log;

import Model.ProductsOrganization.Product;

import java.util.ArrayList;
import java.util.Date;

public class PurchaseLog extends Log {
    private int payedCredit;
    private int decreasedPriceByDiscount;
    private ArrayList<Product> allPurchasedProducts;
    private String sellerName;
    private enum Status {DELIVERED, TO_BE_DELIVERED}
    private Status status;

    public PurchaseLog(int id, Date date, int payedCredit, int decreasedPriceByDiscount, ArrayList<Product> allPurchasedProducts, String sellerName, Status status) {
        super(id, date);
        this.payedCredit = payedCredit;
        this.decreasedPriceByDiscount = decreasedPriceByDiscount;
        this.allPurchasedProducts = allPurchasedProducts;
        this.sellerName = sellerName;
        this.status = status;
    }

    public boolean hasBoughtProduct(int productId){}

    @Override
    public String toString() {
        return "PurchaseLog{}";
    }
}

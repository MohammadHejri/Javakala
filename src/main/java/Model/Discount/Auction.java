package Model.Discount;

import Model.ProductsOrganization.Product;
import Model.Status;

import java.util.ArrayList;
import java.util.Date;

public class Auction extends Discount {
    private int id;
    private ArrayList<Product> allIncludedProducts;
    private Status status;

    public Auction(int id, ArrayList<Product> allIncludedProducts, Status status, Date start, Date end, int percent) {
        super(start, end, percent);
        this.id = id;
        this.allIncludedProducts = allIncludedProducts;
        this.status = status;
    }

}

package Model.ProductsOrganization;

import Model.Account.Customer;

import java.util.ArrayList;

public class Cart {
    private ArrayList<Product> products;
    private Customer owner;

    public Cart(Customer owner) {
        this.owner = owner;
        products = new ArrayList<>();
    }

    public int getPayAmount(){
        return 0;
    }
    public void addProduct(Product product){}
}

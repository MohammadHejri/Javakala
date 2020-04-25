package Model.Account;

import Model.Log.PurchaseLog;

import Model.ProductsOrganization.Cart;

import java.util.ArrayList;

public class Customer extends Account {
    private Cart cart;
    private ArrayList<PurchaseLog> buyLogs = new ArrayList<>();
    public boolean hasBoughtProduct(int productId){return true;}
}
package Model.ProductsOrganization;

import Model.Account.Seller;
import Model.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class Filter {
    private String name;
    private String brand;
    private int price;
    private int remainingItems;
    private HashMap <String, Seller> allSellers;
    private HashMap <String, String> specifications;
    private Category currentCategory;

    public Filter(String name, String brand, int price, int remainingItems, HashMap<String, Seller> allSellers, HashMap<String, String> specifications, Category currentCategory) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.remainingItems = remainingItems;
        this.allSellers = allSellers;
        this.specifications = specifications;
        this.currentCategory = currentCategory;
    }

    public ArrayList<Product> filterByName(ArrayList<Product> currentProducts, String name) {
        return null;
    }

    public ArrayList<Product> filterByBrand(ArrayList<Product> currentProducts, String brand) {
        return null;
    }

    public ArrayList<Product> filterByPrice(ArrayList<Product> currentProducts, int minPrice, int maxPrice) {
        return null;
    }

    public ArrayList<Product> filterByRemaining(ArrayList<Product> currentProducts, int minRemaining) {
        return null;
    }

    public ArrayList<Product> filterBySellers(ArrayList<Product> currentProducts, ArrayList<Seller> sellers) {
        return null;
    }

    public ArrayList<Product> filterBySpecialSpec(ArrayList<Product> currentProducts, String Spec) {
        return null;
    }

    public ArrayList<Product> filterBySpecialNumeralSpec(ArrayList<Product> currentProducts, String minValue, String maxValue) {
        return null;
    }
}

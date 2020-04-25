package Controller;

import Controller.Json.JsonFileReader;
import Model.Account.Account;
import Model.ProductsOrganization.Product;

import java.util.HashMap;

public class DataCenter {
    private HashMap<String, Account> accountsByUsername = new HashMap();
    private HashMap<String, Product> productsByName = new HashMap();

    private void initAccounts(JsonFileReader reader) {
    }

    private void initProducts(JsonFileReader reader) {

    }

    private void initCategories(JsonFileReader reader) {
    }

    private void initReviews(JsonFileReader reader) {
    }

    private void initScores(JsonFileReader reader) {
    }

    public void registerAccount(Account account) {
        if (this.accountsByUsername.containsKey(account.getUsername())) {
            throw new RuntimeException("this username is not available.");
        } else {
            this.accountsByUsername.put(account.getUsername(), account);
        }
    }

    public void registerProduct(Product product) {
        if (this.productsByName.containsKey(product.getName())) {
            throw new RuntimeException("this username is not available.");
        } else {
            this.productsByName.put(product.getName(), product);
        }
    }

    public Account getAccountByName(String name) {
        return null;
    }

    public Product getProductByName(String name) {
        return null;
    }

    public Product getProductById(int id) {
    }
}

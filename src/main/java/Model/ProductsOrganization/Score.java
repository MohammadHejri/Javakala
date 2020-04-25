package Model.ProductsOrganization;

import Model.Account.Customer;

public class Score {
    private Customer customer;
    private double score;
    private Product product;

    public Score(Customer customer, double score, Product product) {
        this.customer = customer;
        this.score = score;
        this.product = product;
    }

    public void submitScoreOnProduct(Product product) {

    }

    @Override
    public String toString() {
        return "Score{}";
    }
}

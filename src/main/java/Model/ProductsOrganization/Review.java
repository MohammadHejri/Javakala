package Model.ProductsOrganization;

import Model.Account.Account;

public class Review {
    private Account account;
    private Product product;
    private String description;
    private enum Status {TO_BE_CONFIRMED, CONFIRMED, UNCONFIRMED}
    private Status status;
    private boolean isBuyer;

    public Review(Account account, Product product, String description, Status status, boolean isBuyer) {
        this.account = account;
        this.product = product;
        this.description = description;
        this.status = status;
        this.isBuyer = isBuyer;
    }

    public void setReviewOnProduct(Product product) {

    }

    @Override
    public String toString() {
        return "";
    }
}

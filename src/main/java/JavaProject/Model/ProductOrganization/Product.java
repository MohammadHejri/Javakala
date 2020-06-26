package JavaProject.Model.ProductOrganization;



import JavaProject.Model.Database.IDGenerator;
import JavaProject.Model.Status.Status;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;

public class Product {

    private String ID;
    private String name;
    private String brand;
    private int views;
    private String date;
    private double price;
    private double averageMark;
    private String sellerUsername;
    private String auctionID;
    private int remainingItems;
    private String parentCategoryName;
    private String description;
    private ArrayList<Rate> rates = new ArrayList<>();
    private ArrayList<Comment> comments = new ArrayList<>();
    private ArrayList<String> buyers = new ArrayList<>();
    private HashMap<String, String> specs;
    private Status status = Status.PENDING;
    private String imagePath;


    public Product(String name, String brand, double price, String sellerName, int remainingItems, String parentCategoryName, String description, HashMap<String, String> specs, String imagePath) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.sellerUsername = sellerName;
        this.remainingItems = remainingItems;
        this.parentCategoryName = parentCategoryName;
        this.description = description;
        this.specs = specs;
        this.imagePath = imagePath;
        ID = IDGenerator.getGeneratedID("PRODUCT");
        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAverageMark() {
        return averageMark;
    }

    public void setAverageMark(double averageMark) {
        this.averageMark = averageMark;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }

    public String getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(String auctionID) {
        this.auctionID = auctionID;
    }

    public int getRemainingItems() {
        return remainingItems;
    }

    public void setRemainingItems(int remainingItems) {
        this.remainingItems = remainingItems;
    }

    public String getParentCategoryName() {
        return parentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Rate> getRates() {
        return rates;
    }

    public void setRates(ArrayList<Rate> rates) {
        this.rates = rates;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public HashMap<String, String> getSpecs() {
        return specs;
    }

    public void setSpecs(HashMap<String, String> specs) {
        this.specs = specs;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ArrayList<String> getBuyers() {
        return buyers;
    }

    public void setBuyers(ArrayList<String> buyers) {
        this.buyers = buyers;
    }

    @Override
    public String toString() {
        return "Product:" + "\n" +
                "Name: " + name + "\n" +
                "Brand: " + brand + "\n" +
                "Price: " + price + "\n" +
                "Seller: " + sellerUsername + "\n" +
                "Remaining items: " + remainingItems + "\n" +
                "Category: " + parentCategoryName + "\n" +
                "Description: " + description + "\n" +
                "Specs:" + "\n" + specsToString();
    }

    private String specsToString() {
        String string = "";
        for (String key : specs.keySet())
            string += "    " + key + ": " + specs.get(key) + "\n";
        return string;
    }

    public void updateMark() {
        int size = rates.size();
        if (size == 0) {
            averageMark = 0;
        } else {
            double sum = 0;
            for (Rate rate : rates) {
                sum += rate.getMark();
            }
            averageMark = sum / size;
        }
    }

}

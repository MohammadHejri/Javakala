package JavaProject.Model.ProductOrganization;

import JavaProject.App;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Discount.Auction;
import JavaProject.Model.Discount.DiscountCode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Cart {
    private HashMap<Product, Integer> products = new HashMap<>();
    private DiscountCode code;
    private double price;

    public HashMap<Product, Integer> getProducts() {
        return products;
    }

    public void setProducts(HashMap<Product, Integer> products) {
        this.products = products;
    }

    public double getPrice() {
        price = 0;
        for (Product product : products.keySet()) {
            if (product.getAuctionID() == null) {
                price += products.get(product) * product.getPrice();
                continue;
            }
            try {
                Auction auction = Database.getInstance().getAuctionByID(product.getAuctionID());
                Date nowDate = new Date();
                Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(auction.getStartDate());
                Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(auction.getEndDate());
                boolean afterStart = nowDate.compareTo(startDate) > 0;
                boolean beforeEnd = nowDate.compareTo(endDate) < 0;
                if (afterStart && beforeEnd) {
                    Double afterPrice = product.getPrice() * (100 - auction.getDiscountPercent()) / 100;
                    price += afterPrice * products.get(product);
                } else if (!afterStart || !beforeEnd) {
                    price += products.get(product) * product.getPrice();
                }
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
        }
        return price;
    }

    public DiscountCode getCode() {
        return code;
    }

    public void setCode(DiscountCode code) {
        this.code = code;
    }
}

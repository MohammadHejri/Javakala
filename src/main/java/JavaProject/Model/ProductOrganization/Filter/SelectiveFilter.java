package JavaProject.Model.ProductOrganization.Filter;

import JavaProject.Model.Database.Database;
import JavaProject.Model.Discount.Auction;
import JavaProject.Model.ProductOrganization.Category;
import JavaProject.Model.ProductOrganization.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SelectiveFilter implements Filterable {
    private String name;
    private ArrayList<String> selectedValues;

    public SelectiveFilter(String name, ArrayList<String> selectedValues) {
        this.name = name;
        this.selectedValues = new ArrayList<>(selectedValues);
    }

    public boolean canPassFilter(Product product) {
        switch (name) {
            case "Name":
                return selectedValues.contains(product.getName());
            case "Brand":
                return selectedValues.contains(product.getBrand());
            case "Auction":
                if (product.getAuctionID() != null) {
                    String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                    Auction auction = Database.getInstance().getAuctionByID(product.getAuctionID());
                    boolean afterStart = nowDate.compareTo(auction.getStartDate()) >= 0;
                    boolean beforeEnd = nowDate.compareTo(auction.getEndDate()) < 0;
                    return afterStart && beforeEnd;
                } else {
                    return false;
                }
            case "Seller":
                return selectedValues.contains(product.getSellerUsername());
            case "Availability":
                return product.getRemainingItems() > 0;
            default:
                if (product.getSpecs().containsKey(name)) {
                    String value = product.getSpecs().get(name);
                    return selectedValues.contains(value);
                }
                return false;
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        String string = name + ":";
        for (String value : selectedValues)
            string += (" " + value);
        return string;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getSelectedValues() {
        return selectedValues;
    }
}

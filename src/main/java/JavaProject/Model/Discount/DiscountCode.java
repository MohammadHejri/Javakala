package JavaProject.Model.Discount;

import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Database.IDGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DiscountCode {

    private String ID;
    private String code;
    private String startDate;
    private String endDate;
    private double discountPercent;
    private double maxDiscount;
    private int maxUsageNumber;
    private HashMap<String, Integer> buyers;

    public DiscountCode(String code, String startDate, String endDate, double discountPercent, double maxDiscount, int maxUsageNumber, HashMap<String, Integer> buyers) {
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountPercent = discountPercent;
        this.maxDiscount = maxDiscount;
        this.maxUsageNumber = maxUsageNumber;
        this.buyers = buyers;
        ID = IDGenerator.getGeneratedID("CODE");
    }

    public DiscountCode(DiscountCode other) {
        ID = other.ID;
        code = other.code;
        startDate = other.startDate;
        endDate = other.endDate;
        discountPercent = other.discountPercent;
        maxDiscount = other.maxDiscount;
        maxUsageNumber = other.maxUsageNumber;
        buyers = new HashMap<>(other.buyers);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(double maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public int getMaxUsageNumber() {
        return maxUsageNumber;
    }

    public void setMaxUsageNumber(int maxUsageNumber) {
        this.maxUsageNumber = maxUsageNumber;
    }

    public HashMap<String, Integer> getBuyers() {
        return buyers;
    }

    public void setBuyers(HashMap<String, Integer> buyers) {
        this.buyers = buyers;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}

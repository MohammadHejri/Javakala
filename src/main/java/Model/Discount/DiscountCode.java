package Model.Discount;

import Model.Account.Account;

import java.util.ArrayList;
import java.util.Date;

public class DiscountCode extends Discount {
    private String code;
    private int maximumDiscountAmount;
    private int maximumNumberOfUsages;
    private ArrayList<Account> allAllowedAccounts;

    public DiscountCode(Date start, Date end, double percent, String code, int maximumDiscountAmount, int maximumNumberOfUsages, ArrayList<Account> allAllowedAccounts) {
        super(start, end, percent);
        this.code = code;
        this.maximumDiscountAmount = maximumDiscountAmount;
        this.maximumNumberOfUsages = maximumNumberOfUsages;
        this.allAllowedAccounts = allAllowedAccounts;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMaximumDiscountAmount(int maximumDiscountAmount) {
        this.maximumDiscountAmount = maximumDiscountAmount;
    }

    public void setMaximumNumberOfUsages(int maximumNumberOfUsages) {
        this.maximumNumberOfUsages = maximumNumberOfUsages;
    }
}

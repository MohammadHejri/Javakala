package JavaProject.Model.Log;

public class ProductOnLog {
    private String name;
    private String auctionID;
    private String discountCode;
    private String sellerUsername;
    private int quantity;
    private double transferredMoney;
    private double decreasedMoney;

    public ProductOnLog(String name, String auctionID, String discountCode, String sellerUsername, int quantity, double transferredMoney, double decreasedMoney) {
        this.name = name;
        this.auctionID = auctionID;
        this.discountCode = discountCode;
        this.sellerUsername = sellerUsername;
        this.quantity = quantity;
        this.transferredMoney = transferredMoney;
        this.decreasedMoney = decreasedMoney;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuctionID() {
        return auctionID;
    }

    public void setAuctionID(String auctionID) {
        this.auctionID = auctionID;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTransferredMoney() {
        return transferredMoney;
    }

    public void setTransferredMoney(double transferredMoney) {
        this.transferredMoney = transferredMoney;
    }

    public double getDecreasedMoney() {
        return decreasedMoney;
    }

    public void setDecreasedMoney(double decreasedMoney) {
        this.decreasedMoney = decreasedMoney;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }
}

package JavaProject.Model.ProductOrganization;

public class Rate {
    private String buyerName;
    private double mark;

    public Rate(String buyerName, double mark) {
        this.buyerName = buyerName;
        this.mark = mark;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }
}

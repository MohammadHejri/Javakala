package Model.Discount;

import java.util.Date;

public class Discount {
    private Date start;
    private Date end;
    private double percent;

    public Discount(Date start, Date end, double percent) {
        this.start = start;
        this.end = end;
        this.percent = percent;
    }

    private void changeDate(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}

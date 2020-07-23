package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Discount.Auction;
import JavaProject.Model.ProductOrganization.Product;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class ProductViewController implements Initializable {

    File starFull = new File("src/main/resources/Images/star-full.png");
    File starHalf = new File("src/main/resources/Images/star-half.png");
    File imageFile;

    @FXML
    AnchorPane anchorPane;
    @FXML
    ImageView productImage;
    @FXML
    ImageView auctionImage;
    @FXML
    ImageView soldImage;
    @FXML
    ImageView star1;
    @FXML
    ImageView star2;
    @FXML
    ImageView star3;
    @FXML
    ImageView star4;
    @FXML
    ImageView star5;
    @FXML
    Label productName;
    @FXML
    Label price;
    @FXML
    Label previousPrice;
    @FXML
    Label percent;
    @FXML
    Label dateLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        auctionImage.setVisible(false);
        soldImage.setVisible(false);
        percent.setVisible(false);
        previousPrice.setVisible(false);
    }

    public void initProduct(Product product) {
        productName.setText(product.getName());
        price.setText("$ " + product.getPrice());
        imageFile = new File(App.getFileData(product.getName(), "productPhoto"));
        productImage.setImage(new Image(imageFile.toURI().toString()));
        if (product.getRemainingItems() == 0)
            soldImage.setVisible(true);
        if (product.getAuctionID() != null) {
            auctionHandling(product);
        }
        double mark = product.getAverageMark();
        if (Math.abs(5.0 - mark) <= 0.25) {
            star5.setImage(new Image(starFull.toURI().toString()));
            star4.setImage(new Image(starFull.toURI().toString()));
            star3.setImage(new Image(starFull.toURI().toString()));
            star2.setImage(new Image(starFull.toURI().toString()));
            star1.setImage(new Image(starFull.toURI().toString()));
        } else if (Math.abs(4.5 - mark) <= 0.25) {
            star5.setImage(new Image(starHalf.toURI().toString()));
            star4.setImage(new Image(starFull.toURI().toString()));
            star3.setImage(new Image(starFull.toURI().toString()));
            star2.setImage(new Image(starFull.toURI().toString()));
            star1.setImage(new Image(starFull.toURI().toString()));
        } else if (Math.abs(4 - mark) <= 0.25) {
            star4.setImage(new Image(starFull.toURI().toString()));
            star3.setImage(new Image(starFull.toURI().toString()));
            star2.setImage(new Image(starFull.toURI().toString()));
            star1.setImage(new Image(starFull.toURI().toString()));
        } else if (Math.abs(3.5 - mark) <= 0.25) {
            star4.setImage(new Image(starHalf.toURI().toString()));
            star3.setImage(new Image(starFull.toURI().toString()));
            star2.setImage(new Image(starFull.toURI().toString()));
            star1.setImage(new Image(starFull.toURI().toString()));
        } else if (Math.abs(3 - mark) <= 0.25) {
            star3.setImage(new Image(starFull.toURI().toString()));
            star2.setImage(new Image(starFull.toURI().toString()));
            star1.setImage(new Image(starFull.toURI().toString()));
        } else if (Math.abs(2.5 - mark) <= 0.25) {
            star3.setImage(new Image(starHalf.toURI().toString()));
            star2.setImage(new Image(starFull.toURI().toString()));
            star1.setImage(new Image(starFull.toURI().toString()));
        } else if (Math.abs(2 - mark) <= 0.25) {
            star2.setImage(new Image(starFull.toURI().toString()));
            star1.setImage(new Image(starFull.toURI().toString()));
        } else if (Math.abs(1.5 - mark) <= 0.25) {
            star2.setImage(new Image(starHalf.toURI().toString()));
            star1.setImage(new Image(starFull.toURI().toString()));
        } else if (Math.abs(1 - mark) <= 0.25)
            star1.setImage(new Image(starFull.toURI().toString()));
        else if (Math.abs(0.5 - mark) <= 0.25)
            star1.setImage(new Image(starHalf.toURI().toString()));
    }

    private void auctionHandling(Product product) {
        Auction auction = Database.getInstance().getAuctionByID(product.getAuctionID());
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            try {
                Date nowDate = new Date();
                Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(auction.getStartDate());
                Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(auction.getEndDate());
                boolean afterStart = nowDate.compareTo(startDate) > 0;
                boolean beforeEnd = nowDate.compareTo(endDate) < 0;
                long diff = 0;
                if (!afterStart)
                    diff = startDate.getTime() - nowDate.getTime();
                else if (afterStart && beforeEnd)
                    diff = endDate.getTime() - nowDate.getTime();
                long days = diff / (1000 * 60 * 60 * 24);
                diff -= days * 1000 * 60 * 60 * 24;
                long hours = diff / (1000 * 60 * 60);
                diff -= hours * 1000 * 60 * 60;
                long minutes = diff / (1000 * 60);
                diff -= minutes * 1000 * 60;
                long seconds = diff / (1000);
                String secondsStr =  (seconds < 10 ? "0" + seconds : "" + seconds);
                String minutesStr =  (minutes < 10 ? "0" + minutes : "" + minutes);
                String hoursStr =  (hours < 10 ? "0" + hours : "" + hours);
                if (!afterStart) {
                    dateLabel.setText("Sale starts in: " + days + ":" + hoursStr + ":" + minutesStr + ":" + secondsStr);
                } else if (afterStart && beforeEnd) {
                    dateLabel.setText("Sale ends in: " + days + ":" + hoursStr + ":" + minutesStr + ":" + secondsStr);
                    Double afterPrice = product.getPrice() * (100 - auction.getDiscountPercent()) / 100;
                    previousPrice.setText("$ " + String.format("%.2f", product.getPrice()));
                    percent.setText(String.format("%.0f", auction.getDiscountPercent()) + "%");
                    price.setText("$ " + String.format("%.2f", afterPrice));
                    auctionImage.setVisible(true);
                    percent.setVisible(true);
                    previousPrice.setVisible(true);
                    dateLabel.setVisible(true);
                } else {
                    dateLabel.setText("");
                    price.setText("$ " + product.getPrice());
                    auctionImage.setVisible(false);
                    percent.setVisible(false);
                    previousPrice.setVisible(false);
                    dateLabel.setVisible(false);
                }
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}

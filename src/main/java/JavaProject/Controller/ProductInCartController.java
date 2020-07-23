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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class ProductInCartController implements Initializable {

    Product product;
    File imageFile;

    @FXML
    AnchorPane anchorPane;
    @FXML
    ImageView productImage;
    @FXML
    ImageView auctionImage;
    @FXML
    Label productName;
    @FXML
    Label price;
    @FXML
    Label previousPrice;
    @FXML
    Label percent;
    @FXML
    Label numberOfProducts;
    @FXML
    Label totalPrice;
    @FXML
    ImageView increaseButton;
    @FXML
    ImageView decreaseButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CartController.updatePrice();
        auctionImage.setVisible(false);
        percent.setVisible(false);
        previousPrice.setVisible(false);
        increaseButton.setOnMouseClicked(this::increaseProduct);
        decreaseButton.setOnMouseClicked(this::decreaseProduct);
    }

    public void initProduct(Product product) {
        this.product = product;
        productName.setText(product.getName());
        price.setText("$ " + product.getPrice());
        int quantity = App.getCart().getProducts().get(product);
        if (quantity > product.getRemainingItems()) {
            App.getCart().getProducts().replace(product, product.getRemainingItems());
            quantity = product.getRemainingItems();
            increaseButton.setVisible(false);
        }
        totalPrice.setText("Total: $ " + String.format("%.2f", product.getPrice() * quantity));
        numberOfProducts.setText("" + quantity);
        imageFile = new File(App.getFileData(product.getName(), "productPhoto"));
        productImage.setImage(new Image(imageFile.toURI().toString()));
        if (product.getAuctionID() != null) {
            auctionHandling(product);
        }
        if (quantity >= product.getRemainingItems()) {
            increaseButton.setVisible(false);
        }
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
                if (afterStart && beforeEnd) {
                    Double afterPrice = product.getPrice() * (100 - auction.getDiscountPercent()) / 100;
                    previousPrice.setText("$ " + String.format("%.2f", product.getPrice()));
                    percent.setText(String.format("%.0f", auction.getDiscountPercent()) + "%");
                    price.setText("$ " + String.format("%.2f", afterPrice));
                    int quantity = App.getCart().getProducts().get(product);
                    totalPrice.setText("Total: $ " + String.format("%.2f", afterPrice * quantity));
                    auctionImage.setVisible(true);
                    percent.setVisible(true);
                    previousPrice.setVisible(true);
                } else if (!afterStart || !beforeEnd) {
                    price.setText("$ " + product.getPrice());
                    int quantity = App.getCart().getProducts().get(product);
                    totalPrice.setText("Total: $ " + String.format("%.2f", product.getPrice() * quantity));
                    auctionImage.setVisible(false);
                    percent.setVisible(false);
                    previousPrice.setVisible(false);
                }
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void decreaseProduct(MouseEvent mouseEvent){
        increaseButton.setVisible(true);
        int quantity = App.getCart().getProducts().get(product);
        quantity--;
        if (quantity == 0) {
            App.getCart().getProducts().remove(product);
            try {
                App.setRoot("cart");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            App.getCart().getProducts().replace(product, quantity);
            numberOfProducts.setText("" + quantity);
            if (product.getAuctionID() == null) {
                totalPrice.setText("Total: $ " + String.format("%.2f", product.getPrice() * quantity));
            }
        }
        CartController.updatePrice();
    }

    public void increaseProduct(MouseEvent mouseEvent) {
        int quantity = App.getCart().getProducts().get(product);
        quantity++;
        if (quantity >= product.getRemainingItems()) {
            increaseButton.setVisible(false);
        }
        App.getCart().getProducts().replace(product, quantity);
        numberOfProducts.setText("" + quantity);
        if (product.getAuctionID() == null) {
            totalPrice.setText("Total: $ " + String.format("%.2f", product.getPrice() * quantity));
        }
        CartController.updatePrice();
    }


}

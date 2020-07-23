package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Discount.Auction;
import JavaProject.Model.DualString;
import JavaProject.Model.ProductOrganization.Category;
import JavaProject.Model.ProductOrganization.Comment;
import JavaProject.Model.ProductOrganization.Product;
import JavaProject.Model.Status.Status;
import JavaProject.Model.TripleString;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.w3c.dom.events.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProductPageController implements Initializable {

    public static AnchorPane prevPane;
    ImageView zoomView = new ImageView();
    File starFull = new File("src/main/resources/Images/star-full.png");
    File starHalf = new File("src/main/resources/Images/star-half.png");
    Product product;
    String searchedName;
    File imageFile;

    @FXML AnchorPane mainPane;
    @FXML ImageView productImage;
    @FXML ImageView soldImage;
    @FXML ImageView auctionImage;
    @FXML ImageView star1;
    @FXML ImageView star2;
    @FXML ImageView star3;
    @FXML ImageView star4;
    @FXML ImageView star5;
    @FXML Label price;
    @FXML Label percent;
    @FXML Label previousPrice;
    @FXML Label productName;
    @FXML Label brandLabel;
    @FXML Label sellerLabel;
    @FXML TextArea descriptionArea;
    @FXML TableView specsTable;
    @FXML TableColumn specsColumn;
    @FXML TableColumn infoColumn;
    @FXML HBox similarProducts;
    @FXML TableView commentTable;
    @FXML TableColumn userColumn;
    @FXML TableColumn titleColumn;
    @FXML TableColumn commentColumn;
    @FXML TextField titleField;
    @FXML TextArea commentArea;
    @FXML Button buyButton;
    @FXML TableView compareTable;
    @FXML TableColumn fieldColumn;
    @FXML TableColumn product1Column;
    @FXML TableColumn product2Column;
    @FXML TextField otherProductField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void initPage(Product product) throws IOException {
        this.product = product;
        App.getResponseFromServer("increaseView", product.getName());
        initInformation();
        initZoom();
        initSimilarProducts();
        initComments();
        initCompareTable(null);
    }

    private void initInformation() {
        product = Database.getInstance().getProductByName(product.getName());
        auctionImage.setVisible(false);
        soldImage.setVisible(false);
        percent.setVisible(false);
        previousPrice.setVisible(false);
        specsColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        infoColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        productName.setText("Name : " + product.getName());
        brandLabel.setText("Brand : " + product.getBrand());
        sellerLabel.setText("Seller : " + product.getSellerUsername());
        descriptionArea.setText(product.getDescription());
        price.setText("$ " + product.getPrice());
        imageFile = new File(App.getFileData(product.getName(), "productPhoto"));
        productImage.setImage(new Image(imageFile.toURI().toString()));
        if (product.getRemainingItems() == 0) {
            soldImage.setVisible(true);
            buyButton.setDisable(true);
        }
        if (product.getAuctionID() != null) {
            auctionHandling();
        }
        double mark = product.getAverageMark();
        for (String key : product.getSpecs().keySet())
            specsTable.getItems().add(new DualString(key, product.getSpecs().get(key)));
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

    private void initZoom() {
        product = Database.getInstance().getProductByName(product.getName());
        productImage.setOnMouseEntered(e -> {
            mainPane.getChildren().add(zoomView);
        });
        productImage.setOnMouseMoved(e -> {
            int x = (int) e.getX();
            int y = (int) e.getY();
            zoomView.setLayoutX(x + 10 + productImage.getLayoutX());
            zoomView.setLayoutY(y + 10 + productImage.getLayoutY());
            zoomView.setFitHeight(50);
            zoomView.setFitWidth(50);
            zoomView.setImage(new Image(product.getImagePath()));
            Rectangle2D viewportRect = new Rectangle2D(x, y, 20, 20);
            zoomView.setViewport(viewportRect);
        });
        productImage.setOnMouseExited(e -> {
            mainPane.getChildren().remove(zoomView);
        });
    }

    private void initSimilarProducts() {
        product = Database.getInstance().getProductByName(product.getName());
        Category category = Database.getInstance().getCategoryByName(product.getParentCategoryName());
        ArrayList<Product> products = Database.getInstance().getAllAcceptedProductsRecursively(category);
        HashMap<Product, Integer> randomProducts = new HashMap<>();
        while (randomProducts.size() < Math.min(10, products.size() - 1)) {
            int index = Math.abs(new Random().nextInt()) % products.size();
            if (randomProducts.containsValue(index)) continue;
            if (this.product != products.get(index)) {
                try {
                    Product prd = products.get(index);
                    randomProducts.put(prd, index);
                    FXMLLoader loader = new FXMLLoader(App.class.getResource("productView.fxml"));
                    AnchorPane anchorPane = loader.load();
                    ProductViewController CNT = loader.getController();
                    CNT.initProduct(prd);
                    similarProducts.getChildren().add(anchorPane);
                    anchorPane.setOnMouseClicked(e -> {
                        try {
                            FXMLLoader productLoader = new FXMLLoader(App.class.getResource("productPage.fxml"));
                            ScrollPane productPane = productLoader.load();
                            ProductPageController controller = productLoader.getController();
                            ProductPageController.prevPane = (AnchorPane) App.getProductsPage();
                            controller.initPage(prd);
                            App.setRoot(productPane);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void initComments() {
        product = Database.getInstance().getProductByName(product.getName());
        commentTable.getItems().clear();
        userColumn.setCellValueFactory(new PropertyValueFactory<>("commenterName"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        for (Comment comment : product.getComments())
            commentTable.getItems().add(comment);
    }

    private void initCompareTable(Product other) {
        product = Database.getInstance().getProductByName(product.getName());
        fieldColumn.setCellValueFactory(new PropertyValueFactory<>("string1"));
        product1Column.setCellValueFactory(new PropertyValueFactory<>("string2"));
        product2Column.setCellValueFactory(new PropertyValueFactory<>("string3"));
        product1Column.setText(product.getName());
        product2Column.setText(other == null ? "" : other.getName());
        compareTable.getItems().clear();
        for (TripleString tripleString : Database.getInstance().getComparison(product, other))
            compareTable.getItems().add(tripleString);
    }

    @FXML
    private void searchProduct(ActionEvent event) {
        if (!otherProductField.getText().isBlank()) {
            searchedName = otherProductField.getText();
            ArrayList<Product> products = Database.getInstance().getAllProducts();
            for (Product product : products) {
                if (product.getStatus().equals(Status.ACCEPTED)) {
                    if (product.getName().equals(searchedName)) {
                        initCompareTable(product);
                        return;
                    }
                }
            }
            otherProductField.setText("Product not found!");
        }
    }

    @FXML
    private void resetProduct(ActionEvent event) {
        if (searchedName != null) {
            initCompareTable(null);
            searchedName = null;
        }
        otherProductField.setText("");
    }


    private void auctionHandling() {
        product = Database.getInstance().getProductByName(product.getName());
        try {
            Auction auction = Database.getInstance().getAuctionByID(product.getAuctionID());
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
                auctionImage.setVisible(true);
                percent.setVisible(true);
                previousPrice.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void leaveComment(ActionEvent event) throws IOException {
        product = Database.getInstance().getProductByName(product.getName());
        String title = titleField.getText().trim();
        String content = commentArea.getText().trim();
        if (title.isBlank()) {
            titleField.setText("!!! Enter title !!!");
        } else if (content.isBlank()) {
            commentArea.setText("!!! Enter comment !!!");
        } else {
            App.getResponseFromServer("leaveComment", product.getName(), App.objectToString(new Comment(App.getSignedInAccount() == null ? "Unknown" : App.getSignedInAccount().getUsername(), title, content)));
            initComments();
        }
    }

    @FXML
    private void addToCart(ActionEvent event) {
        product = Database.getInstance().getProductByName(product.getName());
        if (product.getRemainingItems() > 0)
            App.getCart().getProducts().put(product, 1);
    }

    public void changeToPrevScene(javafx.scene.input.MouseEvent mouseEvent) {
        App.setRoot(prevPane);
    }

    public void goToCart(javafx.scene.input.MouseEvent mouseEvent) throws IOException {
        App.setRoot("cart");
        CartController.prevPane = App.productsPage;
    }
}

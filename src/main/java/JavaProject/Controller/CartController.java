package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Database.Database;
import JavaProject.Model.ProductOrganization.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CartController implements Initializable {

    public static Parent prevPane;
    public static Label price;

    @FXML
    HBox hBox;
    @FXML
    Label totalPrice;
    @FXML
    Button purchaseButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (App.getCart().getProducts().keySet().size() == 0)
            purchaseButton.setDisable(true);
        if (App.getSignedInAccount() instanceof Buyer)
            purchaseButton.setDisable(false);
        else purchaseButton.setDisable(true);
        price = totalPrice;
        HashMap<Product, Integer> newCart = new HashMap<>();
        for (Product product : App.getCart().getProducts().keySet()) {
            int quantity = App.getCart().getProducts().get(product);
            if (quantity > 0)
                newCart.put(Database.getInstance().getProductByName(product.getName()), quantity);
        }
        App.getCart().setProducts(newCart);
        for (Product product : App.getCart().getProducts().keySet()) {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("productInCart.fxml"));
                AnchorPane anchorPane = loader.load();
                hBox.getChildren().add(anchorPane);
                ProductInCartController controller = loader.getController();
                controller.initProduct(product);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updatePrice();
    }

    @FXML
    private void openPurchaseSection(ActionEvent event) throws IOException {
        if (App.getCart().getProducts().size() == 0) {
            purchaseButton.setDisable(true);
            return;
        }
        HashMap<Product, Integer> newCart = new HashMap<>();
        for (Product product : App.getCart().getProducts().keySet()) {
            int quantity = App.getCart().getProducts().get(product);
            if (quantity > 0)
                newCart.put(Database.getInstance().getProductByName(product.getName()), quantity);
        }
        App.getCart().setProducts(newCart);
        for (Product product : App.getCart().getProducts().keySet()) {
            int quantity = App.getCart().getProducts().get(product);
            if (quantity > product.getRemainingItems()) {
                App.getCart().getProducts().replace(product, product.getRemainingItems());
                App.setRoot("cart");
                return;
            }
        }
        if (App.getCart().getProducts().size() == 0)
            App.setRoot("cart");
        App.setRoot("receiverInfo");
    }

    public static void updatePrice() {
        price.setText("Total price: $ " + App.getCart().getPrice());
    }

    @FXML
    private void changeToPrevPane(ActionEvent event) throws IOException {
        App.setRoot(prevPane);
    }

}

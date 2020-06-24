package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.ProductOrganization.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CartController implements Initializable {

    public static String prevFXML;
    public static Label price;

    @FXML
    HBox hBox;

    @FXML
    Label totalPrice;

    @FXML
    Button purchaseButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (App.getSignedInAccount() instanceof Buyer)
            purchaseButton.setDisable(false);
        else purchaseButton.setDisable(true);
        price = totalPrice;
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
    }

    @FXML
    private void openPurchaseSection(ActionEvent event) throws IOException {
        App.setRoot("purchase");
    }

    public static void updatePrice() {
        price.setText("Total price: $ " + App.getCart().getPrice());
    }


    public void changeToPrevScene(ActionEvent event) throws IOException {
        App.setRoot(prevFXML);
    }

}

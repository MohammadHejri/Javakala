package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Log.BuyLog;
import JavaProject.Model.Log.ProductOnLog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BuyerProfileController implements Initializable {

    public static Parent prevPane;

    @FXML
    BorderPane borderPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            borderPane.setCenter(App.loadFXML("accountInfo"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAccountInfoSection(ActionEvent event) throws IOException {
        borderPane.setCenter(App.loadFXML("accountInfo"));
    }

    @FXML
    private void openBuyerSaleHistorySection(ActionEvent event) throws IOException {
        borderPane.setCenter(App.loadFXML("buyerSaleHistory"));
    }

    @FXML
    private void openDiscountCodeViewSection(ActionEvent event) throws IOException {
        borderPane.setCenter(App.loadFXML("discountCodeView"));
    }

    @FXML
    private void openCartSection(ActionEvent event) throws IOException {
        App.setRoot("cart");
        CartController.prevPane = App.loadFXML("buyerProfile");
    }

    @FXML
    private void signOut(ActionEvent event) throws IOException {
        String response = App.getResponseFromServer("signOut", App.getSignedInAccount().getUsername());
        App.setSignedInAccount(null);
        App.setRoot(App.mainPage);
    }

    @FXML
    private void changeToPrevPane(ActionEvent event) throws IOException {
        App.setRoot(prevPane);
    }

    @FXML
    private void changeToMainMenu() {
        App.setRoot(App.mainPage);
    }

}

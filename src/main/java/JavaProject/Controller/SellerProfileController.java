package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Log.ProductOnLog;
import JavaProject.Model.Log.SellLog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SellerProfileController implements Initializable{

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
    private void openProductsManagementSection(ActionEvent event) throws IOException {
        borderPane.setCenter(App.loadFXML("sellerProductsManagement"));
    }

    @FXML
    private void openAuctionsManagementSection(ActionEvent event) throws IOException {
        borderPane.setCenter(App.loadFXML("sellerAuctionsManagement"));
    }

    @FXML
    private void openRequestsViewSection(ActionEvent event) throws IOException {
        borderPane.setCenter(App.loadFXML("requestsView"));
    }

    @FXML
    private void openSellerSaleHistorySection(ActionEvent event) throws IOException {
        borderPane.setCenter(App.loadFXML("sellerSaleHistory"));
    }

    @FXML
    private void openCartSection(ActionEvent event) throws IOException {
        App.setRoot("cart");
        CartController.prevPane = App.loadFXML("sellerProflie");
    }

    @FXML
    private void signOut(ActionEvent event) throws IOException {
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

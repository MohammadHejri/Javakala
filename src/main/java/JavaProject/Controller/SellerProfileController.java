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
//        ArrayList<ProductOnLog> arr = new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            arr.add(new ProductOnLog(String.valueOf(i), "", "", 5, 1000, 12));
//        }
//        SellLog sellLog = new SellLog("mahsa", 10000, 120, arr);
//        ((Seller) App.getSignedInAccount()).getSellLogsID().add(sellLog.getID());
//        try {
//            Database.getInstance().saveSellLog(sellLog);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
        CartController.prevFXML = "sellerProfile";
    }

    @FXML
    private void signOut(ActionEvent event) throws IOException {
        App.setSignedInAccount(null);
        // TODO: App.setRoot(main);
        App.setRoot("signIn");
    }

    @FXML
    private void changeToPrevPane(ActionEvent event) throws IOException {
        App.setRoot(prevPane);
    }


}

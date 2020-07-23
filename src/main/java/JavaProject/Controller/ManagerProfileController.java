package JavaProject.Controller;

import JavaProject.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ManagerProfileController implements Initializable{

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
    private void openUsersManagementSection(ActionEvent event) throws IOException {
        borderPane.setCenter(App.loadFXML("usersManagement"));
    }

    @FXML
    private void openRequestsManagementSection(ActionEvent event) throws IOException {
        borderPane.setCenter(App.loadFXML("requestsManagement"));
    }

    @FXML
    private void openProductsManagementSection(ActionEvent event) throws IOException {
        borderPane.setCenter(App.loadFXML("productsManagement"));
    }

    @FXML
    private void openCategoriesManagementSection(ActionEvent event) throws IOException {
        borderPane.setCenter(App.loadFXML("categoriesManagement"));
    }

    @FXML
    private void openDiscountCodesManagementSection(ActionEvent event) throws IOException {
        borderPane.setCenter(App.loadFXML("discountCodesManagement"));
    }

    @FXML
    private void openCartSection(ActionEvent event) throws IOException {
        App.setRoot("cart");
        CartController.prevPane = null;
    }

    @FXML
    private void signOut(ActionEvent event) {
        String response = App.getResponseFromServer("signOut", App.getSignedInAccount().getUsername());
        App.setSignedInAccount(null);
        App.setRoot(App.mainPage);
    }

    @FXML
    private void changeToPrevPane(ActionEvent event) {
        App.setRoot(prevPane);
    }

    @FXML
    private void changeToMainMenu() {
        App.setRoot(App.mainPage);
    }

}

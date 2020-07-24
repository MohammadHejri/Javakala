package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class PurchasePageCardPaymentController {

    public TextField cardNumberField;
    public TextField cvv2Field;
    public TextField expireDateField;
    public TextField passwordField;
    public static double amount;

    public void pay(ActionEvent event) throws IOException {
        String cardNumber = cardNumberField.getText().trim();
        String cvv2 = cvv2Field.getText().trim();
        String expireDate = expireDateField.getText().trim();
        String password = passwordField.getText().trim();
        if (cardNumber.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter card number").showAndWait();
        } else if (cvv2.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter cvv2").showAndWait();
        } else if (expireDate.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter expire date").showAndWait();
        } else if (password.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter dynamic password").showAndWait();
        } else if (!cardNumber.matches("\\d+")) {
            new Alert(Alert.AlertType.ERROR, "Use digits for card number").showAndWait();
        } else if (!cvv2.matches("\\d+")) {
            new Alert(Alert.AlertType.ERROR, "Use digits for card number").showAndWait();
        } else if (!expireDate.matches("\\d{2}/\\d{2}")) {
            new Alert(Alert.AlertType.ERROR, "Use format yy/MM for expire date").showAndWait();
        } else if (!password.matches("\\d+")) {
            new Alert(Alert.AlertType.ERROR, "Use digits for password").showAndWait();
        } else {
            // new Alert(Alert.AlertType.INFORMATION, "Balance increased").showAndWait();
            App.getResponseFromServer("updateWallet", App.getSignedInAccount().getUsername(), String.valueOf(amount));
            // App.setRoot("paymentWay");
            FXMLLoader loader = new FXMLLoader(App.class.getResource("paymentWay.fxml"));
            Parent pane = loader.load();
            PurchasePagePaymentWayController controller = loader.getController();
            controller.payWithBalance(null);
        }
    }

    public void goToPaymentWay(MouseEvent mouseEvent) throws IOException {
        App.setRoot("paymentWay");
    }
}

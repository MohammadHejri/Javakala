package JavaProject.Controller;

import JavaProject.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class PurchasePageReceiverInfoController {

    @FXML TextField firstNameField;
    @FXML TextField lastNameField;
    @FXML TextField addressField;
    @FXML TextField phoneNumberField;

    @FXML
    private void goToDiscountPage(ActionEvent event) throws IOException {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        if (!firstName.matches("\\w+")) {
            new Alert(Alert.AlertType.ERROR, "Use word letters for first name").showAndWait();
        } else if (!lastName.matches("\\w+")) {
            new Alert(Alert.AlertType.ERROR, "Use word letters for last name").showAndWait();
        } else if (!phoneNumber.matches("\\d+")) {
            new Alert(Alert.AlertType.ERROR, "Use digits for phone number").showAndWait();
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Reciever info accepted").showAndWait();
            App.setRoot("discountCodeValidation");
        }
    }

    @FXML
    private void goToCart(MouseEvent mouseEvent) throws IOException {
        App.setRoot("cart");
    }
}

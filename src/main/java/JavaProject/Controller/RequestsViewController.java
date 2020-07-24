package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Request.Request;
import JavaProject.Model.Request.Subject;
import JavaProject.Model.Status.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

// Client-Server : Done

public class RequestsViewController implements Initializable {
    @FXML
    TextField chargeWalletField;
    @FXML
    TextField putMoneyField;
    @FXML
    TextArea descriptionArea;
    @FXML
    TableView<Request> requestsTable;
    @FXML
    TableColumn<Request, String> requestIDColumn;
    @FXML
    TableColumn<Request, Subject> subjectColumn;
    @FXML
    TableColumn<Request, Subject> statusColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        requestIDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        for (Request request : Database.getInstance().getAllRequests())
            if (request.getRequester().equals(App.getSignedInAccount().getUsername()))
                requestsTable.getItems().add(request);
        requestsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectRequest();
            } else {
                deselectRequest();
            }
        });
    }

    private void selectRequest() {
        Request request = requestsTable.getSelectionModel().getSelectedItem();
        descriptionArea.setText(request.getDescription());
    }

    private void deselectRequest() {
        descriptionArea.setText("");
    }

    @FXML
    private void chargeWallet(ActionEvent event) {
        try {
            double amount = Double.parseDouble(chargeWalletField.getText().trim());
            chargeWalletField.setText("");
            if (amount < 0) {
                new Alert(Alert.AlertType.ERROR, "Enter positive value").showAndWait();
                return;
            }
            String response = App.getResponseFromServer("chargeWalletSeller", App.getSignedInAccount().getUsername(), String.valueOf(amount));
            new Alert(Alert.AlertType.INFORMATION, response).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Enter DOUBLE for money").showAndWait();
        }
    }

    @FXML
    private void putMoneyInBank(ActionEvent event) {
        try {
            double amount = Double.parseDouble(putMoneyField.getText().trim());
            putMoneyField.setText("");
            if (amount < 0) {
                new Alert(Alert.AlertType.ERROR, "Enter positive value").showAndWait();
                return;
            }
            String minBalance = App.getResponseFromServer("getMinBalance");
            if (((Seller)App.getSignedInAccount()).getBalance() - amount < Integer.parseInt(minBalance)) {
                new Alert(Alert.AlertType.ERROR, "At least $" + minBalance + " should remain in your wallet").showAndWait();
                return;
            }
            String response = App.getResponseFromServer("putInBankSeller", App.getSignedInAccount().getUsername(), String.valueOf(amount));
            new Alert(Alert.AlertType.INFORMATION, response).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Enter DOUBLE for money").showAndWait();
        }
    }
}

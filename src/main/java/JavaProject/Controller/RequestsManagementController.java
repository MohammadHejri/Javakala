package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Discount.Auction;
import JavaProject.Model.ProductOrganization.Product;
import JavaProject.Model.Request.Request;
import JavaProject.Model.Request.Subject;
import JavaProject.Model.Status.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

// Client-Server : Done

public class RequestsManagementController implements Initializable {

    @FXML
    TableView<Request> requestsTable;
    @FXML
    TableColumn<Request, String> requestIDColumn;
    @FXML
    TableColumn<Request, Subject> subjectColumn;
    @FXML
    TextArea descriptionArea;
    @FXML
    Button acceptButton;
    @FXML
    Button declineButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        acceptButton.setDisable(true);
        declineButton.setDisable(true);
        requestIDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        for (Request request : Database.getInstance().getAllRequests())
            if (request.getStatus().equals(Status.PENDING))
                requestsTable.getItems().add(request);
        requestsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectRequest();
            } else {
                deselectRequest();
            }
        });
    }

    @FXML
    public void acceptRequest(ActionEvent event) throws IOException {
        Request request = requestsTable.getSelectionModel().getSelectedItem();
        String response = App.getResponseFromServer("acceptRequest", request.getID());
        if (response.startsWith("Success")) {
            new Alert(Alert.AlertType.INFORMATION, "Request accepted").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, response).showAndWait();
        }
        requestsTable.getItems().clear();
        for (Request req : Database.getInstance().getAllRequests())
            if (req.getStatus().equals(Status.PENDING))
                requestsTable.getItems().add(req);
    }

    @FXML
    public void declineRequest(ActionEvent event) throws IOException {
        Request request = requestsTable.getSelectionModel().getSelectedItem();
        String response = App.getResponseFromServer("declineRequest", request.getID());
        if (response.startsWith("Success")) {
            new Alert(Alert.AlertType.INFORMATION, "Request declined").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, response).showAndWait();
        }
        requestsTable.getItems().clear();
        for (Request req : Database.getInstance().getAllRequests())
            if (req.getStatus().equals(Status.PENDING))
                requestsTable.getItems().add(req);
    }

    private void selectRequest() {
        Request request = requestsTable.getSelectionModel().getSelectedItem();
        descriptionArea.setText(request.getDescription());
        acceptButton.setDisable(false);
        declineButton.setDisable(false);
    }

    private void deselectRequest() {
        descriptionArea.setText("");
        acceptButton.setDisable(true);
        declineButton.setDisable(true);
    }
}

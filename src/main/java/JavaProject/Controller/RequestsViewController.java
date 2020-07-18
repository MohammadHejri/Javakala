package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Request.Request;
import JavaProject.Model.Request.Subject;
import JavaProject.Model.Status.Status;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

// Client-Server : Done

public class RequestsViewController implements Initializable {
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
}

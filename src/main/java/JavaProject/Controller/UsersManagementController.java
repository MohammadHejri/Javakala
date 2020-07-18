package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Account;
import JavaProject.Model.Database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

// Client-Server : Done

public class UsersManagementController implements Initializable {

    @FXML
    TableView<Account> userTable;
    @FXML
    TableColumn<Account, String> usernameColumn;
    @FXML
    TableColumn<Account, String> firstNameColumn;
    @FXML
    TableColumn<Account, String> lastNameColumn;
    @FXML
    TableColumn<Account, String> emailAddressColumn;
    @FXML
    TableColumn<Account, String> phoneNumberColumn;
    @FXML
    Button deleteUserButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        deleteUserButton.setDisable(true);
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailAddressColumn.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        for (Account account : Database.getInstance().getAllAccounts())
            userTable.getItems().add(account);
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                deleteUserButton.setDisable(false);
            } else {
                deleteUserButton.setDisable(true);
            }
        });
    }

    @FXML
    private void deleteUser(ActionEvent event) {
        Account toBeDeletedAccount = userTable.getSelectionModel().getSelectedItem();
        String response = App.getResponseFromServer("deleteAccount", App.getSignedInAccount().getUsername(), toBeDeletedAccount.getUsername());
        if (response.startsWith("Success")) {
            new Alert(Alert.AlertType.INFORMATION, "User successfully deleted").showAndWait();
            userTable.getItems().clear();
            for (Account account : Database.getInstance().getAllAccounts())
                userTable.getItems().add(account);
        } else {
            new Alert(Alert.AlertType.ERROR, response).showAndWait();
        }
    }

    @FXML
    private void createManager(ActionEvent event) throws IOException {
        App.setRoot("registerPage");
        RegisterPanelController.prevPane = App.loadFXML("managerProfile");
    }

}

package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Account;
import JavaProject.Model.Database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
    Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailAddressColumn.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        for (Account account : Database.getInstance().getAllAccounts())
            userTable.getItems().add(account);
    }

    @FXML
    private void deleteUser(ActionEvent event) {
        Account toBeDeletedAccount = userTable.getSelectionModel().getSelectedItem();
        if (toBeDeletedAccount == App.getSignedInAccount()) {
            statusLabel.setText("You can not delete yourself idiot");
        } else if (toBeDeletedAccount != null) {
            Database.getInstance().deleteAccount(toBeDeletedAccount);
            statusLabel.setText("User successfully deleted");
            userTable.getItems().clear();
            for (Account account : Database.getInstance().getAllAccounts())
                userTable.getItems().add(account);
        }
    }

    @FXML
    private void createManager(ActionEvent event) throws IOException {
        App.setRoot("signUp");
        SignUpController.prevFXML = "managerProfile";
    }

}

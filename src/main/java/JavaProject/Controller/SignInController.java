package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Account;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Account.Manager;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Status.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignInController implements Initializable {

    @FXML
    TextField usernameField;
    @FXML
    TextField passwordField;
    @FXML
    Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void signIn(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Account account = Database.getInstance().getAccountByUsername(username);
        if (!username.matches("\\w+")) {
            statusLabel.setText("Use word letters for username");
        } else if (!password.matches("\\w+")) {
            statusLabel.setText("Use word letters for password");
        } else if (account == null || !account.getPassword().equals(password)) {
            statusLabel.setText("Wrong username or password");
        } else if (account instanceof Seller && ((Seller) account).getStatus().equals(Status.PENDING)) {
            statusLabel.setText("Account to be Confirmed");
        }  else if (account instanceof Seller && ((Seller) account).getStatus().equals(Status.DECLINED)) {
            statusLabel.setText("Account not allowed");
        } else {
            App.setSignedInAccount(account);
            // TODO: go to main page
            if (account instanceof Manager) {
                App.setRoot("managerProfile");
            }
            if (account instanceof Seller) {
                App.setRoot("sellerProfile");
            }
            if (account instanceof Buyer) {
                App.setRoot("buyerProfile");
            }
        }
    }

    @FXML
    private void changeToSignUp(ActionEvent event) throws IOException {
        App.setRoot("signUp");
        SignUpController.prevFXML = "signIn";
    }
}

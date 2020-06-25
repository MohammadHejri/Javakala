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
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignInController implements Initializable {

    public static Parent prevPane;

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
            if (account instanceof Manager) {
                App.setRoot("managerProfile");
                ManagerProfileController.prevPane = App.mainPage;
            }
            if (account instanceof Seller) {
                App.setRoot("sellerProfile");
                SellerProfileController.prevPane = App.mainPage;
            }
            if (account instanceof Buyer) {
                App.setRoot("buyerProfile");
                BuyerProfileController.prevPane = App.mainPage;
            }
        }
    }

    @FXML
    private void changeToSignUp(ActionEvent event) throws IOException {
        App.setRoot("signUp");
        SignUpController.prevFXML = "signIn";
    }

    @FXML
    private void changeToPrevPane(MouseEvent mouseEvent) {
        App.setRoot(prevPane);
    }
}

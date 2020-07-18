package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Account;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Account.Manager;
import JavaProject.Model.Account.Seller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

// Client-Server : Done

public class AccountInfoController implements Initializable {

    @FXML
    TextField usernameField;
    @FXML
    TextField accountTypeField;
    @FXML
    TextField balanceField;
    @FXML
    TextField currentPasswordField;
    @FXML
    TextField newPasswordField;
    @FXML
    TextField firstNameField;
    @FXML
    TextField lastNameField;
    @FXML
    TextField emailAddressField;
    @FXML
    TextField phoneNumberField;
    @FXML
    TextField companyNameField;
    @FXML
    ImageView imageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Account account = App.getSignedInAccount();
        imageView.setImage(new Image(account.getImagePath()));
        usernameField.setEditable(false);
        accountTypeField.setEditable(false);
        balanceField.setEditable(false);
        usernameField.setText(account.getUsername());
        firstNameField.setText(account.getFirstName());
        lastNameField.setText(account.getLastName());
        emailAddressField.setText(account.getEmailAddress());
        phoneNumberField.setText(account.getPhoneNumber());
        if (account instanceof Manager) {
            accountTypeField.setText("manager");
            balanceField.setText("-");
            companyNameField.setText("-");
            companyNameField.setEditable(false);
        }
        if (account instanceof Seller) {
            accountTypeField.setText("seller");
            balanceField.setText("$" + ((Seller) account).getBalance());
            companyNameField.setText(((Seller) account).getCompanyName());
        }
        if (account instanceof Buyer) {
            accountTypeField.setText("buyer");
            balanceField.setText("$" + ((Buyer) account).getBalance());
            companyNameField.setText("-");
            companyNameField.setEditable(false);
        }
    }

    @FXML
    private void update(ActionEvent event) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String emailAddress = emailAddressField.getText();
        String phoneNumber = phoneNumberField.getText();
        String companyName = companyNameField.getText();
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();

        Account account = App.getSignedInAccount();
        Account changedAccount = null;
        String response = null;
        if (account instanceof Manager) {
            changedAccount = new Manager(account.getUsername(), newPassword, firstName, lastName, emailAddress, phoneNumber, account.getImagePath());
            response = App.getResponseFromServer("updateManagerInfo", account.getUsername(), currentPassword, App.objectToString(changedAccount));
        }
        if (account instanceof Seller) {
            changedAccount = new Seller(account.getUsername(), newPassword, firstName, lastName, emailAddress, phoneNumber, account.getImagePath(), companyName);
            response = App.getResponseFromServer("updateSellerInfo", account.getUsername(), currentPassword, App.objectToString(changedAccount));
        }
        if (account instanceof Buyer) {
            changedAccount = new Buyer(account.getUsername(), newPassword, firstName, lastName, emailAddress, phoneNumber, account.getImagePath());
            response = App.getResponseFromServer("updateBuyerInfo", account.getUsername(), currentPassword, App.objectToString(changedAccount));
        }
        if (response.startsWith("Success"))
            new Alert(Alert.AlertType.INFORMATION, "Profile updated").showAndWait();
        else
            new Alert(Alert.AlertType.ERROR, response).showAndWait();
        currentPasswordField.setText("");
        newPasswordField.setText("");
    }

    @FXML
    private void chooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null)
            imageView.setImage(new Image(file.toURI().toString()));
    }

    @FXML
    private void resetImageToDefault(ActionEvent event) {
        File file = new File("src/main/resources/Images/account.png");
        imageView.setImage(new Image(file.toURI().toString()));
    }

}

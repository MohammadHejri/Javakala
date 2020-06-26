package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Account;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Account.Manager;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Database.Database;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

// Completed

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
            balanceField.setText("$" + String.valueOf(((Seller) account).getBalance()));
            companyNameField.setText(((Seller) account).getCompanyName());
        }
        if (account instanceof Buyer) {
            accountTypeField.setText("buyer");
            balanceField.setText("$" + String.valueOf(((Buyer) account).getBalance()));
            companyNameField.setText("-");
            companyNameField.setEditable(false);
        }
    }

    @FXML
    private void update(ActionEvent event) throws IOException {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String emailAddress = emailAddressField.getText();
        String phoneNumber = phoneNumberField.getText();
        String companyName = companyNameField.getText();
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        Account account = App.getSignedInAccount();
        if (!account.getPassword().equals(currentPassword)) {
            new Alert(Alert.AlertType.ERROR, "Enter current password correctly").showAndWait();
        } else if (!newPassword.isBlank() && !newPassword.matches("\\w+")) {
            new Alert(Alert.AlertType.ERROR, "Use word letters for new password").showAndWait();
        } else if (!firstName.matches("\\w+")) {
            new Alert(Alert.AlertType.ERROR, "Use word letters for first name").showAndWait();
        } else if (!lastName.matches("\\w+")) {
            new Alert(Alert.AlertType.ERROR, "Use word letters for last name").showAndWait();
        } else if (!emailAddress.matches("(\\S+)@(\\S+)\\.(\\S+)")) {
            new Alert(Alert.AlertType.ERROR, "Email format: example@gmail.com").showAndWait();
        } else if (!phoneNumber.matches("\\d+")) {
            new Alert(Alert.AlertType.ERROR, "Use digits for phone number").showAndWait();
        } else if (App.getSignedInAccount() instanceof Seller && !companyName.matches("\\w+")) {
            new Alert(Alert.AlertType.ERROR, "Use word letters for company name").showAndWait();
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Profile updated").showAndWait();
            String imagePath = imageView.getImage().getUrl();
            account.setFirstName(firstName);
            account.setLastName(lastName);
            account.setEmailAddress(emailAddress);
            account.setPhoneNumber(phoneNumber);
            account.setImagePath(imagePath);
            if (account instanceof Seller)
                ((Seller) account).setCompanyName(companyName);
            if (!newPassword.isBlank())
                account.setPassword(newPassword);
            Database.getInstance().saveAccount(account);
        }
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

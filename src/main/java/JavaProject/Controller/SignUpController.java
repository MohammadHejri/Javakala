package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Account;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Account.Manager;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Request.Request;
import JavaProject.Model.Request.Subject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    public static String prevFXML;

    @FXML
    TextField usernameField;
    @FXML
    TextField passwordField;
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
    Label statusLabel;
    @FXML
    RadioButton managerButton;
    @FXML
    RadioButton sellerButton;
    @FXML
    RadioButton buyerButton;
    @FXML
    ImageView imageView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File("src/main/resources/Images/account.png");
        imageView.setImage(new Image(file.toURI().toString()));
        companyNameField.setDisable(true);
        if (!Database.getInstance().managerExists()) {
            managerButton.setSelected(true);
            sellerButton.setDisable(true);
            buyerButton.setDisable(true);
        } else if (App.getSignedInAccount() == null) {
            buyerButton.setSelected(true);
            managerButton.setDisable(true);
        } else {
            managerButton.setSelected(true);
            sellerButton.setDisable(true);
            buyerButton.setDisable(true);
        }
    }

    @FXML
    private void signUp(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String emailAddress = emailAddressField.getText();
        String phoneNumber = phoneNumberField.getText();
        String companyName = companyNameField.getText();
        Account account = Database.getInstance().getAccountByUsername(username);
        if (!username.matches("\\w+")) {
            statusLabel.setText("Use word letters for username");
        } else if (!password.matches("\\w+")) {
            statusLabel.setText("Use word letters for password");
        } else if (!firstName.matches("\\w+")) {
            statusLabel.setText("Use word letters for first name");
        } else if (!lastName.matches("\\w+")) {
            statusLabel.setText("Use word letters for last name");
        } else if (!emailAddress.matches("(\\S+)@(\\S+)\\.(\\S+)")) {
            statusLabel.setText("Email format: example@gmail.com");
        } else if (!phoneNumber.matches("\\d+")) {
            statusLabel.setText("Use digits for phone number");
        } else if (!companyNameField.isDisabled() && !companyName.matches("\\w+")) {
            statusLabel.setText("Use word letters for company name");
        } else if (account != null) {
            statusLabel.setText("Username exists");
        } else {
            String imagePath = imageView.getImage().getUrl();
            if (managerButton.isSelected())
                account = new Manager(username, password, firstName, lastName, emailAddress, phoneNumber, imagePath);
            if (sellerButton.isSelected()) {
                account = new Seller(username, password, firstName, lastName, emailAddress, phoneNumber, imagePath, companyName);
                Request request = new Request(Subject.SELLER_REGISTER, account.toString());
                request.setSeller((Seller) account);
                Database.getInstance().saveRequest(request);
            }
            if (buyerButton.isSelected())
                account = new Buyer(username, password, firstName, lastName, emailAddress, phoneNumber, imagePath);
            Database.getInstance().saveAccount(account);
            changeToPrevScene(event);
        }
    }

    @FXML
    private void updateAccountProperties(ActionEvent event) {
        if (managerButton.isSelected())
            companyNameField.setDisable(true);
        if (sellerButton.isSelected())
            companyNameField.setDisable(false);
        if (buyerButton.isSelected())
            companyNameField.setDisable(true);
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

    @FXML
    private void changeToPrevScene(ActionEvent event) throws IOException {
        App.setRoot(prevFXML);
    }

}

package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.*;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Request.Request;
import JavaProject.Model.Request.Subject;
import JavaProject.Model.Status.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.ResourceBundle;

// Client-Server : Done

public class RegisterPanelController implements Initializable {
    public static Parent prevPane;
    private Animation animation = new Animation();
    File imageFile = new File("src/main/resources/Images/account.png");

    @FXML
    AnchorPane mainPane;
    @FXML
    TextField signUpUsernameField;
    @FXML
    PasswordField signUpPasswordField;
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
    Label signUpStatusLabel;
    @FXML
    RadioButton managerButton;
    @FXML
    RadioButton sellerButton;
    @FXML
    RadioButton buyerButton;
    @FXML
    RadioButton supporterButton;
    @FXML
    ImageView imageView;
    @FXML
    Button loginButton;
    @FXML
    TextField signInUsernameField;
    @FXML
    PasswordField signInPasswordField;
    @FXML
    Label signInStatusLabel;
    @FXML
    Button backButton1;
    @FXML
    Button backButton2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File("src/main/resources/Images/account.png");
        imageView.setImage(new Image(file.toURI().toString()));
        loginButton.setOnAction(e -> animation.playAnimation(mainPane));
        companyNameField.setDisable(true);
        String response = App.getResponseFromServer("managerExists");
        if (response.equals("false")) {
            managerButton.setSelected(true);
            sellerButton.setDisable(true);
            buyerButton.setDisable(true);
            supporterButton.setDisable(true);
            disableChange();
        } else if (App.getSignedInAccount() == null) {
            buyerButton.setSelected(true);
            managerButton.setDisable(true);
            supporterButton.setDisable(true);
        } else {
            managerButton.setSelected(true);
            supporterButton.setDisable(false);
            sellerButton.setDisable(true);
            buyerButton.setDisable(true);
            disableChange();
        }
    }

    @FXML
    private void signUp(ActionEvent event) throws IOException {
        String username = signUpUsernameField.getText();
        String password = signUpPasswordField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String emailAddress = emailAddressField.getText();
        String phoneNumber = phoneNumberField.getText();
        String companyName = companyNameField.getText();
        String imagePath = imageView.getImage().getUrl();

        String response = null;
        if (managerButton.isSelected()) {
            Account account = new Manager(username, password, firstName, lastName, emailAddress, phoneNumber, imagePath);
            response = App.getResponseFromServer("createManager", App.objectToString(account));
        }
        if (sellerButton.isSelected()) {
            Account account = new Seller(username, password, firstName, lastName, emailAddress, phoneNumber, imagePath, companyName);
            response = App.getResponseFromServer("createSeller", App.objectToString(account));
        }
        if (buyerButton.isSelected()) {
            Account account = new Buyer(username, password, firstName, lastName, emailAddress, phoneNumber, imagePath);
            response = App.getResponseFromServer("createBuyer", App.objectToString(account));
        }
        if (supporterButton.isSelected()) {
            Account account = new Supporter(username, password, firstName, lastName, emailAddress, phoneNumber, imagePath);
            response = App.getResponseFromServer("createSupporter", App.objectToString(account));
        }

        if (response.startsWith("Success")) {
            changeToPrevScene(event);
            App.uploadFilesToServer(username + imageFile.getPath().substring(imageFile.getPath().lastIndexOf(".")) + "", imageFile, "accountPhoto");
        } else {
            signUpStatusLabel.setText(response);
        }
    }

    @FXML
    private void signIn(ActionEvent event) throws IOException, NoSuchAlgorithmException {
        String username = signInUsernameField.getText();
        String password = signInPasswordField.getText();

        String response = App.getResponseFromServer("signIn", username, App.encryptPassword(password));
        if (response.startsWith("Success")) {
            String[] responseParts = response.split("###");
            Account account;
            if (responseParts[1].equals("Manager")) {
                account = App.stringToObject(responseParts[2], Manager.class);
                App.setSignedInAccount(account);
                App.setRoot("managerProfile");
                ManagerProfileController.prevPane = RegisterPanelController.prevPane;
            }
            if (responseParts[1].equals("Seller")) {
                account = App.stringToObject(responseParts[2], Seller.class);
                App.setSignedInAccount(account);
                App.setRoot("sellerProfile");
                SellerProfileController.prevPane = RegisterPanelController.prevPane;
            }
            if (responseParts[1].equals("Buyer")) {
                account = App.stringToObject(responseParts[2], Buyer.class);
                App.setSignedInAccount(account);
                App.setRoot("buyerProfile");
                BuyerProfileController.prevPane = RegisterPanelController.prevPane;
            }
            if (responseParts[1].equals("Supporter")) {
                account = App.stringToObject(responseParts[2], Supporter.class);
                App.setSignedInAccount(account);
                App.setRoot("supporterProfile");
                SupporterProfileController.prevPane = RegisterPanelController.prevPane;
            }
        } else {
            signInStatusLabel.setText(response);
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
        if (supporterButton.isSelected())
            companyNameField.setDisable(true);
    }

    @FXML
    private void chooseImage(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imageFile = file;
            imageView.setImage(new Image(imageFile.toURI().toString()));
        }
    }

    @FXML
    private void resetImageToDefault(ActionEvent event) {
        imageFile = new File("src/main/resources/Images/account.png");
        imageView.setImage(new Image(imageFile.toURI().toString()));
    }

    @FXML
    private void changeToPrevScene(ActionEvent event) throws IOException {
        App.setRoot(prevPane);
    }

    @FXML
    private void disableChange() {
        loginButton.setDisable(true);
    }

}

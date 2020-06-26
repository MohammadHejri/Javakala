package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Account;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Account.Manager;
import JavaProject.Model.Account.Seller;
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
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterPanelController implements Initializable {
    public static Parent prevPane;
    private Animation animation = new Animation();

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
        if (!Database.getInstance().managerExists()) {
            managerButton.setSelected(true);
            sellerButton.setDisable(true);
            buyerButton.setDisable(true);
            disableChange();
        } else if (App.getSignedInAccount() == null) {
            buyerButton.setSelected(true);
            managerButton.setDisable(true);
        } else {
            managerButton.setSelected(true);
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
        Account account = Database.getInstance().getAccountByUsername(username);
        if (!username.matches("\\w+")) {
            signUpStatusLabel.setText("Use word letters for username");
        } else if (!password.matches("\\w+")) {
            signUpStatusLabel.setText("Use word letters for password");
        } else if (!firstName.matches("\\w+")) {
            signUpStatusLabel.setText("Use word letters for first name");
        } else if (!lastName.matches("\\w+")) {
            signUpStatusLabel.setText("Use word letters for last name");
        } else if (!emailAddress.matches("(\\S+)@(\\S+)\\.(\\S+)")) {
            signUpStatusLabel.setText("Email format: example@gmail.com");
        } else if (!phoneNumber.matches("\\d+")) {
            signUpStatusLabel.setText("Use digits for phone number");
        } else if (!companyNameField.isDisabled() && !companyName.matches("\\w+")) {
            signUpStatusLabel.setText("Use word letters for company name");
        } else if (account != null) {
            signUpStatusLabel.setText("Username exists");
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
    private void signIn(ActionEvent event) throws IOException {
        String username = signInUsernameField.getText();
        String password = signInPasswordField.getText();
        Account account = Database.getInstance().getAccountByUsername(username);
        if (!username.matches("\\w+")) {
            signInStatusLabel.setText("Use word letters for username");
        } else if (!password.matches("\\w+")) {
            signInStatusLabel.setText("Use word letters for password");
        } else if (account == null || !account.getPassword().equals(password)) {
            signInStatusLabel.setText("Wrong username or password");
        } else if (account instanceof Seller && ((Seller) account).getStatus().equals(Status.PENDING)) {
            signInStatusLabel.setText("Account to be Confirmed");
        }  else if (account instanceof Seller && ((Seller) account).getStatus().equals(Status.DECLINED)) {
            signInStatusLabel.setText("Account not allowed");
        } else {
            App.setSignedInAccount(account);
            if (account instanceof Manager) {
                App.setRoot("managerProfile");
                ManagerProfileController.prevPane = RegisterPanelController.prevPane;
            }
            if (account instanceof Seller) {
                App.setRoot("sellerProfile");
                SellerProfileController.prevPane = RegisterPanelController.prevPane;
            }
            if (account instanceof Buyer) {
                App.setRoot("buyerProfile");
                BuyerProfileController.prevPane = RegisterPanelController.prevPane;
            }
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
    private void chooseImage(MouseEvent event) {
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
        App.setRoot(prevPane);
    }

    @FXML
    private void disableChange() {
        loginButton.setDisable(true);
    }
}

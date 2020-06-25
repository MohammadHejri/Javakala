package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Account;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Account.Manager;
import JavaProject.Model.Account.Seller;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    private int letterIndex;
    @FXML Label letter1;
    @FXML Label letter2;
    @FXML Label letter3;
    @FXML Label letter4;
    @FXML Label letter5;
    @FXML Label letter6;
    @FXML Label letter7;
    @FXML Label letter8;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        runAnimation();
    }

    private void runAnimation() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> {
            letterIndex++;
            letterIndex %= 8;
            ArrayList<Label> allLetters = new ArrayList<>();
            allLetters.add(letter1);
            allLetters.add(letter2);
            allLetters.add(letter3);
            allLetters.add(letter4);
            allLetters.add(letter5);
            allLetters.add(letter6);
            allLetters.add(letter7);
            allLetters.add(letter8);
            Label nowLetter = allLetters.get(letterIndex);
            Label prevLetter = allLetters.get((letterIndex + 7) % 8);
            nowLetter.setStyle("-fx-font-size: 72");
            prevLetter.setStyle("-fx-font-size: 60");
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void goToAccount(MouseEvent mouseEvent) throws IOException {
        Account account = App.getSignedInAccount();
        if (account == null) {
            App.setRoot("signIn");
            SignInController.prevPane = App.mainPage;
        } else if (account instanceof Manager) {
            App.setRoot("managerProfile");
            ManagerProfileController.prevPane = App.mainPage;
        } else if (account instanceof Seller) {
            App.setRoot("sellerProfile");
            SellerProfileController.prevPane = App.mainPage;
        } else if (account instanceof Buyer) {
            App.setRoot("buyerProfile");
            BuyerProfileController.prevPane = App.mainPage;
        }
    }

    @FXML
    private void goToProducts(MouseEvent mouseEvent) throws IOException {
        if (App.productsPage == null)
            App.productsPage = App.loadFXML("productsPage");
        App.setRoot(App.productsPage);
        ProductsPageController.prevPane = App.mainPage;
    }

    @FXML
    private void goToCart(MouseEvent mouseEvent) throws IOException {
        if (App.cartPage == null)
            App.cartPage = App.loadFXML("cart");
        App.setRoot(App.cartPage);
        CartController.prevPane = App.mainPage;
    }
}

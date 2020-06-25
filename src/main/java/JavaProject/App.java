package JavaProject;

import JavaProject.Controller.ProductsPageController;
import JavaProject.Model.Account.Account;
import JavaProject.Model.Database.Database;
import JavaProject.Model.ProductOrganization.Cart;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private static Account signedInAccount;

    public static Parent mainPage;
    public static Parent productsPage;
    public static Parent cartPage;


    private static ProductsPageController productsPageController;
    private static Cart cart = new Cart();

    public static void initProductsPage() {
        try {
            productsPage = loadFXML("productsPage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void start(Stage stage) throws Exception {
        Database.getInstance().loadResources();

        mainPage = loadFXML("mainMenu");
        productsPage = loadFXML("productsPage");

        scene = new Scene(mainPage);
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static void setRoot(Parent root) {
        scene.setRoot(root);
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Account getSignedInAccount() {
        return signedInAccount;
    }

    public static void setSignedInAccount(Account signedInAccount) {
        App.signedInAccount = signedInAccount;
    }

    public static Cart getCart() {
        return cart;
    }

    public static void setCart(Cart cart) {
        App.cart = cart;
    }

    public static Parent getProductsPage() {
        return productsPage;
    }

    public static void main(String[] args) {
        launch();
    }
}
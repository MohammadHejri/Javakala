package JavaProject;

import JavaProject.Controller.ProductViewController;
import JavaProject.Controller.ProductsPageController;
import JavaProject.Model.Account.Account;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Database.Database;
import JavaProject.Model.ProductOrganization.Cart;
import JavaProject.Model.ProductOrganization.Category;
import JavaProject.Model.ProductOrganization.Filter.Filter;
import JavaProject.Model.ProductOrganization.Product;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class App extends Application {

    private static Scene scene;
    private static Account signedInAccount;
    private static Parent productsPage;
    private static ProductsPageController productsPageController;
    private static Cart cart = new Cart();

    @Override
    public void start(Stage stage) throws Exception {
        Database.getInstance().loadResources();
        initProductsPage();
        scene = new Scene(loadFXML("signIn"));
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

    public static Parent getProductsPage() {
        return productsPage;
    }

    public static void setProductsPage(Parent productsPage) {
        App.productsPage = productsPage;
    }

    public static ProductsPageController getProductsPageController() {
        return productsPageController;
    }

    public static void setProductsPageController(ProductsPageController productsPageController) {
        App.productsPageController = productsPageController;
    }

    public static Cart getCart() {
        return cart;
    }

    public static void setCart(Cart cart) {
        App.cart = cart;
    }

    public static void initProductsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("productsPage.fxml"));
            productsPage = loader.load();
            productsPageController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
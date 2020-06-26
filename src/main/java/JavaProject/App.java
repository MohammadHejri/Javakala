package JavaProject;

import JavaProject.Controller.ProductsPageController;
import JavaProject.Model.Account.Account;
import JavaProject.Model.Database.Database;
import JavaProject.Model.ProductOrganization.Cart;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;

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
        mediaPlayer.play();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        changeBgMusic();
    }

    public static void setRoot(Parent root) {
        scene.setRoot(root);
        changeBgMusic();
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

    private static int option = 0;

    private static MediaPlayer mediaPlayer = new MediaPlayer(new Media(Paths.get("src/main/resources/APPROJMUSIC/1.mp3").toUri().toString()));

    private static String[] backgroundMusics = new String[]{Paths.get("src/main/resources/APPROJMUSIC/2.mp3").toUri().toString(),
            Paths.get("src/main/resources/APPROJMUSIC/3.mp3").toUri().toString()
            , Paths.get("src/main/resources/APPROJMUSIC/4.mp3").toUri().toString()
            , Paths.get("src/main/resources/APPROJMUSIC/1.mp3").toUri().toString()};

    public static void changeBgMusic(){
        mediaPlayer.stop();
        mediaPlayer = new MediaPlayer(new Media(backgroundMusics[(++option)%4]));
        mediaPlayer.play();
    }
}
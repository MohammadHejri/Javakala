package JavaProject.Controller;

import JavaProject.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SupporterProfileController implements Initializable {
    public static Parent prevPane;

    @FXML
    BorderPane borderPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            borderPane.setCenter(App.loadFXML("accountInfo"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAccountInfoSection(ActionEvent event) throws IOException {
        borderPane.setCenter(App.loadFXML("accountInfo"));
    }

    @FXML
    private void signOut(ActionEvent event) throws IOException {
        String response = App.getResponseFromServer("signOut", App.getSignedInAccount().getUsername());
        App.setSignedInAccount(null);
        App.setRoot(App.mainPage);
    }

    @FXML
    private void changeToPrevPane(ActionEvent event) throws IOException {
        App.setRoot(prevPane);
    }

    @FXML
    private void changeToMainMenu() {
        App.setRoot(App.mainPage);
    }

    public void openChatSection(ActionEvent event) throws IOException {
        App.setRoot("chat");
    }
}

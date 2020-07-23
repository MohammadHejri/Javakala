package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.*;
import JavaProject.Model.Chat.Conversation;
import JavaProject.Model.Chat.Message;
import JavaProject.Model.Database.Database;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.w3c.dom.events.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    public static Parent prevPane;
    Timeline timeline;

    @FXML
    ScrollPane chatPane;
    @FXML
    VBox chatVBox;
    @FXML
    Button sendButton;
    @FXML
    TextArea messageArea;
    @FXML
    TableView<Account> userTable;
    @FXML
    TableColumn<Account, String> usernameColumn;
    @FXML
    TableColumn<Account, String> networkColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        timeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> initChat()));
        chatPane.vvalueProperty().bind(chatVBox.heightProperty());
        chatPane.setVisible(false);
        messageArea.setVisible(false);
        sendButton.setVisible(false);
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        networkColumn.setCellValueFactory(new PropertyValueFactory<>("network"));
        for (Account account : Database.getInstance().getAllAccounts()) {
            if (App.getSignedInAccount() instanceof Buyer) {
                if (account instanceof Supporter) {
                    String response = App.getResponseFromServer("isOnlineUser", account.getUsername());
                    account.setNetwork(response.equals("True") ? "online" : "offline");
                    userTable.getItems().add(account);
                }
            }
            if (App.getSignedInAccount() instanceof Supporter) {
                if (account instanceof Buyer) {
                    String response = App.getResponseFromServer("isOnlineUser", account.getUsername());
                    account.setNetwork(response.equals("True") ? "online" : "offline");
                    String hadConversation = App.getResponseFromServer("hadConversation", App.getSignedInAccount().getUsername(), account.getUsername());
                    if (hadConversation.equals("True"))
                        userTable.getItems().add(account);
                    System.out.println(account.getUsername() + "&&&" + hadConversation);
                }
            }
        }
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                chatPane.setVisible(true);
                messageArea.setVisible(true);
                sendButton.setVisible(true);
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.play();
            } else {
                chatPane.setVisible(false);
                messageArea.setVisible(false);
                sendButton.setVisible(false);
                timeline.stop();
            }
        });
    }

    private void initChat() {
        String firstSide = App.getSignedInAccount().getUsername();
        String secondSide = userTable.getSelectionModel().getSelectedItem().getUsername();
        String conversationAsString =  App.getResponseFromServer("getConversation", firstSide, secondSide);
        Conversation conversation = App.stringToObject(conversationAsString, Conversation.class);
        chatVBox.getChildren().clear();
        for (Message message : conversation.getMesaages())
            addMessageToChatPane(message);
    }

    @FXML
    private void sendMessage() {
        String content = messageArea.getText().trim();
        String senderUsername = App.getSignedInAccount().getUsername();
        String recieverUsername = userTable.getSelectionModel().getSelectedItem().getUsername();
        messageArea.setText("");
        if (content.isBlank()) return;
        Message message = new Message(content, senderUsername, recieverUsername);
        addMessageToChatPane(message);
        App.getResponseFromServer("saveMessageAndNotify", App.objectToString(message));
    }

    private void addMessageToChatPane(Message message) {
        TextArea textArea = new TextArea(message.getContent());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMinWidth(350);
        textArea.setMaxWidth(350);
        textArea.setMinHeight(45);
        textArea.setMaxHeight(45);
        HBox hBox = new HBox(textArea);
        if (message.getRecieverUsername().equals(userTable.getSelectionModel().getSelectedItem().getUsername())) {
            hBox.setAlignment(Pos.BOTTOM_LEFT);
        } else {
            hBox.setAlignment(Pos.BOTTOM_RIGHT);
        }
        chatVBox.getChildren().add(hBox);
    }

    @FXML
    private void changeToPrevPane() throws IOException {
        timeline.stop();
        if (App.getSignedInAccount() instanceof Buyer)
            App.setRoot("buyerProfile");
        if (App.getSignedInAccount() instanceof Supporter)
            App.setRoot("supporterProfile");
    }

}

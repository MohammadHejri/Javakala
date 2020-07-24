package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Database.Database;
import JavaProject.Model.DualString;
import JavaProject.Model.Log.BuyLog;
import JavaProject.Model.Log.ProductOnLog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ShopPropertiesController implements Initializable {

    @FXML
    TableColumn addressColumn;
    @FXML
    TableColumn statusColumn;
    @FXML
    TableView<BuyLog> logsTable;
    @FXML
    TableColumn<BuyLog, String> logIDColumn;
    @FXML
    TableColumn<BuyLog, String> dateColumn;
    @FXML
    TableColumn<BuyLog, String> moneyColumn;
    @FXML
    TableColumn<BuyLog, String> discountColumn;
    @FXML
    TableView<ProductOnLog> productsTable;
    @FXML
    TableColumn<ProductOnLog, String> productColumn;
    @FXML
    TableColumn<ProductOnLog, String> quantityColumn;
    @FXML
    TableColumn<ProductOnLog, String> sellerColumn;
    @FXML
    TableColumn<ProductOnLog, String> productMoneyColumn;
    @FXML
    TableColumn<ProductOnLog, String> productDiscountColumn;
    @FXML
    TableColumn<ProductOnLog, String> auctionIDColumn;
    @FXML
    TableColumn<ProductOnLog, String> fileNameColumn;

    @FXML
    Button deliveredButton;

    @FXML
    TextField minBalanceField;
    @FXML
    TextField shopCommissionField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String response = App.getResponseFromServer("getShopProperties");
        shopCommissionField.setPromptText(App.stringToObject(response, DualString.class).getKey());
        minBalanceField.setPromptText(App.stringToObject(response, DualString.class).getValue());

        logIDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("buyDate"));
        moneyColumn.setCellValueFactory(new PropertyValueFactory<>("paidAmount"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("decreasedAmount"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        deliveredButton.setDisable(true);

        productColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        sellerColumn.setCellValueFactory(new PropertyValueFactory<>("sellerUsername"));
        productMoneyColumn.setCellValueFactory(new PropertyValueFactory<>("transferredMoney"));
        productDiscountColumn.setCellValueFactory(new PropertyValueFactory<>("decreasedMoney"));
        auctionIDColumn.setCellValueFactory(new PropertyValueFactory<>("auctionID"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));

        for (BuyLog buyLog : Database.getInstance().getAllBuyLogs())
            logsTable.getItems().add(buyLog);
        logsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) selectLog();
            else deselectLog();
        });
    }

    private void selectLog() {
        deliveredButton.setDisable(false);
        BuyLog buyLog = logsTable.getSelectionModel().getSelectedItem();
        productsTable.getItems().clear();
        for (ProductOnLog productOnLog : buyLog.getBoughtProducts())
            productsTable.getItems().add(productOnLog);
    }

    private void deselectLog() {
        deliveredButton.setDisable(true);
        productsTable.getItems().clear();
    }

    @FXML
    private void updateShopCommission() {
        String shopCommissionStr = shopCommissionField.getText().trim();
        String response = App.getResponseFromServer("updateShopCommission", shopCommissionStr);
        if (response.startsWith("Success")) {
            new Alert(Alert.AlertType.INFORMATION, "Shop commission updated").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, response).showAndWait();
        }
    }

    @FXML
    private void updateMinBalance() {
        String minBalanceStr = minBalanceField.getText().trim();
        String response = App.getResponseFromServer("updateMinBalance", minBalanceStr);
        if (response.startsWith("Success")) {
            new Alert(Alert.AlertType.INFORMATION, "Minimum balance updated").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, response).showAndWait();
        }
    }

    @FXML
    private void changeToDelivered(ActionEvent event) {
        BuyLog buyLog = logsTable.getSelectionModel().getSelectedItem();
        App.getResponseFromServer("deliverProducts", buyLog.getID());
        logsTable.getItems().clear();
        productsTable.getItems().clear();
        for (BuyLog BL : Database.getInstance().getAllBuyLogs())
            logsTable.getItems().add(BL);
    }
}

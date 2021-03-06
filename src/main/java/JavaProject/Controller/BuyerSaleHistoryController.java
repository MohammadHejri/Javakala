package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Log.BuyLog;
import JavaProject.Model.Log.ProductOnLog;
import JavaProject.Model.Log.SellLog;
import JavaProject.Model.ProductOrganization.Comment;
import JavaProject.Model.ProductOrganization.Product;
import JavaProject.Model.ProductOrganization.Rate;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

// Client-Server : Done

public class BuyerSaleHistoryController implements Initializable {
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
    TextField productNameField;
    @FXML
    TextField productMarkField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logIDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("buyDate"));
        moneyColumn.setCellValueFactory(new PropertyValueFactory<>("paidAmount"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("decreasedAmount"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        productColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        sellerColumn.setCellValueFactory(new PropertyValueFactory<>("sellerUsername"));
        productMoneyColumn.setCellValueFactory(new PropertyValueFactory<>("transferredMoney"));
        productDiscountColumn.setCellValueFactory(new PropertyValueFactory<>("decreasedMoney"));
        auctionIDColumn.setCellValueFactory(new PropertyValueFactory<>("auctionID"));
        for (String buyLogID : ((Buyer) App.getSignedInAccount()).getBuyLogsID())
            logsTable.getItems().add(Database.getInstance().getBuyLogByID(buyLogID));
        logsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) selectLog();
            else deselectLog();
        });
    }

    private void selectLog() {
        BuyLog buyLog = logsTable.getSelectionModel().getSelectedItem();
        productsTable.getItems().clear();
        for (ProductOnLog productOnLog : buyLog.getBoughtProducts())
            productsTable.getItems().add(productOnLog);
    }

    private void deselectLog() {
        productsTable.getItems().clear();
    }

    @FXML
    private void rate() throws IOException {
        String productName = productNameField.getText().trim();
        String productMark = productMarkField.getText().trim();
        String username = App.getSignedInAccount().getUsername();
        String response = App.getResponseFromServer("rateProduct", productName, productMark, username);
        if (response.startsWith("Success")) {
            new Alert(Alert.AlertType.INFORMATION, "Thanks for rating this product").showAndWait();
            productMarkField.setText("");
            productNameField.setText("");
        } else {
            new Alert(Alert.AlertType.ERROR, response).showAndWait();
        }
    }
}

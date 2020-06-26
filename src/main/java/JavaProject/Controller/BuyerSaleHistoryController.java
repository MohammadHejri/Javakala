package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Log.BuyLog;
import JavaProject.Model.Log.ProductOnLog;
import JavaProject.Model.Log.SellLog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class BuyerSaleHistoryController implements Initializable {
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logIDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("buyDate"));
        moneyColumn.setCellValueFactory(new PropertyValueFactory<>("paidAmount"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("decreasedAmount"));
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
}

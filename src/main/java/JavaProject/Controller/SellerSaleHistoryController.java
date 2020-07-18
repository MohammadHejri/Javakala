package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Log.ProductOnLog;
import JavaProject.Model.Log.SellLog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

// Client-Server : Done

public class SellerSaleHistoryController implements Initializable {
    @FXML
    TableView<SellLog> logsTable;
    @FXML
    TableColumn<SellLog, String> logIDColumn;
    @FXML
    TableColumn<SellLog, String> dateColumn;
    @FXML
    TableColumn<SellLog, String> buyerColumn;
    @FXML
    TableColumn<SellLog, String> moneyColumn;
    @FXML
    TableColumn<SellLog, String> discountColumn;
    @FXML
    TableView<ProductOnLog> productsTable;
    @FXML
    TableColumn<ProductOnLog, String> productColumn;
    @FXML
    TableColumn<ProductOnLog, String> quantityColumn;
    @FXML
    TableColumn<ProductOnLog, String> productMoneyColumn;
    @FXML
    TableColumn<ProductOnLog, String> productDiscountColumn;
    @FXML
    TableColumn<ProductOnLog, String> auctionIDColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logIDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("sellDate"));
        buyerColumn.setCellValueFactory(new PropertyValueFactory<>("buyerUsername"));
        moneyColumn.setCellValueFactory(new PropertyValueFactory<>("gainedAmount"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("decreasedAmount"));
        productColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        productMoneyColumn.setCellValueFactory(new PropertyValueFactory<>("transferredMoney"));
        productDiscountColumn.setCellValueFactory(new PropertyValueFactory<>("decreasedMoney"));
        auctionIDColumn.setCellValueFactory(new PropertyValueFactory<>("auctionID"));
        for (String sellLogID : ((Seller) App.getSignedInAccount()).getSellLogsID())
            logsTable.getItems().add(Database.getInstance().getSellLogByID(sellLogID));
        logsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) selectLog();
            else deselectLog();
        });

    }

    private void selectLog() {
        SellLog sellLog = logsTable.getSelectionModel().getSelectedItem();
        productsTable.getItems().clear();
        for (ProductOnLog productOnLog : sellLog.getSoldProducts())
            productsTable.getItems().add(productOnLog);
    }

    private void deselectLog() {
        productsTable.getItems().clear();
    }
}

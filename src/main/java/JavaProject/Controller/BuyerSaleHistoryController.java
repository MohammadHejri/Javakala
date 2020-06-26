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
        if (productName.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter product name").showAndWait();
        } else if (productMark.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter product mark").showAndWait();
        } else if (Database.getInstance().getProductByName(productName) == null) {
            new Alert(Alert.AlertType.ERROR, "Product not found").showAndWait();
        } else if (!productMark.matches("(\\d+)(\\.\\d+)?")) {
            new Alert(Alert.AlertType.ERROR, "Use DOUBLE for mark").showAndWait();
        } else {
            Product product = Database.getInstance().getProductByName(productName);
            double mark = Double.parseDouble(productMark);
            if (mark > 5 || mark < 0) {
                new Alert(Alert.AlertType.ERROR, "Mark should be in range [0-5]").showAndWait();
                return;
            }
            if (!product.getBuyers().contains(App.getSignedInAccount().getUsername())) {
                new Alert(Alert.AlertType.ERROR, "You have not purchased this product to rate it").showAndWait();
                return;
            }
            for (Rate rate : product.getRates()) {
                if (rate.getBuyerName().equals(App.getSignedInAccount().getUsername())) {
                    new Alert(Alert.AlertType.ERROR, "You have rated this product before").showAndWait();
                    return;
                }
            }
            Rate rate = new Rate(App.getSignedInAccount().getUsername(), mark);
            product.getRates().add(rate);
            product.updateMark();
            productMarkField.setText("");
            productNameField.setText("");
            Database.getInstance().updateCategories();
            new Alert(Alert.AlertType.INFORMATION, "Thanks for rating this product").showAndWait();
        }
    }
}

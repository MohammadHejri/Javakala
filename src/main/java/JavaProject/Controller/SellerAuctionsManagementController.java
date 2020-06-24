package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Discount.Auction;
import JavaProject.Model.ProductOrganization.Product;
import JavaProject.Model.Request.Request;
import JavaProject.Model.Request.Subject;
import JavaProject.Model.Status.Status;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class SellerAuctionsManagementController implements Initializable {

    Auction selectedAuction;

    @FXML
    ListView<String> auctionsList;
    @FXML
    ListView<String> productsList;
    @FXML
    ListView<String> auctionProductsList;
    @FXML
    TextField startDateField;
    @FXML
    TextField endDateField;
    @FXML
    TextField percentField;
    @FXML
    Label statusLabel;
    @FXML
    Button addButton;
    @FXML
    Button editButton;
    @FXML
    Button deleteButton;
    @FXML
    Button addProductButton;
    @FXML
    Button removeProductButton;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addButton.setDisable(false);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        addProductButton.setDisable(true);
        removeProductButton.setDisable(true);

        for (String auctionID : ((Seller) App.getSignedInAccount()).getAuctionsID())
            auctionsList.getItems().add(auctionID);
        auctionsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) selectAuction();
            else deselectAuction();
        });

        for (String productID : Database.getInstance().getProductsInNoAuction((Seller) App.getSignedInAccount())) {
            Product product = Database.getInstance().getProductByID(productID);
            productsList.getItems().add(product.getName());
        }
        productsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) addProductButton.setDisable(false);
            else addProductButton.setDisable(true);
        });

        auctionProductsList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) removeProductButton.setDisable(false);
            else removeProductButton.setDisable(true);
        });

    }

    private void selectAuction() {
        addButton.setDisable(true);
        editButton.setDisable(false);
        deleteButton.setDisable(false);
        String auctionID = auctionsList.getSelectionModel().getSelectedItem();
        selectedAuction = Database.getInstance().getAuctionByID(auctionID);
        startDateField.setText(selectedAuction.getStartDate());
        endDateField.setText(selectedAuction.getEndDate());
        percentField.setText(String.valueOf(selectedAuction.getDiscountPercent()));
        productsList.getItems().clear();
        for (String productID : Database.getInstance().getProductsInNoAuction((Seller) App.getSignedInAccount())) {
            Product product = Database.getInstance().getProductByID(productID);
            productsList.getItems().add(product.getName());
        }
        auctionProductsList.getItems().clear();
        for (String ID : selectedAuction.getProductsID()) {
            Product product = Database.getInstance().getProductByID(ID);
            auctionProductsList.getItems().add(product.getName());
        }
    }

    private void deselectAuction() {
        addButton.setDisable(false);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        selectedAuction = null;
        startDateField.setText("");
        endDateField.setText("");
        percentField.setText("");
        productsList.getItems().clear();
        for (String productID : Database.getInstance().getProductsInNoAuction((Seller) App.getSignedInAccount())) {
            Product product = Database.getInstance().getProductByID(productID);
            productsList.getItems().add(product.getName());
        }
        auctionProductsList.getItems().clear();
    }

    @FXML
    private void requestAddOrEditAuction(ActionEvent event) throws IOException {
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();
        String percentStr = percentField.getText().trim();
        String dateTimeRegex = "([12]\\d{3})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]) ([0-1]?[0-9]|2[0-3]):[0-5][0-9]";
        if (startDate.isBlank()) {
            statusLabel.setText("Enter start date!");
        } else if (endDate.isBlank()) {
            statusLabel.setText("Enter end date!");
        } else if (percentStr.isBlank()) {
            statusLabel.setText("Enter discount percent!");
        } else if (!startDate.matches(dateTimeRegex)) {
            statusLabel.setText("Use format yyyy-mm-dd hh:mm for start date!");
        } else if (!endDate.matches(dateTimeRegex)) {
            statusLabel.setText("Use format yyyy-mm-dd hh:mm for end date!");
        } else if (startDate.compareTo(endDate) >= 0) {
            statusLabel.setText("Start date should be before end date");
        } else if (!percentStr.matches("(\\d+)(\\.\\d+)?")) {
            statusLabel.setText("Use DOUBLE for price!");
        } else {
            double discountPercent = Double.parseDouble(percentStr);
            if (discountPercent > 100 || discountPercent == 0) {
                statusLabel.setText("Percent should be in range (0-100]");
            } else {
                Seller seller = (Seller) App.getSignedInAccount();
                ArrayList<String> productsID = new ArrayList<>();
                for (String productName : auctionProductsList.getItems())
                    productsID.add(Database.getInstance().getProductByName(productName).getID());
                Auction auction = new Auction(seller.getUsername(), startDate, endDate, discountPercent, productsID);
                Request request = null;
                if (selectedAuction == null) {
                    request = new Request(Subject.ADD_AUCTION, auction.toString());
                    statusLabel.setText("Adding auction requested");
                } else {
                    auction.setID(selectedAuction.getID());
                    request = new Request(Subject.EDIT_AUCTION, "Edited " + auction.toString() + "\n" + "Current " + selectedAuction.toString());
                    statusLabel.setText("Editing auction requested");
                }
                request.setAuction(auction);
                Database.getInstance().saveRequest(request);
            }
        }
    }

    @FXML
    private void requestDeleteAuction(ActionEvent event) throws IOException {
        Auction auction = Database.getInstance().getAuctionByID(auctionsList.getSelectionModel().getSelectedItem());
        Request request = new Request(Subject.DELETE_AUCTION, auction.toString());
        request.setAuction(auction);
        Database.getInstance().saveRequest(request);
        statusLabel.setText("Deleting auction requested");
    }

    @FXML
    private void addProduct(ActionEvent event) {
        String productName = productsList.getSelectionModel().getSelectedItem();
        productsList.getItems().remove(productName);
        auctionProductsList.getItems().add(productName);
    }

    @FXML
    private void removeProduct(ActionEvent event) {
        String productName = auctionProductsList.getSelectionModel().getSelectedItem();
        auctionProductsList.getItems().remove(productName);
        productsList.getItems().add(productName);
    }

}

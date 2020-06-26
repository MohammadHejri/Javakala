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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

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
    Button addButton;
    @FXML
    Button editButton;
    @FXML
    Button deleteButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addButton.setDisable(false);
        editButton.setDisable(true);
        deleteButton.setDisable(true);

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

        productsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    String productName = productsList.getSelectionModel().getSelectedItem();
                    productsList.getItems().remove(productName);
                    productsList.getSelectionModel().clearSelection();
                    auctionProductsList.getItems().add(productName);
                }
            }
        });
        auctionProductsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    String productName = auctionProductsList.getSelectionModel().getSelectedItem();
                    auctionProductsList.getItems().remove(productName);
                    auctionProductsList.getSelectionModel().clearSelection();
                    productsList.getItems().add(productName);
                }
            }
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
            new Alert(Alert.AlertType.ERROR, "Enter start date").showAndWait();
        } else if (endDate.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter end date").showAndWait();
        } else if (percentStr.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter discount percent").showAndWait();
        } else if (!startDate.matches(dateTimeRegex)) {
            new Alert(Alert.AlertType.ERROR, "Use format yyyy-mm-dd hh:mm for start date").showAndWait();
        } else if (!endDate.matches(dateTimeRegex)) {
            new Alert(Alert.AlertType.ERROR, "Use format yyyy-mm-dd hh:mm for end date").showAndWait();
        } else if (startDate.compareTo(endDate) >= 0) {
            new Alert(Alert.AlertType.ERROR, "Start date should be before end date").showAndWait();
        } else if (!percentStr.matches("(\\d+)(\\.\\d+)?")) {
            new Alert(Alert.AlertType.ERROR, "Use DOUBLE for price").showAndWait();
        } else {
            double discountPercent = Double.parseDouble(percentStr);
            if (discountPercent > 100 || discountPercent <= 0) {
                new Alert(Alert.AlertType.ERROR, "Percent should be in range (0-100]").showAndWait();
            } else {
                Seller seller = (Seller) App.getSignedInAccount();
                ArrayList<String> productsID = new ArrayList<>();
                for (String productName : auctionProductsList.getItems())
                    productsID.add(Database.getInstance().getProductByName(productName).getID());
                Auction auction = new Auction(seller.getUsername(), startDate, endDate, discountPercent, productsID);
                Request request = null;
                if (selectedAuction == null) {
                    request = new Request(Subject.ADD_AUCTION, auction.toString());
                    new Alert(Alert.AlertType.INFORMATION, "Adding auction requested").showAndWait();
                } else {
                    auction.setID(selectedAuction.getID());
                    request = new Request(Subject.EDIT_AUCTION, "Edited " + auction.toString() + "\n" + "Current " + selectedAuction.toString());
                    new Alert(Alert.AlertType.INFORMATION, "Editing auction requested").showAndWait();
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
        new Alert(Alert.AlertType.INFORMATION, "Deleting auction requested").showAndWait();
    }
}

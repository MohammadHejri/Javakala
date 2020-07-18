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

// Client-Server : Done

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
        String ID = selectedAuction == null ? null : selectedAuction.getID();
        String startDate = startDateField.getText().trim();
        String endDate = endDateField.getText().trim();
        String percentStr = percentField.getText().trim();
        String sellerUsername = App.getSignedInAccount().getUsername();
        ArrayList<String> auctionProductsName = new ArrayList<>(auctionProductsList.getItems());

        String response = App.getResponseFromServer("requestAddOrEditAuction", ID, startDate, endDate, percentStr, sellerUsername, App.objectToString(auctionProductsName));
        if (response.startsWith("Success")) {
            new Alert(Alert.AlertType.INFORMATION, "Adding | Editing Auction requested").showAndWait();
        } else {
            new Alert(Alert.AlertType.ERROR, response).showAndWait();
        }
    }

    @FXML
    private void requestDeleteAuction(ActionEvent event) throws IOException {
        App.getResponseFromServer("requestDeleteAuction", auctionsList.getSelectionModel().getSelectedItem());
        new Alert(Alert.AlertType.INFORMATION, "Deleting auction requested").showAndWait();
    }
}

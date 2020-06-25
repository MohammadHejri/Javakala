package JavaProject.Controller;

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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class RequestsManagementController implements Initializable {

    @FXML
    TableView<Request> requestsTable;
    @FXML
    TableColumn<Request, String> requestIDColumn;
    @FXML
    TableColumn<Request, Subject> subjectColumn;
    @FXML
    TextArea descriptionArea;
    @FXML
    Button acceptButton;
    @FXML
    Button declineButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        acceptButton.setDisable(true);
        declineButton.setDisable(true);
        requestIDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        for (Request request : Database.getInstance().getAllRequests())
            if (request.getStatus().equals(Status.PENDING))
                requestsTable.getItems().add(request);
        requestsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectRequest();
            } else {
                deselectRequest();
            }
        });
    }

    @FXML
    public void acceptRequest(ActionEvent event) throws IOException {
        Request request = requestsTable.getSelectionModel().getSelectedItem();
        if (request.getSubject().equals(Subject.SELLER_REGISTER)) {
            String sellerUsername = request.getSeller().getUsername();
            Seller seller = (Seller) Database.getInstance().getAccountByUsername(sellerUsername);
            seller.setStatus(Status.ACCEPTED);
            request.getSeller().setStatus(Status.ACCEPTED);
            Database.getInstance().saveAccount(seller);
        } else if (request.getSubject().equals(Subject.ADD_PRODUCT)) {
            String productName = request.getProduct().getName();
            Product product = Database.getInstance().getProductByName(productName);
            product.setStatus(Status.ACCEPTED);
            request.getProduct().setStatus(Status.ACCEPTED);
            Database.getInstance().updateCategories();
        } else if (request.getSubject().equals(Subject.EDIT_PRODUCT)) {
            String productID = request.getProduct().getID();
            Product product = Database.getInstance().getProductByID(productID);
            product.setName(request.getProduct().getName());
            product.setBrand(request.getProduct().getBrand());
            product.setPrice(request.getProduct().getPrice());
            product.setRemainingItems(request.getProduct().getRemainingItems());
            product.setDescription(request.getProduct().getDescription());
            product.setImagePath(request.getProduct().getImagePath());
            product.setSpecs(new HashMap<>(request.getProduct().getSpecs()));
            String prevParentCat = product.getParentCategoryName();
            String newParentCat = request.getProduct().getParentCategoryName();
            Database.getInstance().getCategoryByName(prevParentCat).getProducts().remove(product);
            Database.getInstance().getCategoryByName(newParentCat).getProducts().add(product);
            product.setParentCategoryName(newParentCat);
            product.setStatus(Status.ACCEPTED);
            request.getProduct().setStatus(Status.ACCEPTED);
            Database.getInstance().updateCategories();
        } else if (request.getSubject().equals(Subject.DELETE_PRODUCT)) {
            String productID = request.getProduct().getID();
            Product product = Database.getInstance().getProductByID(productID);
            Database.getInstance().deleteProduct(product);
        } else if (request.getSubject().equals(Subject.ADD_AUCTION)) {
            request.getAuction().setStatus(Status.ACCEPTED);
            Auction auction = new Auction(request.getAuction());
            String sellerUsername = auction.getSellerUsername();
            ((Seller) Database.getInstance().getAccountByUsername(sellerUsername)).getAuctionsID().add(auction.getID());
            for (String productID : auction.getProductsID())
                Database.getInstance().getProductByID(productID).setAuctionID(auction.getID());
            Database.getInstance().saveAccount(Database.getInstance().getAccountByUsername(sellerUsername));
            Database.getInstance().saveAuction(auction);
            Database.getInstance().updateCategories();
        } else if (request.getSubject().equals(Subject.EDIT_AUCTION)) {
            request.getAuction().setStatus(Status.ACCEPTED);
            Auction auction = Database.getInstance().getAuctionByID(request.getAuction().getID());
            auction.setStartDate(request.getAuction().getStartDate());
            auction.setEndDate(request.getAuction().getEndDate());
            auction.setStartDate(request.getAuction().getStartDate());
            auction.setStartDate(request.getAuction().getStartDate());
            auction.setDiscountPercent(request.getAuction().getDiscountPercent());
            for (String productID : auction.getProductsID())
                Database.getInstance().getProductByID(productID).setAuctionID(null);
            for (String productID : request.getAuction().getProductsID())
                Database.getInstance().getProductByID(productID).setAuctionID(auction.getID());
            auction.setProductsID(new ArrayList<>(request.getAuction().getProductsID()));
            Database.getInstance().saveAuction(auction);
            Database.getInstance().updateCategories();
        } else if (request.getSubject().equals(Subject.DELETE_AUCTION)) {
            String auctionID = request.getAuction().getID();
            Auction auction = Database.getInstance().getAuctionByID(auctionID);
            Database.getInstance().deleteAuction(auction);
        }
        request.setStatus(Status.ACCEPTED);
        Database.getInstance().saveRequest(request);
        requestsTable.getItems().clear();
        for (Request req : Database.getInstance().getAllRequests())
            if (req.getStatus().equals(Status.PENDING))
                requestsTable.getItems().add(req);
        new Alert(Alert.AlertType.INFORMATION, "Request accepted").showAndWait();
    }

    @FXML
    public void declineRequest(ActionEvent event) throws IOException {
        Request request = requestsTable.getSelectionModel().getSelectedItem();
        if (request.getSubject().equals(Subject.SELLER_REGISTER)) {
            String sellerUsername = request.getSeller().getUsername();
            Seller seller = (Seller) Database.getInstance().getAccountByUsername(sellerUsername);
            seller.setStatus(Status.DECLINED);
            request.getSeller().setStatus(Status.DECLINED);
            Database.getInstance().saveAccount(seller);
        } else if (request.getSubject().equals(Subject.ADD_PRODUCT)) {
            String productName = request.getProduct().getName();
            Product product = Database.getInstance().getProductByName(productName);
            product.setStatus(Status.DECLINED);
            request.getProduct().setStatus(Status.DECLINED);
            Database.getInstance().updateCategories();
        } else if (request.getSubject().equals(Subject.ADD_AUCTION)) {
            request.getAuction().setStatus(Status.DECLINED);
        }
        request.setStatus(Status.DECLINED);
        Database.getInstance().saveRequest(request);
        requestsTable.getItems().clear();
        for (Request req : Database.getInstance().getAllRequests())
            if (req.getStatus().equals(Status.PENDING))
                requestsTable.getItems().add(req);
        new Alert(Alert.AlertType.INFORMATION, "Request declined").showAndWait();
    }

    private void selectRequest() {
        Request request = requestsTable.getSelectionModel().getSelectedItem();
        descriptionArea.setText(request.getDescription());
        acceptButton.setDisable(false);
        declineButton.setDisable(false);
    }

    private void deselectRequest() {
        descriptionArea.setText("");
        acceptButton.setDisable(true);
        declineButton.setDisable(true);
    }
}

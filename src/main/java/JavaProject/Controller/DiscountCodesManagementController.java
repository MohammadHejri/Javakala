package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Account;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Discount.DiscountCode;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

// Client-Server : Done

public class DiscountCodesManagementController implements Initializable {

    @FXML
    ListView<String> userList;
    @FXML
    TextField codeField;
    @FXML
    TextField percentField;
    @FXML
    TextField maxDiscountField;
    @FXML
    TextField maxUsageField;
    @FXML
    TextField startDateField;
    @FXML
    TextField endDateField;
    @FXML
    TableView<DiscountCode> discountCodeTable;
    @FXML
    TableColumn<DiscountCode, String> codeColumn;
    @FXML
    TableColumn<DiscountCode, String> startDateColumn;
    @FXML
    TableColumn<DiscountCode, String> endDateColumn;
    @FXML
    TableColumn<DiscountCode, Double> percentColumn;
    @FXML
    TableColumn<DiscountCode, Integer> maxDiscountColumn;
    @FXML
    TableColumn<DiscountCode, Integer> maxUsageColumn;
    @FXML
    Button createButton;
    @FXML
    Button editButton;
    @FXML
    Button deleteButton;
    @FXML
    Button generateButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        percentColumn.setCellValueFactory(new PropertyValueFactory<>("discountPercent"));
        maxDiscountColumn.setCellValueFactory(new PropertyValueFactory<>("maxDiscount"));
        maxUsageColumn.setCellValueFactory(new PropertyValueFactory<>("maxUsageNumber"));
        for (DiscountCode discountCode : Database.getInstance().getAllDiscountCodes())
            discountCodeTable.getItems().add(discountCode);
        userList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        for (Account account : Database.getInstance().getAllAccounts())
            if (account instanceof Buyer)
                userList.getItems().add(account.getUsername());
        discountCodeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectDiscountCode();
            } else {
                deselectDiscountCode();
            }
        });
    }

    @FXML
    public void createOrEditDiscountCode(ActionEvent event) throws IOException {
        String code = codeField.getText();
        String startDate = startDateField.getText();
        String endDate = endDateField.getText();
        String percent = percentField.getText();
        String maxDiscount = maxDiscountField.getText();
        String maxUsage = maxUsageField.getText();
        DiscountCode selectedDiscountCode = discountCodeTable.getSelectionModel().getSelectedItem();
        String selectedCode = selectedDiscountCode == null ? null : selectedDiscountCode.getCode();
        ObservableList<String> buyersList = userList.getSelectionModel().getSelectedItems();
        ArrayList<String> buyers = new ArrayList<>(buyersList);
        String response = App.getResponseFromServer("createOrEditDiscountCode", selectedCode, code, startDate, endDate, percent, maxDiscount, maxUsage, App.objectToString(buyers));
        if (response.startsWith("Success")) {
            new Alert(Alert.AlertType.INFORMATION, "Discount code created | edited successfully").showAndWait();
            discountCodeTable.getItems().clear();
            for (DiscountCode DC : Database.getInstance().getAllDiscountCodes())
                discountCodeTable.getItems().add(DC);
        } else {
            new Alert(Alert.AlertType.ERROR, response).showAndWait();
        }

    }

    private void selectDiscountCode() {
        generateButton.setDisable(true);
        createButton.setDisable(true);
        editButton.setDisable(false);
        deleteButton.setDisable(false);
        DiscountCode discountCode = discountCodeTable.getSelectionModel().getSelectedItem();
        codeField.setText(discountCode.getCode());
        startDateField.setText(discountCode.getStartDate());
        endDateField.setText(discountCode.getEndDate());
        percentField.setText(String.valueOf(discountCode.getDiscountPercent()));
        maxDiscountField.setText(String.valueOf(discountCode.getMaxDiscount()));
        maxUsageField.setText(String.valueOf(discountCode.getMaxUsageNumber()));
        userList.getSelectionModel().clearSelection();
        for (String buyerUsername : discountCode.getBuyers().keySet()) {
            for (String username : userList.getItems()) {
                if (buyerUsername.equals(username)) {
                    userList.getSelectionModel().select(username);
                }
            }
        }
    }

    private void deselectDiscountCode() {
        generateButton.setDisable(false);
        createButton.setDisable(false);
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        codeField.setText("");
        startDateField.setText("");
        endDateField.setText("");
        percentField.setText("");
        maxDiscountField.setText("");
        maxUsageField.setText("");
        userList.getSelectionModel().clearSelection();
    }

    @FXML
    private void deleteDiscountCode(ActionEvent event) throws IOException {
        for (DiscountCode discountCode : discountCodeTable.getSelectionModel().getSelectedItems()) {
            App.getResponseFromServer("deleteDiscountCode", discountCode.getCode());
        }
        new Alert(Alert.AlertType.INFORMATION, "Discount code deleted successfully").showAndWait();
        discountCodeTable.getItems().clear();
        for (DiscountCode discountCode : Database.getInstance().getAllDiscountCodes())
            discountCodeTable.getItems().add(discountCode);
    }

    @FXML
    private void generateRandomCode(ActionEvent event) throws IOException {
        String response = App.getResponseFromServer("generateRandomCode");
        discountCodeTable.getItems().clear();
        for (DiscountCode DC : Database.getInstance().getAllDiscountCodes())
            discountCodeTable.getItems().add(DC);
        new Alert(Alert.AlertType.INFORMATION, "Random discount code generated successfully").showAndWait();
    }
}

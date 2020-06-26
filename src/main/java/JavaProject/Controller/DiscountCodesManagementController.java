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
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

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
        ObservableList<String> buyersList = userList.getSelectionModel().getSelectedItems();
        String dateTimeRegex = "([12]\\d{3})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]) ([0-1]?[0-9]|2[0-3]):[0-5][0-9]";
        if (!code.matches("\\S+")) {
            new Alert(Alert.AlertType.ERROR, "Use non-whitespace letters for code").showAndWait();
        } else if (!startDate.matches(dateTimeRegex)) {
            new Alert(Alert.AlertType.ERROR, "Use format yyyy-mm-dd hh:mm for start date").showAndWait();
        } else if (!endDate.matches(dateTimeRegex)) {
            new Alert(Alert.AlertType.ERROR, "Use format yyyy-mm-dd hh:mm for end date").showAndWait();
        } else if (startDate.compareTo(endDate) >= 0) {
            new Alert(Alert.AlertType.ERROR, "Start date should be before end date").showAndWait();
        } else if (!percent.matches("(\\d+)(\\.\\d+)?")) {
            new Alert(Alert.AlertType.ERROR, "Percent should be DOUBLE value").showAndWait();
        } else if (!maxDiscount.matches("(\\d+)(\\.\\d+)?")) {
            new Alert(Alert.AlertType.ERROR, "Maximum discount should be DOUBLE value").showAndWait();
        } else if (!maxUsage.matches("\\d+")) {
            new Alert(Alert.AlertType.ERROR, "Maximum usage should be integer value").showAndWait();
        } else {
            double discountPercent = Double.parseDouble(percent);
            if (discountPercent > 100 || discountPercent <= 0) {
                new Alert(Alert.AlertType.ERROR, "Percent should be in range (0-100]").showAndWait();
            } else {
                double maximumDiscount = Double.parseDouble(maxDiscount);
                int maxUsageNumber = Integer.parseInt(maxUsage);
                HashMap<String, Integer> hashMap = new HashMap<>();
                DiscountCode discountCode = Database.getInstance().getDiscountCodeByCode(code);
                DiscountCode selected = discountCodeTable.getSelectionModel().getSelectedItem();
                if (selected == null && discountCode != null) {
                    new Alert(Alert.AlertType.ERROR, "Discount code exists").showAndWait();
                } else if (selected == null && discountCode == null) {
                    for (String user : buyersList) {
                        Buyer buyer = (Buyer) Database.getInstance().getAccountByUsername(user);
                        hashMap.put(user, 0);
                        buyer.getDiscountCodes().add(code);
                        Database.getInstance().saveAccount(buyer);
                    }
                    Database.getInstance().saveDiscountCode(new DiscountCode(code, startDate, endDate, discountPercent, maximumDiscount, maxUsageNumber, hashMap));
                    new Alert(Alert.AlertType.INFORMATION, "Discount code added successfully").showAndWait();
                } else if (selected != null && discountCode != null && !selected.getCode().equals(code)) {
                    new Alert(Alert.AlertType.ERROR, "Discount code exists").showAndWait();
                } else {
                    for (String user : selected.getBuyers().keySet()) {
                        Buyer buyer = (Buyer) Database.getInstance().getAccountByUsername(user);
                        if (!buyersList.contains(user)) {
                            buyer.getDiscountCodes().remove(selected.getCode());
                            Database.getInstance().saveAccount(buyer);
                        }
                    }
                    for (String user : buyersList) {
                        Buyer buyer = (Buyer) Database.getInstance().getAccountByUsername(user);
                        hashMap.put(user, selected.getBuyers().getOrDefault(user, 0));
                        buyer.getDiscountCodes().remove(selected.getCode());
                        buyer.getDiscountCodes().add(code);
                        Database.getInstance().saveAccount(buyer);
                    }
                    selected.setCode(code);
                    selected.setStartDate(startDate);
                    selected.setEndDate(endDate);
                    selected.setDiscountPercent(discountPercent);
                    selected.setMaxUsageNumber(maxUsageNumber);
                    selected.setMaxDiscount(maximumDiscount);
                    selected.setBuyers(hashMap);
                    Database.getInstance().saveDiscountCode(selected);
                    new Alert(Alert.AlertType.INFORMATION, "Discount code edited successfully").showAndWait();
                }
                discountCodeTable.getItems().clear();
                for (DiscountCode DC : Database.getInstance().getAllDiscountCodes())
                    discountCodeTable.getItems().add(DC);
            }
        }
    }

    private void selectDiscountCode() {
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
    private void deleteDiscountCode(ActionEvent event) {
        for (DiscountCode discountCode : discountCodeTable.getSelectionModel().getSelectedItems())
            Database.getInstance().deleteDiscountCode(discountCode);
        new Alert(Alert.AlertType.INFORMATION, "Discount code deleted successfully").showAndWait();
        discountCodeTable.getItems().clear();
        for (DiscountCode discountCode : Database.getInstance().getAllDiscountCodes())
            discountCodeTable.getItems().add(discountCode);
    }
}

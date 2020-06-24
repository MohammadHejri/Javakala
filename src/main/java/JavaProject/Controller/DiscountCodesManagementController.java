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
    Label statusLabel;
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
    TableColumn<DiscountCode, HashMap<String, Integer>> buyersColumn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        percentColumn.setCellValueFactory(new PropertyValueFactory<>("discountPercent"));
        maxDiscountColumn.setCellValueFactory(new PropertyValueFactory<>("maxDiscount"));
        maxUsageColumn.setCellValueFactory(new PropertyValueFactory<>("maxUsageNumber"));
        buyersColumn.setCellValueFactory(new PropertyValueFactory<>("buyers"));
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
            statusLabel.setText("Use non-whitespace letters for code");
        } else if (!startDate.matches(dateTimeRegex)) {
            statusLabel.setText("Use format yyyy-mm-dd hh:mm for start date");
        } else if (!endDate.matches(dateTimeRegex)) {
            statusLabel.setText("Use format yyyy-mm-dd hh:mm for end date");
        } else if (startDate.compareTo(endDate) >= 0) {
            statusLabel.setText("Start date should be before end date");
        } else if (!percent.matches("(\\d+)(\\.\\d+)?")) {
            statusLabel.setText("Percent should be double value");
        } else if (!maxDiscount.matches("(\\d+)(\\.\\d+)?")) {
            statusLabel.setText("Maximum discount should be double value");
        } else if (!maxUsage.matches("\\d+")) {
            statusLabel.setText("Maximum usage should be integer value");
        } else {
            double discountPercent = Double.parseDouble(percent);
            if (discountPercent > 100) {
                statusLabel.setText("Percent should be less than 100");
            } else {
                double maximumDiscount = Double.parseDouble(maxDiscount);
                int maxUsageNumber = Integer.parseInt(maxUsage);
                HashMap<String, Integer> hashMap = new HashMap<>();
                DiscountCode discountCode = Database.getInstance().getDiscountCodeByCode(code);
                DiscountCode selected = discountCodeTable.getSelectionModel().getSelectedItem();
                if (selected == null && discountCode != null) {
                    statusLabel.setText("Discount code exists");
                } else if (selected == null && discountCode == null) {
                    for (String user : buyersList) {
                        Buyer buyer = (Buyer) Database.getInstance().getAccountByUsername(user);
                        hashMap.put(user, 0);
                        buyer.getDiscountCodes().add(code);
                        Database.getInstance().saveAccount(buyer);
                    }
                    Database.getInstance().saveDiscountCode(new DiscountCode(code, startDate, endDate, discountPercent, maximumDiscount, maxUsageNumber, hashMap));
                } else if (selected != null && discountCode != null && !selected.getCode().equals(code)) {
                    statusLabel.setText("Discount code exists");
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
                }
                discountCodeTable.getItems().clear();
                for (DiscountCode DC : Database.getInstance().getAllDiscountCodes())
                    discountCodeTable.getItems().add(DC);
            }
        }
    }

    private void selectDiscountCode() {
        DiscountCode discountCode = discountCodeTable.getSelectionModel().getSelectedItem();
        codeField.setText(discountCode.getCode());
        startDateField.setText(discountCode.getStartDate());
        endDateField.setText(discountCode.getEndDate());
        percentField.setText(String.valueOf(discountCode.getDiscountPercent()));
        maxDiscountField.setText(String.valueOf(discountCode.getMaxDiscount()));
        maxUsageField.setText(String.valueOf(discountCode.getMaxUsageNumber()));
    }

    private void deselectDiscountCode() {
        codeField.setText("");
        startDateField.setText("");
        endDateField.setText("");
        percentField.setText("");
        maxDiscountField.setText("");
        maxUsageField.setText("");
    }

    @FXML
    private void deleteDiscountCode(ActionEvent event) {
        for (DiscountCode discountCode : discountCodeTable.getSelectionModel().getSelectedItems())
            Database.getInstance().deleteDiscountCode(discountCode);
        statusLabel.setText("Deleted successfully");
        discountCodeTable.getItems().clear();
        for (DiscountCode discountCode : Database.getInstance().getAllDiscountCodes())
            discountCodeTable.getItems().add(discountCode);
    }
}

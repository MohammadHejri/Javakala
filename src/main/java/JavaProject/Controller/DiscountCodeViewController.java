package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Discount.DiscountCode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DiscountCodeViewController implements Initializable {
    @FXML
    ListView<String> discountCodesList;
    @FXML
    TextField codeField;
    @FXML
    TextField startField;
    @FXML
    TextField endField;
    @FXML
    TextField percentField;
    @FXML
    TextField maxDiscountField;
    @FXML
    TextField remainingField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (String discountCode : ((Buyer) App.getSignedInAccount()).getDiscountCodes())
            discountCodesList.getItems().add(discountCode);
        discountCodesList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) selectDiscountCode();
            else deselectDiscountCode();
        });
    }

    private void selectDiscountCode() {
        String code = discountCodesList.getSelectionModel().getSelectedItem();
        DiscountCode discountCode = Database.getInstance().getDiscountCodeByCode(code);
        codeField.setText(discountCode.getCode());
        startField.setText(discountCode.getStartDate());
        endField.setText(discountCode.getEndDate());
        percentField.setText(String.valueOf(discountCode.getDiscountPercent()));
        maxDiscountField.setText(String.valueOf(discountCode.getMaxDiscount()));
        int remaining = discountCode.getMaxUsageNumber() - discountCode.getBuyers().get(App.getSignedInAccount().getUsername());
        remainingField.setText(String.valueOf(remaining));
    }

    private void deselectDiscountCode() {
        codeField.setText("");
        startField.setText("");
        endField.setText("");
        percentField.setText("");
        maxDiscountField.setText("");
        remainingField.setText("");
    }
}

package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Discount.DiscountCode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PurchasePageDiscountSectionController {

    @FXML TextField codeField;

    @FXML
    private void goToPayment(ActionEvent event) {
        String code = codeField.getText().trim();
        if (code.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Enter code").showAndWait();
        } else if (Database.getInstance().getDiscountCodeByCode(code) == null) {
            new Alert(Alert.AlertType.ERROR, "Code not found").showAndWait();
        } else if (!((Buyer) App.getSignedInAccount()).getDiscountCodes().contains(code)) {
            new Alert(Alert.AlertType.ERROR, "Code doesnt belong to you").showAndWait();
        } else {
            try {
                DiscountCode discountCode = Database.getInstance().getDiscountCodeByCode(code);
                Date nowDate = new Date();
                Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(discountCode.getStartDate());
                Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(discountCode.getEndDate());
                boolean afterStart = nowDate.compareTo(startDate) > 0;
                boolean beforeEnd = nowDate.compareTo(endDate) < 0;
                if (discountCode.getBuyers().get(App.getSignedInAccount().getUsername()) >= discountCode.getMaxUsageNumber()) {
                    new Alert(Alert.AlertType.ERROR, "You can not use this code any more").showAndWait();
                } else if (!afterStart) {
                    new Alert(Alert.AlertType.ERROR, "For using this code, come back in " + discountCode.getStartDate()).showAndWait();
                } else if (!beforeEnd) {
                    new Alert(Alert.AlertType.ERROR, "Code has been expired :(").showAndWait();
                } else {
                    App.getCart().setCode(discountCode);
                    new Alert(Alert.AlertType.INFORMATION, "Code accepted").showAndWait();
                    App.setRoot("paymentWay");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void skipToPayment() throws IOException {
        App.setRoot("paymentWay");
    }

    @FXML
    private void goToReceiverInfo(MouseEvent mouseEvent) throws IOException {
        App.getCart().setCode(null);
        App.setRoot("receiverInfo");
    }
}

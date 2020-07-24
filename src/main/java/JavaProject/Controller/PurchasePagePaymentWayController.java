package JavaProject.Controller;

import JavaProject.App;
import JavaProject.Model.Account.Account;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Discount.Auction;
import JavaProject.Model.Discount.DiscountCode;
import JavaProject.Model.Log.BuyLog;
import JavaProject.Model.Log.ProductOnLog;
import JavaProject.Model.Log.SellLog;
import JavaProject.Model.ProductOrganization.Cart;
import JavaProject.Model.ProductOrganization.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

public class PurchasePagePaymentWayController implements Initializable {

    @FXML Label totalPriceLabel;
    @FXML Label discountLabel;
    @FXML Label toBePaidLabel;
    @FXML Label balanceLabel;
    @FXML Button balanceButton;
    @FXML Button increaseButton;

    double totalPrice;
    double discount;
    double toBePaid;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        totalPrice = App.getCart().getPrice();
        if (App.getCart().getCode() != null) {
            discount = Math.min(App.getCart().getCode().getMaxDiscount(), totalPrice * App.getCart().getCode().getDiscountPercent() / 100);
        }
        toBePaid = totalPrice - discount;
        totalPriceLabel.setText("Total price : $" + totalPrice);
        discountLabel.setText("Discount : $" + discount);
        toBePaidLabel.setText("To pay : $" + toBePaid);
        balanceLabel.setText("Your balance : $" + ((Buyer)App.getSignedInAccount()).getBalance());
        if (toBePaid > ((Buyer)App.getSignedInAccount()).getBalance()) {
            balanceButton.setDisable(true);
        }
        increaseButton.setText("Pay with bank account");
    }

    public void payWithBalance(ActionEvent event) throws IOException {
        Buyer buyer = (Buyer) App.getSignedInAccount();
        // buyer.setBalance(buyer.getBalance() - toBePaid);
        DiscountCode discountCode = App.getCart().getCode();
//        if (discountCode != null) {
//            discountCode.getBuyers().replace(buyer.getUsername(), discountCode.getBuyers().get(buyer.getUsername()) + 1);
//            Database.getInstance().saveDiscountCode(discountCode);
//        }
        App.getResponseFromServer("handleBuyerInPurchase", buyer.getUsername(), String.valueOf(toBePaid), discountCode == null ? "NOTHING" : discountCode.getCode());

        // buyer part
        double buyerTotalPaidAmount = 0, buyerTotalDecreasedAmount = 0;
        ArrayList<ProductOnLog> buyerProductOnLogs = new ArrayList<>();
        for (Product product : App.getCart().getProducts().keySet()) {
            Seller seller = (Seller) Database.getInstance().getAccountByUsername(product.getSellerUsername());
            Auction auction = Database.getInstance().getCurrentAuction(product);
            int quantity = App.getCart().getProducts().get(product);
            double paidAmount = quantity * product.getPrice() * (auction == null ? 100 : 100 - auction.getDiscountPercent()) / 100;
            double decreasedAmount = quantity * product.getPrice() * (auction == null ? 0 : auction.getDiscountPercent()) / 100;
            buyerTotalPaidAmount += paidAmount;
            buyerTotalDecreasedAmount += decreasedAmount;
            ProductOnLog productOnLog = new ProductOnLog(product.getName(), auction == null ? null : auction.getID(), null, seller.getUsername(), quantity, paidAmount, decreasedAmount);
            productOnLog.setFileName(App.getResponseFromServer("getProductFileName", product.getName()));
            buyerProductOnLogs.add(productOnLog);
            App.getResponseFromServer("updateProductsInPurchase", product.getName(), String.valueOf(quantity), buyer.getUsername());
            App.getFileData(product.getName(), "productFile");
        }
        if (discountCode != null) {
            double extraDiscount = Math.min(discountCode.getMaxDiscount(), buyerTotalPaidAmount * discountCode.getDiscountPercent() / 100);
            buyerTotalPaidAmount -= extraDiscount;
            buyerTotalDecreasedAmount += extraDiscount;
        }
        BuyLog buyLog = new BuyLog(buyerTotalPaidAmount, buyerTotalDecreasedAmount, buyerProductOnLogs);
        buyLog.setAddress(PurchasePageReceiverInfoController.address);
        App.getResponseFromServer("handleBuyLog", App.objectToString(buyLog), buyer.getUsername());

        // seller part
        ArrayList<String> handledSellers = new ArrayList<>();
        for (Product pro : App.getCart().getProducts().keySet()) {
            Seller seller = (Seller) Database.getInstance().getAccountByUsername(pro.getSellerUsername());
            if (handledSellers.contains(seller.getUsername())) continue;
            handledSellers.add(seller.getUsername());
            double totalGainedAmount = 0, totalDecreasedAmount = 0;
            ArrayList<ProductOnLog> productOnLogs = new ArrayList<>();
            for (Product product : App.getCart().getProducts().keySet()) {
                if (!seller.getUsername().equals(product.getSellerUsername())) continue;
                Auction auction = Database.getInstance().getCurrentAuction(product);
                int quantity = App.getCart().getProducts().get(product);
                double gainedAmount = quantity * product.getPrice() * (auction == null ? 100 : 100 - auction.getDiscountPercent()) / 100;
                double decreasedAmount = quantity * product.getPrice() * (auction == null ? 0 : auction.getDiscountPercent()) / 100;
                totalGainedAmount += gainedAmount;
                totalDecreasedAmount += decreasedAmount;
                ProductOnLog productOnLog = new ProductOnLog(product.getName(), auction == null ? null : auction.getID(), null, seller.getUsername(), quantity, gainedAmount, decreasedAmount);
                productOnLogs.add(productOnLog);
            }
//            seller.setBalance(seller.getBalance() + totalGainedAmount);
            SellLog sellLog = new SellLog(buyer.getUsername(), totalGainedAmount, totalDecreasedAmount, productOnLogs);
//            seller.getSellLogsID().add(sellLog.getID());
//            Database.getInstance().saveSellLog(sellLog);
//            Database.getInstance().saveAccount(seller);
            App.getResponseFromServer("handleSellerInPurchase", seller.getUsername(), String.valueOf(totalGainedAmount), App.objectToString(sellLog));
        }
        App.setCart(new Cart());
        if (buyerTotalPaidAmount > 100)
            giveDiscountCodeForGoodPurchases();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Purchase successful!\nDo you wish to view sale history?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.NO) {
            App.setRoot(App.mainPage);
        } else {
            App.setRoot("buyerProfile");
            BuyerProfileController.prevPane = App.mainPage;
        }
    }

    private void giveDiscountCodeForGoodPurchases() throws IOException {
        Date nowDate = new Date();
        DiscountCode discountCode;
        String code = "Gift" + new SimpleDateFormat("mmssSSS").format(nowDate);
        String startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(nowDate);
        long time = 24 * 60 * 60 * 1000 * 7;
        String endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(nowDate.getTime() + time));
        HashMap<String, Integer> hashMap = new HashMap<>();
        hashMap.put(App.getSignedInAccount().getUsername(), 0);
        discountCode = new DiscountCode(code, startDate, endDate, Math.random() * 100, 100, 1, hashMap);
        App.getResponseFromServer("saveGiftCode", App.objectToString(discountCode), App.getSignedInAccount().getUsername());
        new Alert(Alert.AlertType.INFORMATION, "Gift discount code : " + code).showAndWait();
    }

    public void increaseBalance(ActionEvent event) throws IOException {
        App.setRoot("payment");
        // PurchasePageCardPaymentController.amount = toBePaid - ((Buyer)App.getSignedInAccount()).getBalance();
        PurchasePageCardPaymentController.amount = toBePaid;
    }

    public void goToDiscountSection(MouseEvent mouseEvent) throws IOException {
        App.setRoot("discountCodeValidation");
    }
}

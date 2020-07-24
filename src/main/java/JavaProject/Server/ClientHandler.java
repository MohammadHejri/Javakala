package JavaProject.Server;

import JavaProject.App;
import JavaProject.Controller.BuyerProfileController;
import JavaProject.Model.Account.*;
import JavaProject.Model.Chat.Conversation;
import JavaProject.Model.Chat.Message;
import JavaProject.Model.Discount.Auction;
import JavaProject.Model.Discount.DiscountCode;
import JavaProject.Model.Log.BuyLog;
import JavaProject.Model.Log.ProductOnLog;
import JavaProject.Model.Log.SellLog;
import JavaProject.Model.ProductOrganization.*;
import JavaProject.Model.Request.Request;
import JavaProject.Model.Request.Subject;
import JavaProject.Model.Status.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClientHandler extends Thread {

    private static ArrayList<String> allActiveSessionKeys = new ArrayList<>();
    public static HashMap<ClientHandler, String> allConnectedClients = new HashMap<>();
    public static HashMap<ClientHandler, String> allTokens = new HashMap<>();
    private static final String uploadedFilesPath = "src/main/server/Files";
    private Socket clientSocket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public ClientHandler(Socket clientSocket, DataOutputStream dataOutputStream, DataInputStream dataInputStream, String token) {
        this.clientSocket = clientSocket;
        this.dataOutputStream = dataOutputStream;
        this.dataInputStream = dataInputStream;
        try {
            System.out.println("Token: " + token);
            dataInputStream.readUTF();
            respondToClient(token);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        allTokens.put(this, token);
        allConnectedClients.put(this, null);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = "";
                while (true) {
                    String messagePart = dataInputStream.readUTF();
                    if (messagePart.equals("END_OF_MESSAGE"))
                        break;
                    message += messagePart;
                    respondToClient("Got it");
                }
                System.out.println("==================================================");
                System.out.println(message);
                System.out.println("==================================================");

                String[] mParts = message.split("###");
                String[] messageParts = new String[mParts.length - 1];
                for (int i = 0; i < mParts.length - 1; i++)
                    messageParts[i] = mParts[i];
                String session = mParts[mParts.length - 1];
                if (allActiveSessionKeys.contains(session)) {
                    allActiveSessionKeys.remove(session);
                } else {
                    respondToClient("You have used this session key");
                    continue;
                }
                String token = messageParts[messageParts.length - 1];
                if (!allTokens.get(this).equals(token)) {
                    respondToClient("Invalid token");
                    continue;
                }
                if (message.startsWith("managerExists")) {
                    respondToClient(Database.getInstance().managerExists() ? "true" : "false");
                } else if (message.startsWith("createManager")) {
                    Account account = stringToObject(messageParts[1], Manager.class);
                    respondToClient(getSignUpResult(account));
                } else if (message.startsWith("createSeller")) {
                    Account account = stringToObject(messageParts[1], Seller.class);
                    respondToClient(getSignUpResult(account));
                } else if (message.startsWith("createBuyer")) {
                    Account account = stringToObject(messageParts[1], Buyer.class);
                    respondToClient(getSignUpResult(account));
                } else if (message.startsWith("createSupporter")) {
                    Account account = stringToObject(messageParts[1], Supporter.class);
                    respondToClient(getSignUpResult(account));
                } else if (message.startsWith("signIn")) {
                    respondToClient(getSignInResult(messageParts[1], messageParts[2]));
                } else if (message.startsWith("signOut")) {
                    respondToClient(getSignOutResult(messageParts[1]));
                } else if (message.startsWith("updateManagerInfo")) {
                    Account changedAccount = stringToObject(messageParts[3], Manager.class);
                    respondToClient(getUpdateAccountInfoResult(messageParts[1], messageParts[2], changedAccount));
                } else if (message.startsWith("updateSellerInfo")) {
                    Account changedAccount = stringToObject(messageParts[3], Seller.class);
                    respondToClient(getUpdateAccountInfoResult(messageParts[1], messageParts[2], changedAccount));
                } else if (message.startsWith("updateBuyerInfo")) {
                    Account changedAccount = stringToObject(messageParts[3], Buyer.class);
                    respondToClient(getUpdateAccountInfoResult(messageParts[1], messageParts[2], changedAccount));
                } else if (message.startsWith("updateSupporterInfo")) {
                    Account changedAccount = stringToObject(messageParts[3], Supporter.class);
                    respondToClient(getUpdateAccountInfoResult(messageParts[1], messageParts[2], changedAccount));
                } else if (message.startsWith("getAllAccounts")) {
                    respondToClient(getAllAccountsAsString());
                } else if (message.startsWith("deleteAccount")) {
                    respondToClient(getDeleteAccountResult(messageParts[1], messageParts[2]));
                } else if (message.startsWith("getAllRequests")) {
                    respondToClient(getAllRequestsAsString());
                } else if (message.startsWith("acceptRequest")) {
                    respondToClient(getAcceptRequestResult(messageParts[1]));
                } else if (message.startsWith("declineRequest")) {
                    respondToClient(getDeclineRequestResult(messageParts[1]));
                } else if (message.startsWith("getAllDiscountCodes")) {
                    respondToClient(getAllDiscountCodesAsString());
                } else if (message.startsWith("createOrEditDiscountCode")) {
                    respondToClient(getCreateOrEditDiscountCodeResult(message));
                } else if (message.startsWith("deleteDiscountCode")) {
                    respondToClient(getDeleteDiscountCodeResult(messageParts[1]));
                } else if (message.startsWith("generateRandomCode")) {
                    respondToClient(getGenerateRandomCodeResult());
                } else if (message.startsWith("getCategoryByName")) {
                    respondToClient(getCategoryByNameAsString(messageParts[1]));
                } else if (message.startsWith("createOrEditCategory")) {
                    respondToClient(getCreateOrEditCategoryResult(message));
                } else if (message.startsWith("deleteCategory")) {
                    respondToClient(getDeleteCategory(messageParts[1]));
                } else if (message.startsWith("getAllProductsFromCategory")) {
                    respondToClient(getAllProductsFromCategoryAsString(messageParts[1]));
                } else if (message.startsWith("deleteProduct")) {
                    respondToClient(getDeleteProductResult(messageParts[1]));
                } else if (message.startsWith("getProductByName")) {
                    respondToClient(getProductByNameAsString(messageParts[1]));
                } else if (message.startsWith("getProductByID")) {
                    respondToClient(getProductByIDAsString(messageParts[1]));
                } else if (message.startsWith("requestAddProduct")) {
                    respondToClient(getRequestAddProductResult(message));
                } else if (message.startsWith("requestEditProduct")) {
                    respondToClient(getRequestEditProductResult(message));
                } else if (message.startsWith("requestDeleteProduct")) {
                    respondToClient(getRequestDeleteProductResult(messageParts[1]));
                } else if (message.startsWith("requestAddOrEditAuction")) {
                    respondToClient(getRequestAddOrEditAuctionResult(message));
                } else if (message.startsWith("requestDeleteAuction")) {
                    respondToClient(getRequestDeleteAuctionResult(messageParts[1]));
                } else if (message.startsWith("getAuctionByID")) {
                    respondToClient(getAuctionByIDAsString(messageParts[1]));
                } else if (message.startsWith("rateProduct")) {
                    respondToClient(getRateProductResult(messageParts[1], messageParts[2], messageParts[3]));
                } else if (message.startsWith("uploadFile")) {
                    respondToClient(getUploadFileResult(messageParts[1]));
                } else if (message.startsWith("getFileData")) {
                    getFileData(messageParts[1], messageParts[2]);
                } else if (message.startsWith("increaseView")) {
                    respondToClient(getIncreaseViewResult(messageParts[1]));
                } else if (message.startsWith("leaveComment")) {
                    respondToClient(getLeaveCommentResult(messageParts[1], messageParts[2]));
                } else if (message.startsWith("isOnlineUser")) {
                    respondToClient(getOnlineUserCheckResult(messageParts[1]));
                } else if (message.startsWith("saveMessageAndNotify")) {
                    respondToClient(getSaveMessageAndNotifyResult(messageParts[1]));
                } else if (message.startsWith("getConversation")) {
                    respondToClient(getConversationAsString(messageParts[1], messageParts[2]));
                } else if (message.startsWith("hadConversation")) {
                    respondToClient(getHadConversationResult(messageParts[1], messageParts[2]));
                } else if (message.startsWith("updateShopCommission")) {
                    respondToClient(getUpdateShopCommissionResult(messageParts[1]));
                } else if (message.startsWith("updateMinBalance")) {
                    respondToClient(getUpdateMinBalanceResult(messageParts[1]));
                } else if (message.startsWith("getShopProperties")) {
                    respondToClient(getShopPropertiesAsString());
                } else if (message.startsWith("saveGiftCode")) {
                    respondToClient(getGiftCodeResult(messageParts[1], messageParts[2]));
                } else if (message.startsWith("updateWallet")) {
                    respondToClient(getUpdateWalletResult(messageParts[1], messageParts[2]));
                } else if (message.startsWith("chargeWalletSeller")) {
                    respondToClient(getChargeWalletResult(messageParts[1], messageParts[2]));
                } else if (message.startsWith("putInBankSeller")) {
                    respondToClient(getPutInBankSellerResult(messageParts[1], messageParts[2]));
                } else if (message.startsWith("getMinBalance")) {
                    respondToClient(getMinBalanceAsString());
                } else if (message.startsWith("updateProductsInPurchase")) {
                    respondToClient(getUpdateProductsInPurchase(messageParts[1], messageParts[2], messageParts[3]));
                } else if (message.startsWith("handleBuyLog")) {
                    respondToClient(getHandleBuyLogResult(messageParts[1], messageParts[2]));
                } else if (message.startsWith("handleBuyerInPurchase")) {
                    respondToClient(getHandleBuyerInPurchase(messageParts[1], messageParts[2], messageParts[3]));
                } else if (message.startsWith("handleSellerInPurchase")) {
                    respondToClient(getHandleSellerInPurchase(messageParts[1], messageParts[2], messageParts[3]));
                } else if (message.startsWith("getAllBuyLogs")) {
                    respondToClient(getAllBuyLogsAsString());
                } else if (message.startsWith("getAllSellLogs")) {
                    respondToClient(getAllSellLogsAsString());
                } else if (message.startsWith("deliverProducts")) {
                    respondToClient(getDeliverProductsResult(messageParts[1]));
                } else if (message.startsWith("getProductFileName")) {
                    respondToClient(getProductFileName(messageParts[1]));
                } else {
                    respondToClient(message);
                }
            } catch (SocketException se) {
                allConnectedClients.remove(this);
                System.out.println("socket disconnected");
                break;
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    private String getProductFileName(String productName) {
        File file = null;
        for (File fileInFolder : new File(uploadedFilesPath + "\\productFile").listFiles()) {
            String name = fileInFolder.getName();
            if (productName.equals(name.substring(0, name.lastIndexOf("."))))
                file = fileInFolder;
        }
        return file == null ? "-" : file.getName();
    }

    private String getDeliverProductsResult(String buyLogID) throws IOException, SQLException {
        BuyLog buyLog = Database.getInstance().getBuyLogByID(buyLogID);
        buyLog.setStatus("Delivered");
        Database.getInstance().saveBuyLog(buyLog);
        return "Success";
    }

    private String getAllSellLogsAsString() {
        ArrayList<SellLog> sellLogs = Database.getInstance().getAllSellLogs();
        String result = "Success";
        for (SellLog sellLog : sellLogs) {
            result += "###" + objectToString(sellLog);
        }
        return result;
    }

    private String getAllBuyLogsAsString() {
        ArrayList<BuyLog> buyLogs = Database.getInstance().getAllBuyLogs();
        String result = "Success";
        for (BuyLog buyLog : buyLogs) {
            result += "###" + objectToString(buyLog);
        }
        return result;
    }

    private String getHandleSellerInPurchase(String username, String amountStr, String selllogAsString) throws IOException, SQLException {
        double moneyForSeller = Double.parseDouble(amountStr) * (100 - Double.parseDouble(Database.getInstance().shopProperties.getKey())) / 100;
        Seller seller = (Seller) Database.getInstance().getAccountByUsername(username);
        seller.setBalance(seller.getBalance() + moneyForSeller);
        SellLog sellLog = stringToObject(selllogAsString, SellLog.class);
        seller.getSellLogsID().add(sellLog.getID());
        Database.getInstance().saveSellLog(sellLog);
        Database.getInstance().saveAccount(seller);
        return "Success";
    }

    private String getHandleBuyerInPurchase(String username, String toBePaidStr, String code) throws IOException, SQLException {
        Buyer buyer = (Buyer) Database.getInstance().getAccountByUsername(username);
        buyer.setBalance(buyer.getBalance() - Double.parseDouble(toBePaidStr));
        DiscountCode discountCode = Database.getInstance().getDiscountCodeByCode(code);
        if (discountCode != null) {
            discountCode.getBuyers().replace(buyer.getUsername(), discountCode.getBuyers().get(buyer.getUsername()) + 1);
            Database.getInstance().saveDiscountCode(discountCode);
        }
        return "Success";
    }

    private String getHandleBuyLogResult(String buylogAsStr, String username) throws IOException, SQLException {
        Buyer buyer = (Buyer) Database.getInstance().getAccountByUsername(username);
        BuyLog buyLog = stringToObject(buylogAsStr, BuyLog.class);
        buyer.getBuyLogsID().add(buyLog.getID());
        Database.getInstance().saveBuyLog(buyLog);
        Database.getInstance().saveAccount(buyer);
        return "Success";
    }

    private String getUpdateProductsInPurchase(String productName, String quantityStr, String username) throws IOException, SQLException {
        Product product = Database.getInstance().getProductByName(productName);
        int quantity = Integer.parseInt(quantityStr);
        Buyer buyer = (Buyer) Database.getInstance().getAccountByUsername(username);
        product.setRemainingItems(product.getRemainingItems() - quantity);
        if (!product.getBuyers().contains(buyer.getUsername()))
            product.getBuyers().add(buyer.getUsername());
        Database.getInstance().updateCategories();
        return "Success";
    }

    private String getMinBalanceAsString() {
        return Database.getInstance().shopProperties.getValue();
    }

    private String getChargeWalletResult(String username, String amountStr) throws IOException, SQLException {
        Account account = Database.getInstance().getAccountByUsername(username);
        double amount = Double.parseDouble(amountStr);

        String token = Server.requestGettingToken(account);
        String receiptID = Server.requestGettingReceipt(token, "move", String.valueOf((int) amount), String.valueOf(account.getBankAccountNumber()), String.valueOf(Server.shopAccountNumber), "ChargingWallet");
        String response = Server.requestPayingReceipt(receiptID);
        if (response.startsWith("done")) {
            ((Seller) account).setBalance(((Seller) account).getBalance() + amount);
            Database.getInstance().saveAccount(account);
        }
        return response;
    }

    private String getPutInBankSellerResult(String username, String amountStr) throws IOException, SQLException {
        Account account = Database.getInstance().getAccountByUsername(username);
        double amount = Double.parseDouble(amountStr);

        String token = Server.requestGettingToken(account);
        String receiptID = Server.requestGettingReceipt(token, "move", String.valueOf((int) amount), String.valueOf(Server.shopAccountNumber), String.valueOf(account.getBankAccountNumber()), "ChargingWallet");
        String response = Server.requestPayingReceipt(receiptID);
        if (response.startsWith("done")) {
            ((Seller) account).setBalance(((Seller) account).getBalance() - amount);
            Database.getInstance().saveAccount(account);
        }
        return response;
    }

    private String getUpdateWalletResult(String username, String amountStr) throws IOException, SQLException {
        Account account = Database.getInstance().getAccountByUsername(username);
        double amount = Double.parseDouble(amountStr);

        String token = Server.requestGettingToken(account);
        String receiptID = Server.requestGettingReceipt(token, "deposit", String.valueOf((int) amount), "-1", String.valueOf(Server.shopAccountNumber), "ChargingWallet");
        String response = Server.requestPayingReceipt(receiptID);
        if (response.startsWith("done")) {
            ((Buyer) account).setBalance(((Buyer) account).getBalance() + amount);
            Database.getInstance().saveAccount(account);
        }
        return response;
    }

    private String getGiftCodeResult(String discountCodeAsString, String username) throws IOException, SQLException {
        DiscountCode discountCode = stringToObject(discountCodeAsString, DiscountCode.class);
        Account account = Database.getInstance().getAccountByUsername(username);
        ((Buyer)account).getDiscountCodes().add(discountCode.getCode());
        Database.getInstance().saveDiscountCode(discountCode);
        Database.getInstance().saveAccount(account);
        return "Success";
    }

    private String getShopPropertiesAsString() {
        return objectToString(Database.getInstance().shopProperties);
    }

    private String getUpdateShopCommissionResult(String shopCommissionStr) {
        try {
            Double shopCommission = Double.parseDouble(shopCommissionStr);
            if (shopCommission > 100 || shopCommission < 0)
                return "Enter [0-100]";
            Database.getInstance().shopProperties.setKey(shopCommissionStr);
            Database.getInstance().saveShopProperties();
            return "Success";
        } catch (Exception e) {
            return "Enter DOUBLE for shop commission";
        }
    }

    private String getUpdateMinBalanceResult(String minBalanceStr) {
        try {
            Double minBalance = Double.parseDouble(minBalanceStr);
            if (minBalance < 0)
                return "Enter positive value";
            Database.getInstance().shopProperties.setValue(minBalanceStr);
            Database.getInstance().saveShopProperties();
            return "Success";
        } catch (Exception e) {
            return "Enter DOUBLE for minimum balance";
        }
    }

    private String getHadConversationResult(String firstSide, String secondSide) {
        Conversation conversation = Database.getInstance().getConversationByBothSides(firstSide, secondSide);
        if (conversation == null)
            return "False";
        return "True";
    }

    private String getConversationAsString(String firstSide, String secondSide) throws IOException, SQLException {
        Conversation conversation = Database.getInstance().getConversationByBothSides(firstSide, secondSide);
        if (conversation == null) {
            conversation = new Conversation(firstSide, secondSide);
            Database.getInstance().saveConversation(conversation);
        }
        return objectToString(conversation);
    }

    private String getSaveMessageAndNotifyResult(String messageAsString) throws IOException, SQLException {
        Message message = stringToObject(messageAsString, Message.class);
        Conversation conversation = Database.getInstance().getConversationByBothSides(message.getSenderUsername(), message.getRecieverUsername());
        if (conversation == null)
            conversation = new Conversation(message.getSenderUsername(), message.getRecieverUsername());
        conversation.getMesaages().add(message);
        Database.getInstance().saveConversation(conversation);
        return "Success";
    }

    private String getOnlineUserCheckResult(String username) {
        if (allConnectedClients.containsValue(username))
            return "True";
        return "False";
    }

    private String getLeaveCommentResult(String productName, String commentAsString) throws IOException, SQLException {
        Product product = Database.getInstance().getProductByName(productName);
        product.getComments().add(stringToObject(commentAsString, Comment.class));
        Database.getInstance().updateCategories();
        System.out.println("dvsdvsdvsdvsdvsv");
        return "Success";
    }

    private String getIncreaseViewResult(String productName) throws IOException, SQLException {
        Product product = Database.getInstance().getProductByName(productName);
        product.setViews(product.getViews() + 1);
        Database.getInstance().updateCategories();
        return "Success";
    }

    private void getFileData(String name, String type) throws IOException, SQLException {
        File file = null;
        for (File fileInFolder : new File(uploadedFilesPath + "\\" + type).listFiles()) {
            if (fileInFolder.getName().substring(0, fileInFolder.getName().lastIndexOf(".")).equals(name))
                file = fileInFolder;
        }
        System.out.println(file == null ? "File not found" : file.getName());
        dataOutputStream.writeUTF(file == null ? "File not found" : file.getName());
        dataOutputStream.flush();
        if (file != null) {
            byte[] array = Files.readAllBytes(Paths.get(file.getPath()));
            dataOutputStream.writeInt(array.length);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[32 * 1024 * 1024];
            int len;
            while ((len = fileInputStream.read(buffer)) > 0)
                dataOutputStream.write(buffer, 0, len);
            dataOutputStream.flush();
            fileInputStream.close();
        }
    }

    private String getUploadFileResult(String name) throws IOException, SQLException {
        new File(uploadedFilesPath).mkdirs();
        new File(uploadedFilesPath + "\\accountPhoto").mkdirs();
        new File(uploadedFilesPath + "\\productPhoto").mkdirs();
        new File(uploadedFilesPath + "\\productFile").mkdirs();
        int numberOfBytes = dataInputStream.readInt();
        FileOutputStream fileOutputStream = new FileOutputStream(uploadedFilesPath + "\\" + name);
        byte[] buffer = new byte[32 * 1024 * 1024];
        int len;
        while (numberOfBytes > 0 && (len = dataInputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, len);
            numberOfBytes -= len;
        }
        fileOutputStream.close();
        return "Success";
    }

    private String getRateProductResult(String productName, String productMark, String username) throws IOException, SQLException {
        if (productName.isBlank()) {
            return  "Enter product name";
        } else if (productMark.isBlank()) {
            return "Enter product mark";
        } else if (Database.getInstance().getProductByName(productName) == null) {
            return "Product not found";
        } else if (!productMark.matches("(\\d+)(\\.\\d+)?")) {
            return "Use DOUBLE for mark";
        } else {
            Product product = Database.getInstance().getProductByName(productName);
            double mark = Double.parseDouble(productMark);
            if (mark > 5 || mark < 0) {
                return "Mark should be in range [0-5]";
            }
            if (!product.getBuyers().contains(username)) {
                return "You have not purchased this product to rate it";
            }
            for (Rate rate : product.getRates()) {
                if (rate.getBuyerName().equals(username)) {
                    return "You have rated this product before";
                }
            }
            Rate rate = new Rate(username, mark);
            product.getRates().add(rate);
            product.updateMark();
            Database.getInstance().updateCategories();
            return "Success";
        }
    }

    private String getAuctionByIDAsString(String ID) {
        Auction auction = Database.getInstance().getAuctionByID(ID);
        if (auction == null)
            return "Not found";
        return "Success###" + objectToString(auction);
    }

    private String getRequestAddOrEditAuctionResult(String data) throws IOException, SQLException {
        String[] dataParts = data.split("###");
        String ID = dataParts[1].equals("null") ? null : dataParts[1];
        String startDate = dataParts[2];
        String endDate = dataParts[3];
        String percentStr = dataParts[4];
        String sellerUsername = dataParts[5];
        ArrayList<String> auctionProductsName = stringToObject(dataParts[6], ArrayList.class);
        Auction selectedAuction = ID == null ? null : Database.getInstance().getAuctionByID(ID);
        String dateTimeRegex = "([12]\\d{3})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]) ([0-1]?[0-9]|2[0-3]):[0-5][0-9]";
        if (startDate.isBlank()) {
            return "Enter start date";
        } else if (endDate.isBlank()) {
            return "Enter end date";
        } else if (percentStr.isBlank()) {
            return "Enter discount percent";
        } else if (!startDate.matches(dateTimeRegex)) {
            return "Use format yyyy-mm-dd hh:mm for start date";
        } else if (!endDate.matches(dateTimeRegex)) {
            return "Use format yyyy-mm-dd hh:mm for end date";
        } else if (startDate.compareTo(endDate) >= 0) {
            return "Start date should be before end date";
        } else if (!percentStr.matches("(\\d+)(\\.\\d+)?")) {
            return "Use DOUBLE for price";
        } else {
            double discountPercent = Double.parseDouble(percentStr);
            if (discountPercent > 100 || discountPercent <= 0) {
                return "Percent should be in range (0-100]";
            } else {
                ArrayList<String> productsID = new ArrayList<>();
                for (String productName : auctionProductsName)
                    productsID.add(Database.getInstance().getProductByName(productName).getID());
                Auction auction = new Auction(sellerUsername, startDate, endDate, discountPercent, productsID);
                Request request = null;
                if (selectedAuction == null) {
                    request = new Request(Subject.ADD_AUCTION, auction.toString());
                } else {
                    auction.setID(selectedAuction.getID());
                    request = new Request(Subject.EDIT_AUCTION, "Edited " + auction.toString() + "\n" + "Current " + selectedAuction.toString());
                }
                request.setAuction(auction);
                request.setRequester(sellerUsername);
                Database.getInstance().saveRequest(request);
                return "Success";
            }
        }
    }

    private String getRequestDeleteAuctionResult(String ID) throws IOException, SQLException {
        Auction auction = Database.getInstance().getAuctionByID(ID);
        Request request = new Request(Subject.DELETE_AUCTION, auction.toString());
        request.setAuction(auction);
        request.setRequester(auction.getSellerUsername());
        Database.getInstance().saveRequest(request);
        return "Success";
    }

    private String getRequestAddProductResult(String data) throws IOException, SQLException {
        String[] dataParts = data.split("###");
        String name = dataParts[1];
        String brand = dataParts[2];
        String priceStr = dataParts[3];
        String remainingStr = dataParts[4];
        String description = dataParts[5];
        String imagePath = dataParts[6];
        String sellerUsername = dataParts[7];
        String selectedCategoryName = dataParts[8];
        HashMap<String, String> specs = stringToObject(dataParts[9], HashMap.class);

        Seller seller = (Seller) Database.getInstance().getAccountByUsername(sellerUsername);
        Category selectedCategory = Database.getInstance().getCategoryByName(selectedCategoryName);
        Product product = Database.getInstance().getProductByName(name);

        if (name.isBlank()) {
            return "Enter name";
        } else if (brand.isBlank()) {
            return "Enter brand";
        } else if (priceStr.isBlank()) {
            return "Enter price";
        } else if (remainingStr.isBlank()) {
            return "Enter quantity";
        } else if (description.isBlank()) {
            return "Enter description";
        } else if (!priceStr.matches("(\\d+)(\\.\\d+)?")) {
            return "Use DOUBLE for price";
        } else if (!remainingStr.matches("(\\d+)")) {
            return "Use INTEGER for quantity";
        } else if (product != null) {
            return "Product name not available";
        } else {
            double price = Double.parseDouble(priceStr);
            int remainingItems = Integer.parseInt(remainingStr);
            product = new Product(name, brand, price, seller.getUsername(), remainingItems, selectedCategory.getName(), description, new HashMap<>(specs), imagePath);
            selectedCategory.getProducts().add(product);
            seller.getProductsID().add(product.getID());
            Database.getInstance().updateCategories();
            Database.getInstance().saveAccount(seller);
            Request request = new Request(Subject.ADD_PRODUCT, product.toString());
            request.setProduct(product);
            request.setRequester(sellerUsername);
            Database.getInstance().saveRequest(request);
            return "Success";
        }
    }

    private String getRequestEditProductResult(String data) throws IOException, SQLException {
        String[] dataParts = data.split("###");
        String name = dataParts[1];
        String brand = dataParts[2];
        String priceStr = dataParts[3];
        String remainingStr = dataParts[4];
        String description = dataParts[5];
        String imagePath = dataParts[6];
        String sellerUsername = dataParts[7];
        String selectedCategoryName = dataParts[8];
        HashMap<String, String> specs = stringToObject(dataParts[9], HashMap.class);
        String prevName = dataParts[10];

        Seller seller = (Seller) Database.getInstance().getAccountByUsername(sellerUsername);
        Category selectedCategory = Database.getInstance().getCategoryByName(selectedCategoryName);
        Product selectedProduct = Database.getInstance().getProductByName(prevName);

        if (name.isBlank()) {
            return "Enter name";
        } else if (brand.isBlank()) {
            return "Enter brand";
        } else if (priceStr.isBlank()) {
            return "Enter price";
        } else if (remainingStr.isBlank()) {
            return "Enter quantity";
        } else if (description.isBlank()) {
            return "Enter description";
        } else if (!priceStr.matches("(\\d+)(\\.\\d+)?")) {
            return "Use DOUBLE for price";
        } else if (!remainingStr.matches("(\\d+)")) {
            return "Use INTEGER for quantity";
        } else if (!selectedProduct.getName().equals(name) && Database.getInstance().getProductByName(name) != null) {
            return "Product name not available";
        } else {
            double price = Double.parseDouble(priceStr);
            int remainingItems = Integer.parseInt(remainingStr);
            Product product = new Product(name, brand, price, seller.getUsername(), remainingItems, selectedCategory.getName(), description, new HashMap<>(specs), imagePath);
            product.setID(selectedProduct.getID());
            product.setViews(selectedProduct.getViews());
            product.setDate(selectedProduct.getDate());
            product.setAverageMark(selectedProduct.getAverageMark());
            product.setStatus(selectedProduct.getStatus());
            product.setRates(selectedProduct.getRates());
            product.setComments(selectedProduct.getComments());
            if (selectedProduct.getAuctionID() != null)
                product.setAuctionID(selectedProduct.getAuctionID());
            Request request = new Request(Subject.EDIT_PRODUCT, "Edited " + product.toString() +  "\n" + "Current" + selectedProduct.toString());
            request.setProduct(product);
            request.setRequester(sellerUsername);
            Database.getInstance().saveRequest(request);
            return "Success";
        }
    }

    private String getRequestDeleteProductResult(String name) throws IOException, SQLException {
        Product product = Database.getInstance().getProductByName(name);
        Request request = new Request(Subject.DELETE_PRODUCT, product.toString());
        request.setProduct(product);
        request.setRequester(product.getSellerUsername());
        Database.getInstance().saveRequest(request);
        return "Success";
    }

    private String getProductByNameAsString(String name) {
        Product product = Database.getInstance().getProductByName(name);
        if (product == null)
            return "Not found";
        return "Success###" + objectToString(product);
    }

    private String getProductByIDAsString(String ID) {
        Product product = Database.getInstance().getProductByID(ID);
        if (product == null)
            return "Not found";
        return "Success###" + objectToString(product);
    }

    private String getDeleteProductResult(String name) throws IOException, SQLException {
        Product product = Database.getInstance().getProductByName(name);
        Database.getInstance().deleteProduct(product);
        return "Success";
    }

    private String getAllProductsFromCategoryAsString(String name) {
        Category category = Database.getInstance().getCategoryByName(name);
        ArrayList<Product> products = Database.getInstance().getAllProductsRecursively(category);
        String result = "Success";
        for (Product product : products)
            result += "###" + objectToString(product);
        return result;
    }

    private String getDeleteCategory(String name) throws IOException, SQLException {
        Category toBeDeletedCategory = Database.getInstance().getCategoryByName(name);
        if (!toBeDeletedCategory.getName().equals("root")) {
            Database.getInstance().deleteCategory(toBeDeletedCategory);
            return "Success";
        } else {
            return "You can not delete ROOT category";
        }
    }

    private String getCreateOrEditCategoryResult(String data) throws IOException, SQLException {
        String[] dataParts = data.split("###");
        String name = dataParts[1];
        String parent = dataParts[2];
        String prevName = dataParts[3].equals("null") ? null : dataParts[3];
        ArrayList<String> features = stringToObject(dataParts[4], ArrayList.class);

        if (prevName != null && prevName.equals("root")) {
            return "No changes for ROOT category";
        }
        if (name.isBlank()) {
            return "Enter category name";
        } else if (parent.isBlank()) {
            return "Enter parent category name";
        } else if (Database.getInstance().getCategoryByName(parent) == null) {
            return "No parent found";
        } else {
            Category category = Database.getInstance().getCategoryByName(name);
            Category parentCat = Database.getInstance().getCategoryByName(parent);
            if (prevName == null && category != null) {
                return "Category name exists";
            } else if (prevName == null && category == null) {
                ArrayList<String> catFeatures = new ArrayList<>(features);
                category = new Category(name, parent, new ArrayList<>(), new ArrayList<>(), catFeatures);
                parentCat.getSubCategories().add(category);
                Database.getInstance().updateCategories();
                return "Success";
            } else if (prevName != null && category != null) {
                if (!Database.getInstance().canChangeParentCategory(Database.getInstance().getCategoryByName(prevName), parentCat)) {
                    return "Can not change parent";
                }
                if (prevName.equals(name)) {
                    category.setFeatures(new ArrayList<>(features));
                    Category prevParentCat = Database.getInstance().getCategoryByName(category.getParentName());
                    if (!category.getParentName().equals(parent)) {
                        prevParentCat.getSubCategories().remove(category);
                        parentCat.getSubCategories().add(category);
                        category.setParentName(parent);
                    }
                    Database.getInstance().updateCategories();
                    return "Success";
                } else {
                    return "Category name exists";
                }
            } else {
                if (!Database.getInstance().canChangeParentCategory(Database.getInstance().getCategoryByName(prevName), parentCat)) {
                    return "Can not change parent";
                }
                category = Database.getInstance().getCategoryByName(prevName);
                category.setName(name);
                category.setFeatures(new ArrayList<>(features));
                Category prevParentCat = Database.getInstance().getCategoryByName(category.getParentName());
                if (!category.getParentName().equals(parent)) {
                    prevParentCat.getSubCategories().remove(category);
                    parentCat.getSubCategories().add(category);
                    category.setParentName(parent);
                }
                for (Product product : category.getProducts())
                    product.setParentCategoryName(name);
                for (Category child : category.getSubCategories())
                    child.setParentName(name);
                Database.getInstance().updateCategories();
                return "Success";
            }
        }
    }

    private String getCategoryByNameAsString(String name) {
        Category category = Database.getInstance().getCategoryByName(name);
        if (category == null)
            return "Not found";
        return "Success###" + objectToString(category);
    }

    private String getGenerateRandomCodeResult() throws IOException, SQLException {
        Date nowDate = new Date();
        DiscountCode discountCode;
        String startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(nowDate);
        long time = 24 * 60 * 60 * 1000 * 7;
        String endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(nowDate.getTime() + time));
        HashMap<String, Integer> hashMap = Database.getInstance().getRandomBuyers();
        discountCode = new DiscountCode("RND" + new SimpleDateFormat("mmssSSS").format(nowDate), startDate, endDate, Math.random() * 100, 100, 1, hashMap);
        Database.getInstance().saveDiscountCode(discountCode);
        for (String user : hashMap.keySet()) {
            Buyer buyer = (Buyer) Database.getInstance().getAccountByUsername(user);
            hashMap.put(user, 0);
            buyer.getDiscountCodes().add(discountCode.getCode());
            Database.getInstance().saveAccount(buyer);
        }
        return "Success";
    }

    private String getDeleteDiscountCodeResult(String code) throws IOException, SQLException {
        DiscountCode toBeDeletedDiscountcode = Database.getInstance().getDiscountCodeByCode(code);
        Database.getInstance().deleteDiscountCode(toBeDeletedDiscountcode);
        return "Success";
    }

    private String getCreateOrEditDiscountCodeResult(String data) throws IOException, SQLException {
        String[] dataParts = data.split("###");
        String selectedCode = dataParts[1].equals("null") ? null : dataParts[1];
        String code = dataParts[2];
        String startDate = dataParts[3];
        String endDate = dataParts[4];
        String percent = dataParts[5];
        String maxDiscount = dataParts[6];
        String maxUsage = dataParts[7];
        ArrayList<String> buyersList = stringToObject(dataParts[8], ArrayList.class);
        DiscountCode selected = Database.getInstance().getDiscountCodeByCode(selectedCode);
        String dateTimeRegex = "([12]\\d{3})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]) ([0-1]?[0-9]|2[0-3]):[0-5][0-9]";
        if (!code.matches("\\S+")) {
            return "Use non-whitespace letters for code";
        } else if (!startDate.matches(dateTimeRegex)) {
            return "Use format yyyy-mm-dd hh:mm for start date";
        } else if (!endDate.matches(dateTimeRegex)) {
            return "Use format yyyy-mm-dd hh:mm for end date";
        } else if (startDate.compareTo(endDate) >= 0) {
            return "Start date should be before end date";
        } else if (!percent.matches("(\\d+)(\\.\\d+)?")) {
            return "Percent should be DOUBLE value";
        } else if (!maxDiscount.matches("(\\d+)(\\.\\d+)?")) {
            return "Maximum discount should be DOUBLE value";
        } else if (!maxUsage.matches("\\d+")) {
            return "Maximum usage should be integer value";
        } else {
            double discountPercent = Double.parseDouble(percent);
            if (discountPercent > 100 || discountPercent <= 0) {
                return "Percent should be in range (0-100]";
            } else {
                double maximumDiscount = Double.parseDouble(maxDiscount);
                int maxUsageNumber = Integer.parseInt(maxUsage);
                HashMap<String, Integer> hashMap = new HashMap<>();
                DiscountCode discountCode = Database.getInstance().getDiscountCodeByCode(code);
                if (selected == null && discountCode != null) {
                    return "Discount code exists";
                } else if (selected == null && discountCode == null) {
                    for (String user : buyersList) {
                        Buyer buyer = (Buyer) Database.getInstance().getAccountByUsername(user);
                        hashMap.put(user, 0);
                        buyer.getDiscountCodes().add(code);
                        Database.getInstance().saveAccount(buyer);
                    }
                    Database.getInstance().saveDiscountCode(new DiscountCode(code, startDate, endDate, discountPercent, maximumDiscount, maxUsageNumber, hashMap));
                    return "Success";
                } else if (selected != null && discountCode != null && !selected.getCode().equals(code)) {
                    return "Discount code exists";
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
                    return "Success";
                }
            }
        }
    }

    private String getAllDiscountCodesAsString() {
        ArrayList<DiscountCode> discountCodes = Database.getInstance().getAllDiscountCodes();
        String result = "Success";
        for (DiscountCode discountCode : discountCodes) {
            result += "###" + objectToString(discountCode);
        }
        return result;
    }

    private String getDeclineRequestResult(String requestID) throws IOException, SQLException {
        Request request = Database.getInstance().getRequestByID(requestID);
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
        return "Success";
    }

    private String getAcceptRequestResult(String requestID) throws IOException, SQLException {
        Request request = Database.getInstance().getRequestByID(requestID);
        if (request.getSubject().equals(Subject.SELLER_REGISTER)) {
            String sellerUsername = request.getSeller().getUsername();
            Seller seller = (Seller) Database.getInstance().getAccountByUsername(sellerUsername);
            seller.setStatus(Status.ACCEPTED);
            request.getSeller().setStatus(Status.ACCEPTED);
            Server.requestCreatingBankAccountForClients(seller);
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
        return "Success";
    }

    private String getAllRequestsAsString() {
        ArrayList<Request> requests = Database.getInstance().getAllRequests();
        String result = "Success";
        for (Request request : requests) {
            result += "###" + objectToString(request);
        }
        return result;
    }

    private String getDeleteAccountResult(String managerUsername, String toBeDeletedUsername) {
        Account toBeDeletedAccount = Database.getInstance().getAccountByUsername(toBeDeletedUsername);
        if (managerUsername.equals(toBeDeletedUsername)) {
            return "You can not delete yourself";
        } else {
            Database.getInstance().deleteAccount(toBeDeletedAccount);
            return "Success";
        }
    }

    private String getAllAccountsAsString() {
        ArrayList<Account> accounts = Database.getInstance().getAllAccounts();
        String result = "Success";
        for (Account account : accounts) {
            if (account instanceof Manager)
                result += "###Manager###";
            if (account instanceof Seller)
                result += "###Seller###";
            if (account instanceof Buyer)
                result += "###Buyer###";
            if (account instanceof Supporter)
                result += "###Supporter###";

            result += objectToString(account);
        }
        return result;
    }

    private String getUpdateAccountInfoResult(String username, String password, Account changedAccount) throws IOException, SQLException, NoSuchAlgorithmException {
        Account account = Database.getInstance().getAccountByUsername(username);
        String firstName = changedAccount.getFirstName();
        String lastName = changedAccount.getLastName();
        String emailAddress = changedAccount.getEmailAddress();
        String phoneNumber = changedAccount.getPhoneNumber();
        String companyName = changedAccount instanceof Seller ? ((Seller) changedAccount).getCompanyName() : null;
        String newPassword = changedAccount.getPassword();

        if (!password.equals(encryptPassword(account.getPassword()))) {
            return "Enter current password correctly";
        } else if (!newPassword.isBlank() && !newPassword.matches("\\w+")) {
            return "Use word letters for new password";
        } else if (!firstName.matches("\\w+")) {
            return "Use word letters for first name";
        } else if (!lastName.matches("\\w+")) {
            return "Use word letters for last name";
        } else if (!emailAddress.matches("(\\S+)@(\\S+)\\.(\\S+)")) {
            return "Email format: example@gmail.com";
        } else if (!phoneNumber.matches("\\d+")) {
            return "Use digits for phone number";
        } else if (account instanceof Seller && !companyName.matches("\\w+")) {
            return "Use word letters for company name";
        } else {
            account.setFirstName(firstName);
            account.setLastName(lastName);
            account.setEmailAddress(emailAddress);
            account.setPhoneNumber(phoneNumber);
            account.setImagePath(changedAccount.getImagePath());
            if (account instanceof Seller)
                ((Seller) account).setCompanyName(companyName);
            if (!newPassword.isBlank())
                account.setPassword(newPassword);
            Database.getInstance().saveAccount(account);
            return "Success";
        }
    }

    private String getSignOutResult(String username) {
        if (allConnectedClients.keySet().contains(this))
            allConnectedClients.replace(this, null);
        return "Success";
    }

    private String getSignInResult(String username, String password) throws NoSuchAlgorithmException {
        Account account = Database.getInstance().getAccountByUsername(username);
        if (!username.matches("\\w+")) {
            return "Use word letters for username";
        } else if (!password.matches("\\S+")) {
            return "Use word letters for password";
        } else if (account == null || !encryptPassword(account.getPassword()).equals(password)) {
            return "Wrong username or password";
        } else if (account instanceof Seller && ((Seller) account).getStatus().equals(Status.PENDING)) {
            return "Account to be Confirmed";
        }  else if (account instanceof Seller && ((Seller) account).getStatus().equals(Status.DECLINED)) {
            return "Account not allowed";
        } else {
            if (allConnectedClients.keySet().contains(this))
                allConnectedClients.replace(this, username);
            else allConnectedClients.put(this, username);
            if (account instanceof Manager)
                return "Success###Manager###" + objectToString(account);
            if (account instanceof Seller)
                return "Success###Seller###" + objectToString(account);
            if (account instanceof Buyer)
                return "Success###Buyer###" + objectToString(account);
            if (account instanceof Supporter)
                return "Success###Supporter###" + objectToString(account);
        }
        return null;
    }

    private String getSignUpResult(Account account) throws IOException, SQLException {
        String str1 = validateUsername(account.getUsername());
        String str2 = validatePassword(account.getPassword());
        String str3 = validateName(account.getFirstName(), account.getLastName());
        String str4 = validateEmailAddres(account.getEmailAddress());
        String str5 = validatePhoneNumber(account.getPhoneNumber());
        if (str1 != null) return str1;
        if (str2 != null) return str2;
        if (str3 != null) return str3;
        if (str4 != null) return str4;
        if (str5 != null) return str5;
        if (!account.getUsername().matches("\\S+")) {
            return "Use word letters for username";
        } else if (!account.getPassword().matches("\\S+")) {
            return "Use word letters for password";
        } else if (!account.getFirstName().matches("\\S+")) {
            return "Use word letters for first name";
        } else if (!account.getLastName().matches("\\S+")) {
            return "Use word letters for last name";
        } else if (!account.getEmailAddress().matches("(\\S+)@(\\S+)\\.(\\S+)")) {
            return "Email format: example@gmail.com";
        } else if (!account.getPhoneNumber().matches("\\d+")) {
            return "Use digits for phone number";
        } else if (account instanceof Seller && !((Seller) account).getCompanyName().matches("\\w+")) {
            return "Use word letters for company name";
        } else if (Database.getInstance().getAccountByUsername(account.getUsername()) != null) {
            return "Username exists";
        } else {
            if (account instanceof Seller) {
                Request request = new Request(Subject.SELLER_REGISTER, account.toString());
                request.setSeller((Seller) account);
                Database.getInstance().saveRequest(request);
            }
            if (account instanceof Buyer) {
                Server.requestCreatingBankAccountForClients(account);
            }
            Database.getInstance().saveAccount(account);
            return "Success";
        }
    }

    private <T> T stringToObject(String string, Class<T> classOfT) {
        return new Gson().fromJson(string, classOfT);
    }

    private <T> String objectToString(T object) {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        return gson.toJson(object);
    }

    private void respondToClient(String response) throws IOException, SQLException {
        response += "###" + randomSessionKeyGenerator();
        dataOutputStream.writeUTF(response);
        dataOutputStream.flush();
    }

    private void printOnlineUsers() {
        for (ClientHandler clientHandler : allConnectedClients.keySet()) {
            System.out.println(allConnectedClients.get(clientHandler));
        }
    }

    // Security

    private final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789%&*+-/!?:[]{}()";

    private String randomSaltGenerator() {
        Random random = new Random(alphabet.length());
        String sessionKey = "";
        for (int i = 0; i < 12; i++) {
            sessionKey += alphabet.charAt(random.nextInt(alphabet.length()));
        }
        return sessionKey;
    }

    private String encryptPassword(String password) throws NoSuchAlgorithmException {
        String encryptedPassword = "";
        for (int i = 0; i < password.length(); i++) {
            int position = alphabet.indexOf(password.charAt(i));
            int newLetterPsition = ((int) (3 * position * position + Math.floor(Math.sqrt(position)) + 15)) % alphabet.length();
            char newLetter = alphabet.charAt(newLetterPsition);
            encryptedPassword += newLetter;
        }
        encryptedPassword = Hash.toHexString(Hash.getSHA(encryptedPassword));
        encryptedPassword = randomSaltGenerator() + encryptedPassword;
        return encryptedPassword;
    }

    static class Hash {
        public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(input.getBytes(StandardCharsets.UTF_8));
        }

        public static String toHexString(byte[] hash) {
            BigInteger number = new BigInteger(1, hash);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        }
    }

    private String randomSessionKeyGenerator() {
        Random random = new Random();
        String sessionKey = "";
        for (int i = 0; i < 8; i++) {
            sessionKey += alphabet.charAt(random.nextInt(alphabet.length()));
        }
        allActiveSessionKeys.add(sessionKey);
        return sessionKey;
    }

    // Security
    private String validateUsername(String username) {
        if (username.length() < 5)
            return "username is short";
        else if (username.length() > 30)
            return "username is long";
        else if (!username.matches("\\w+"))
            return "illegal characters";
        else return null;
    }

    private String validatePassword(String password) {
        int validationStep = 0;
        for (int i = 0; i < 26; i++) {
            if (password.contains(alphabet.substring(i, i + 1))) {
                validationStep++;
                break;
            }
        }
        for (int i = 26; i < 52; i++) {
            if (password.contains(alphabet.substring(i, i + 1))) {
                validationStep++;
                break;
            }
        }
        for (int i = 52; i < 62; i++) {
            if (password.contains(alphabet.substring(i, i + 1))) {
                validationStep++;
                break;
            }
        }
        for (int i = 62; i < alphabet.length(); i++) {
            if (password.contains(alphabet.substring(i, i + 1))) {
                validationStep++;
                break;
            }
        }

        if (password.length() < 5)
            return "password should have more than 8 characters";
        else if (password.length() > 30)
            return "password should have less than 30 characters";
        else if (password.contains(","))
            return "illegal characters. you can use <<abcdefghijklmnopqrstuvwxyz0123456789%&*+-/!?:[]{}() + capital letters>>";
        else if (validationStep != 4)
            return "weak password. you sholud use small letters, capital letters, numbers and writing marks in your password";
        else return null;
    }

    private String validateName(String firstName, String lastName) {
        if (!firstName.matches("[a-zA-Z]+") || !lastName.matches("[a-zA-Z]+"))
            return "first name and last name should be in english letters";
        else if (firstName.length() > 30 || lastName.length() > 30)
            return "first name or last name is too long";
        else return null;
    }

    private String validateEmailAddres(String emailAddress) {
        if (!emailAddress.matches("(\\w+)@gmail.com"))
            return "wrong patten. we only accept gmails";
        else if (emailAddress.length() > 60)
            return "gmail address is too long please use another one";
        else return null;
    }

    private String validatePhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("\\d+"))
            return "wrong patten";
        else if (phoneNumber.length() != 13)
            return "13 digits needed";
        else return null;
    }

    private String validateCompanyName(String companyName) {
        if (companyName.length() > 60)
            return "company name is too long";
        else return null;
    }
}



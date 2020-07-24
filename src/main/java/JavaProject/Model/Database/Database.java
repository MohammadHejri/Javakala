package JavaProject.Model.Database;

import JavaProject.App;
import JavaProject.Model.Account.*;
import JavaProject.Model.Discount.Auction;
import JavaProject.Model.Discount.DiscountCode;
import JavaProject.Model.Log.BuyLog;
import JavaProject.Model.Log.SellLog;
import JavaProject.Model.ProductOrganization.Category;
import JavaProject.Model.ProductOrganization.Product;
import JavaProject.Model.Request.Request;
import JavaProject.Model.Status.Status;
import JavaProject.Model.TripleString;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Database {

    public Product getProductByName(String name) {
        String response = App.getResponseFromServer("getProductByName", name);
        if (response.startsWith("Success")) {
            String[] responseParts = response.split("###");
            return App.stringToObject(responseParts[1], Product.class);
        } else {
            return null;
        }
    }

    public Product getProductByID(String ID) {
        String response = App.getResponseFromServer("getProductByID", ID);
        if (response.startsWith("Success")) {
            String[] responseParts = response.split("###");
            return App.stringToObject(responseParts[1], Product.class);
        } else {
            return null;
        }
    }

    public Auction getAuctionByID(String ID) {
        String response = App.getResponseFromServer("getAuctionByID", ID);
        if (response.startsWith("Success")) {
            String[] responseParts = response.split("###");
            return App.stringToObject(responseParts[1], Auction.class);
        } else {
            return null;
        }
    }

    public ArrayList<Product> getAllProducts() {
        Category root = Database.getInstance().getCategoryByName("root");
        return getAllProductsRecursively(root);
    }

    public ArrayList<Product> getAllProductsRecursively(Category category) {
        String response = App.getResponseFromServer("getAllProductsFromCategory", category.getName());
        String[] responseParts = response.split("###");
        ArrayList<Product> products = new ArrayList<>();
        for (int i = 1; i < responseParts.length; i++)
            products.add(App.stringToObject(responseParts[i], Product.class));
        return products;
    }

    public ArrayList<Product> getAllAcceptedProductsRecursively(Category category) {
        ArrayList<Product> products = getAllProductsRecursively(category);
        ArrayList<Product> newProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.getStatus().equals(Status.ACCEPTED))
                newProducts.add(product);
        }
        return newProducts;
    }

    public Category getCategoryByName(String name) {
        String response = App.getResponseFromServer("getCategoryByName", name);
        if (response.startsWith("Success")) {
            String[] responseParts = response.split("###");
            return App.stringToObject(responseParts[1], Category.class);
        } else {
            return null;
        }
    }

    public ArrayList<DiscountCode> getAllDiscountCodes() {
        String response = App.getResponseFromServer("getAllDiscountCodes");
        String[] responseParts = response.split("###");
        ArrayList<DiscountCode> discountCodes = new ArrayList<>();
        for (int i = 1; i < responseParts.length; i++)
            discountCodes.add(App.stringToObject(responseParts[i], DiscountCode.class));
        return discountCodes;
    }

    public ArrayList<Request> getAllRequests() {
        String response = App.getResponseFromServer("getAllRequests");
        String[] responseParts = response.split("###");
        ArrayList<Request> requests = new ArrayList<>();
        for (int i = 1; i < responseParts.length; i++)
            requests.add(App.stringToObject(responseParts[i], Request.class));
        return requests;
    }

    public ArrayList<Account> getAllAccounts() {
        String response = App.getResponseFromServer("getAllAccounts");
        String[] responseParts = response.split("###");
        ArrayList<Account> accounts = new ArrayList<>();
        for (int i = 1; i < responseParts.length; i += 2) {
            if (responseParts[i].equals("Manager"))
                accounts.add(App.stringToObject(responseParts[i + 1], Manager.class));
            if (responseParts[i].equals("Seller"))
                accounts.add(App.stringToObject(responseParts[i + 1], Seller.class));
            if (responseParts[i].equals("Buyer"))
                accounts.add(App.stringToObject(responseParts[i + 1], Buyer.class));
            if (responseParts[i].equals("Supporter"))
                accounts.add(App.stringToObject(responseParts[i + 1], Supporter.class));
        }
        return accounts;
    }

    private static Database instance;
//    private Category root;
//    private ArrayList<Account> allAccounts = new ArrayList<>();
//    private ArrayList<Request> allRequests = new ArrayList<>();
//    private ArrayList<Auction> allAuctions = new ArrayList<>();
//    private ArrayList<BuyLog> allBuyLogs = new ArrayList<>();
//    private ArrayList<SellLog> allSellLogs = new ArrayList<>();
//    private ArrayList<DiscountCode> allDiscountCodes = new ArrayList<>();
//    private final String rootPath = "src/main/resources/Data/Root";
//    private final String managersPath = "src/main/resources/Data/Accounts/Managers";
//    private final String sellersPath = "src/main/resources/Data/Accounts/Sellers";
//    private final String buyersPath = "src/main/resources/Data/Accounts/Buyers";
//    private final String requestsPath = "src/main/resources/Data/Requests";
//    private final String auctionsPath = "src/main/resources/Data/Auctions";
//    private final String buyLogsPath = "src/main/resources/Data/Log/BuyLogs";
//    private final String sellLogsPath = "src/main/resources/Data/Log/SellLogs";
//    private final String discountCodesPath = "src/main/resources/Data/DiscountCodes";

    public static Database getInstance() {
        if (instance == null)
            instance = new Database();
        return instance;
    }

//    public void loadResources() throws Exception {
//        new File(rootPath).mkdirs();
//        new File(managersPath).mkdirs();
//        new File(sellersPath).mkdirs();
//        new File(buyersPath).mkdirs();
//        new File(requestsPath).mkdirs();
//        new File(auctionsPath).mkdirs();
//        new File(buyLogsPath).mkdirs();
//        new File(sellLogsPath).mkdirs();
//        new File(discountCodesPath).mkdirs();
//        for (File file : new File(rootPath).listFiles())
//            root = new JsonFileReader().read(file, Category.class);
//        for (File file : new File(managersPath).listFiles())
//            allAccounts.add(new JsonFileReader().read(file, Manager.class));
//        for (File file : new File(sellersPath).listFiles())
//            allAccounts.add(new JsonFileReader().read(file, Seller.class));
//        for (File file : new File(buyersPath).listFiles())
//            allAccounts.add(new JsonFileReader().read(file, Buyer.class));
//        for (File file : new File(requestsPath).listFiles())
//            allRequests.add(new JsonFileReader().read(file, Request.class));
//        for (File file : new File(auctionsPath).listFiles())
//            allAuctions.add(new JsonFileReader().read(file, Auction.class));
//        for (File file : new File(buyLogsPath).listFiles())
//            allBuyLogs.add(new JsonFileReader().read(file, BuyLog.class));
//        for (File file : new File(sellLogsPath).listFiles())
//            allSellLogs.add(new JsonFileReader().read(file, SellLog.class));
//        for (File file : new File(discountCodesPath).listFiles())
//            allDiscountCodes.add(new JsonFileReader().read(file, DiscountCode.class));
//        if (root == null) {
//            root = new Category("root", null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
//            new JsonFileWriter().write(rootPath + "\\root.json", root);
//        }
//    }

//    public boolean managerExists() {
//        for (Account account : allAccounts)
//            if (account instanceof Manager)
//                return true;
//        return false;
//    }

//    public void saveAccount(Account account) throws IOException {
//        if (!allAccounts.contains(account))
//            allAccounts.add(account);
//        if (account instanceof Manager)
//            new JsonFileWriter().write(managersPath + "\\" + account.getUsername() + ".json", account);
//        if (account instanceof Seller)
//            new JsonFileWriter().write(sellersPath + "\\" + account.getUsername() + ".json", account);
//        if (account instanceof Buyer)
//            new JsonFileWriter().write(buyersPath + "\\" + account.getUsername() + ".json", account);
//    }

//    public void deleteAccount(Account account) {
//        allAccounts.remove(account);
//        File accountFile = null;
//        if (account instanceof Manager)
//            accountFile = new File(managersPath + "\\" + account.getUsername() + ".json");
//        if (account instanceof Seller) {
//            accountFile = new File(sellersPath + "\\" + account.getUsername() + ".json");
//            for (String productID : ((Seller) account).getProductsID()) {
//                try {
//                    deleteProduct(getProductByID(productID));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//        if (account instanceof Buyer)
//            accountFile = new File(buyersPath + "\\" + account.getUsername() + ".json");
//        accountFile.delete();
//    }

//    public void saveRequest(Request request) throws IOException {
//        if (!allRequests.contains(request))
//            allRequests.add(request);
//        new JsonFileWriter().write(requestsPath + "\\" + request.getID() + ".json", request);
//    }

//    public void saveAuction(Auction auction) throws IOException {
//        if (!allAuctions.contains(auction))
//            allAuctions.add(auction);
//        new JsonFileWriter().write(auctionsPath + "\\" + auction.getID() + ".json", auction);
//    }

//    public void deleteProduct(Product product) throws IOException {
//        Category parent = getCategoryByName(product.getParentCategoryName());
//        parent.getProducts().remove(product);
//        Seller seller = (Seller) getAccountByUsername(product.getSellerUsername());
//        if (seller != null) {
//            seller.getProductsID().remove(product.getID());
//            saveAccount(seller);
//        }
//        Auction auction = getAuctionByID(product.getAuctionID());
//        if (auction != null) {
//            auction.getProductsID().remove(product.getID());
//            saveAuction(auction);
//        }
//        for (Request request : allRequests) {
//            if (request.getStatus().equals(Status.PENDING) && request.getProduct() != null && request.getProduct().getID().equals(product.getID())) {
//                request.setStatus(Status.DECLINED);
//                saveRequest(request);
//            }
//        }
//        updateCategories();
//    }

//    public void deleteAuction(Auction auction) throws IOException {
//        allAuctions.remove(auction);
//        Seller seller = (Seller) getAccountByUsername(auction.getSellerUsername());
//        seller.getAuctionsID().remove(auction.getID());
//        for (String productID : auction.getProductsID())
//            getProductByID(productID).setAuctionID(null);
//        updateCategories();
//        File auctionFile = new File(auctionsPath + "\\" + auction.getID() + ".json");
//        auctionFile.delete();
//    }


//    public void saveDiscountCode(DiscountCode discountCode) throws IOException {
//        if (!allDiscountCodes.contains(discountCode))
//            allDiscountCodes.add(discountCode);
//        new JsonFileWriter().write(discountCodesPath + "\\" + discountCode.getID() + ".json", discountCode);
//    }

//    public void deleteDiscountCode(DiscountCode discountCode) throws IOException {
//        allDiscountCodes.remove(discountCode);
//        new File(discountCodesPath + "\\" + discountCode.getID() + ".json").delete();
//        for (String buyerName : discountCode.getBuyers().keySet()) {
//            Buyer buyer = (Buyer) getAccountByUsername(buyerName);
//            if (buyer != null) {
//                buyer.getDiscountCodes().remove(discountCode.getCode());
//                saveAccount(buyer);
//            }
//        }
//    }

//    public void saveSellLog(SellLog sellLog) throws IOException {
//        if (!allSellLogs.contains(sellLog))
//            allSellLogs.add(sellLog);
//        new JsonFileWriter().write(sellLogsPath + "\\" + sellLog.getID() + ".json", sellLog);
//    }

//    public void saveBuyLog(BuyLog buyLog) throws IOException {
//        if (!allBuyLogs.contains(buyLog))
//            allBuyLogs.add(buyLog);
//        new JsonFileWriter().write(buyLogsPath + "\\" + buyLog.getID() + ".json", buyLog);
//    }

//    public void updateCategories() throws IOException {
//        new JsonFileWriter().write(rootPath + "\\root.json", root);
//    }

//    public void deleteCategory(Category category) throws IOException {
//        Category parentCat = getCategoryByName(category.getParentName());
//        parentCat.getSubCategories().remove(category);
//        changeDeletedCategoryIncludedProducts(category, parentCat);
//        updateCategories();
//    }

//    private void changeDeletedCategoryIncludedProducts(Category deletedCategory, Category destination) {
//        for (Product product : deletedCategory.getProducts()) {
//            product.setParentCategoryName(destination.getName());
//            destination.getProducts().add(product);
//        }
//        for (Category subCat : deletedCategory.getSubCategories())
//            changeDeletedCategoryIncludedProducts(subCat, destination);
//    }

    public Account getAccountByUsername(String username) {
        for (Account account : getAllAccounts())
            if (account.getUsername().equals(username))
                return account;
        return null;
    }

    public Request getRequestByID(String ID) {
        for (Request request : getAllRequests())
            if (request.getID().equals(ID))
                return request;
        return null;
    }

    public ArrayList<BuyLog> getAllBuyLogs() {
        String response = App.getResponseFromServer("getAllBuyLogs");
        String[] responseParts = response.split("###");
        ArrayList<BuyLog> buyLogs = new ArrayList<>();
        for (int i = 1; i < responseParts.length; i++)
            buyLogs.add(App.stringToObject(responseParts[i], BuyLog.class));
        return buyLogs;
    }

    public ArrayList<SellLog> getAllSellLogs() {
        String response = App.getResponseFromServer("getAllSellLogs");
        String[] responseParts = response.split("###");
        ArrayList<SellLog> sellLogs = new ArrayList<>();
        for (int i = 1; i < responseParts.length; i++)
            sellLogs.add(App.stringToObject(responseParts[i], SellLog.class));
        return sellLogs;
    }

    public BuyLog getBuyLogByID(String ID) {
        for (BuyLog buyLog : getAllBuyLogs())
            if (buyLog.getID().equals(ID))
                return buyLog;
        return null;
    }

    public SellLog getSellLogByID(String ID) {
        for (SellLog sellLog : getAllSellLogs())
            if (sellLog.getID().equals(ID))
                return sellLog;
        return null;
    }

    public DiscountCode getDiscountCodeByCode(String code) {
        for (DiscountCode discountCode : getAllDiscountCodes())
            if (discountCode.getCode().equals(code))
                return discountCode;
        return null;
    }

    public ArrayList<TripleString> getComparison(Product p1, Product p2) {
        ArrayList<TripleString> arrayList = new ArrayList<>();
        if (p2 == null) {
            arrayList.add(new TripleString("brand", p1.getBrand(), ""));
            arrayList.add(new TripleString("seller", p1.getSellerUsername(), ""));
            for (String spec : p1.getSpecs().keySet())
                arrayList.add(new TripleString(spec, p1.getSpecs().get(spec), ""));
        } else {
            arrayList.add(new TripleString("brand", p1.getBrand(), p2.getBrand()));
            arrayList.add(new TripleString("seller", p1.getSellerUsername(), p2.getSellerUsername()));
            for (String spec : p1.getSpecs().keySet())
                arrayList.add(new TripleString(spec, p1.getSpecs().get(spec), p2.getSpecs().getOrDefault(spec, "")));
            for (String spec : p2.getSpecs().keySet()) {
                if (p1.getSpecs().containsKey(spec)) continue;
                else arrayList.add(new TripleString(spec, "", p2.getSpecs().get(spec)));
            }
        }
        return arrayList;
    }

    public ArrayList<String> getAllFeatures(Category category) {
        ArrayList<String> allFeatures = new ArrayList<>();
        if (category.getParentName() != null)
            allFeatures.addAll(getAllFeatures(getCategoryByName(category.getParentName())));
        allFeatures.addAll(category.getFeatures());
        return allFeatures;
    }

    public ArrayList<String> getProductsInNoAuction(Seller seller) {
        ArrayList<String> array = new ArrayList<>();
        for (String productID : seller.getProductsID()) {
            Product product = getProductByID(productID);
            if (product.getStatus().equals(Status.ACCEPTED) && product.getAuctionID() == null)
                array.add(productID);
        }
        return array;
    }

    public ArrayList<String> getSpecsInfoFromCategory(Category category, String feature) {
        ArrayList<String> array = new ArrayList<>();
        for (Category child : category.getSubCategories()) {
            ArrayList<String> childArray = getSpecsInfoFromCategory(child, feature);
            for (String childArrayStr : childArray) {
                boolean shouldAdd = true;
                for (String arrayStr : array) {
                    if (childArrayStr.equals(arrayStr)) {
                        shouldAdd = false;
                        break;
                    }
                }
                if (shouldAdd)
                    array.add(childArrayStr);
            }
        }
        for (Product product : category.getProducts()) {
            if (product.getSpecs().containsKey(feature)) {
                String value = product.getSpecs().get(feature);
                boolean shouldAdd = true;
                for (String arrayStr : array) {
                    if (value.equals(arrayStr)) {
                        shouldAdd = false;
                        break;
                    }
                }
                if (shouldAdd)
                    array.add(value);
            }
        }
        return array;
    }

    public ArrayList<String> getBrandsFromCategory(Category category) {
        ArrayList<String> array = new ArrayList<>();
        for (Category child : category.getSubCategories()) {
            ArrayList<String> childArray = getBrandsFromCategory(child);
            for (String childArrayStr : childArray) {
                boolean shouldAdd = true;
                for (String arrayStr : array) {
                    if (childArrayStr.equals(arrayStr)) {
                        shouldAdd = false;
                        break;
                    }
                }
                if (shouldAdd)
                    array.add(childArrayStr);
            }
        }
        for (Product product : category.getProducts()) {
            String value = product.getBrand();
            boolean shouldAdd = true;
            for (String arrayStr : array) {
                if (value.equals(arrayStr)) {
                    shouldAdd = false;
                    break;
                }
            }
            if (shouldAdd)
                array.add(value);
        }
        return array;
    }

    public ArrayList<String> getSellersFromCategory(Category category) {
        ArrayList<String> array = new ArrayList<>();
        for (Category child : category.getSubCategories()) {
            ArrayList<String> childArray = getSellersFromCategory(child);
            for (String childArrayStr : childArray) {
                boolean shouldAdd = true;
                for (String arrayStr : array) {
                    if (childArrayStr.equals(arrayStr)) {
                        shouldAdd = false;
                        break;
                    }
                }
                if (shouldAdd)
                    array.add(childArrayStr);
            }
        }
        for (Product product : category.getProducts()) {
            String value = product.getSellerUsername();
            boolean shouldAdd = true;
            for (String arrayStr : array) {
                if (value.equals(arrayStr)) {
                    shouldAdd = false;
                    break;
                }
            }
            if (shouldAdd)
                array.add(value);
        }
        return array;
    }

//    public boolean canChangeParentCategory(Category category, Category desired) {
//        if (category.getName().equals("root")) {
//            if (desired.getName().equals("root"))
//                return true;
//            return false;
//        }
//        if (category.getName().equals(desired.getName()))
//            return true;
//        return canChangeParentCategory(getCategoryByName(category.getParentName()), desired);
//    }

    public Auction getCurrentAuction(Product product) {
        try {
            Auction auction = getAuctionByID(product.getAuctionID());
            Date nowDate = new Date();
            Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(auction.getStartDate());
            Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(auction.getEndDate());
            boolean afterStart = nowDate.compareTo(startDate) > 0;
            boolean beforeEnd = nowDate.compareTo(endDate) < 0;
            if (afterStart && beforeEnd) {
                return auction;
            }
        } catch (Exception e) {
        }
        return null;
    }

//    public HashMap<String, Integer> getRandomBuyers() {
//        HashMap<String, Integer> arrayList = new HashMap<>();
//        for (Account buyer : getAllAccounts()) {
//            if (buyer instanceof Buyer && Math.random() < 0.5) {
//                arrayList.put(buyer.getUsername(), 0);
//            }
//        }
//        return arrayList;
//    }

}

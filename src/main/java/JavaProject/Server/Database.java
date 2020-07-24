package JavaProject.Server;

import JavaProject.Model.Account.*;
import JavaProject.Model.Chat.Conversation;
import JavaProject.Model.Discount.Auction;
import JavaProject.Model.Discount.DiscountCode;
import JavaProject.Model.DualString;
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

    private static Database instance;
    private Category root;
    private ArrayList<Account> allAccounts = new ArrayList<>();
    private ArrayList<Request> allRequests = new ArrayList<>();
    private ArrayList<Auction> allAuctions = new ArrayList<>();
    private ArrayList<BuyLog> allBuyLogs = new ArrayList<>();
    private ArrayList<SellLog> allSellLogs = new ArrayList<>();
    private ArrayList<DiscountCode> allDiscountCodes = new ArrayList<>();
    private ArrayList<Conversation> allConversations = new ArrayList<>();
    private final String rootPath = "src/main/server/Root";
    private final String managersPath = "src/main/server/Accounts/Managers";
    private final String sellersPath = "src/main/server/Accounts/Sellers";
    private final String buyersPath = "src/main/server/Accounts/Buyers";
    private final String supportersPath = "src/main/server/Accounts/Supporters";
    private final String requestsPath = "src/main/server/Requests";
    private final String auctionsPath = "src/main/server/Auctions";
    private final String buyLogsPath = "src/main/server/Log/BuyLogs";
    private final String sellLogsPath = "src/main/server/Log/SellLogs";
    private final String discountCodesPath = "src/main/server/DiscountCodes";
    private final String conversationsPath = "src/main/server/Conversations";
    protected DualString shopProperties;


    public static Database getInstance() {
        if (instance == null)
            instance = new Database();
        return instance;
    }

    public void loadResources() throws Exception {
        new File(rootPath).mkdirs();
        new File(managersPath).mkdirs();
        new File(sellersPath).mkdirs();
        new File(buyersPath).mkdirs();
        new File(supportersPath).mkdirs();
        new File(requestsPath).mkdirs();
        new File(auctionsPath).mkdirs();
        new File(buyLogsPath).mkdirs();
        new File(sellLogsPath).mkdirs();
        new File(discountCodesPath).mkdirs();
        new File(conversationsPath).mkdirs();
        for (File file : new File(rootPath).listFiles())
            root = new JsonFileReader().read(file, Category.class);
        for (File file : new File(managersPath).listFiles())
            allAccounts.add(new JsonFileReader().read(file, Manager.class));
        for (File file : new File(sellersPath).listFiles())
            allAccounts.add(new JsonFileReader().read(file, Seller.class));
        for (File file : new File(buyersPath).listFiles())
            allAccounts.add(new JsonFileReader().read(file, Buyer.class));
        for (File file : new File(supportersPath).listFiles())
            allAccounts.add(new JsonFileReader().read(file, Supporter.class));
        for (File file : new File(requestsPath).listFiles())
            allRequests.add(new JsonFileReader().read(file, Request.class));
        for (File file : new File(auctionsPath).listFiles())
            allAuctions.add(new JsonFileReader().read(file, Auction.class));
        for (File file : new File(buyLogsPath).listFiles())
            allBuyLogs.add(new JsonFileReader().read(file, BuyLog.class));
        for (File file : new File(sellLogsPath).listFiles())
            allSellLogs.add(new JsonFileReader().read(file, SellLog.class));
        for (File file : new File(discountCodesPath).listFiles())
            allDiscountCodes.add(new JsonFileReader().read(file, DiscountCode.class));
        for (File file : new File(conversationsPath).listFiles())
            allConversations.add(new JsonFileReader().read(file, Conversation.class));
        if (root == null) {
            root = new Category("root", null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            new JsonFileWriter().write(rootPath + "\\root.json", root);
        }

        try {
            File shopPropertiesFile = new File("src/main/server/shopProperties.json");
            shopProperties = new JsonFileReader().read(shopPropertiesFile, DualString.class);
        } catch (Exception e) {
            shopProperties = new DualString("5", "50");
            saveShopProperties();
        }
    }

    public boolean managerExists() {
        for (Account account : allAccounts)
            if (account instanceof Manager)
                return true;
        return false;
    }

    public void saveShopProperties() throws IOException {
        new JsonFileWriter().write("src/main/server/shopProperties.json", shopProperties);
    }

    public void saveAccount(Account account) throws IOException {
        if (getAccountByUsername(account.getUsername()) == null)
            allAccounts.add(account);
        if (account instanceof Manager)
            new JsonFileWriter().write(managersPath + "\\" + account.getUsername() + ".json", account);
        if (account instanceof Seller)
            new JsonFileWriter().write(sellersPath + "\\" + account.getUsername() + ".json", account);
        if (account instanceof Buyer)
            new JsonFileWriter().write(buyersPath + "\\" + account.getUsername() + ".json", account);
        if (account instanceof Supporter)
            new JsonFileWriter().write(supportersPath + "\\" + account.getUsername() + ".json", account);
    }

    public void deleteAccount(Account account) {
        allAccounts.remove(account);
        File accountFile = null;
        if (account instanceof Manager)
            accountFile = new File(managersPath + "\\" + account.getUsername() + ".json");
        if (account instanceof Seller) {
            accountFile = new File(sellersPath + "\\" + account.getUsername() + ".json");
            for (String productID : ((Seller) account).getProductsID()) {
                try {
                    deleteProduct(getProductByID(productID));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (account instanceof Buyer)
            accountFile = new File(buyersPath + "\\" + account.getUsername() + ".json");
        if (account instanceof Supporter)
            accountFile = new File(supportersPath + "\\" + account.getUsername() + ".json");
        accountFile.delete();
    }

    public void saveRequest(Request request) throws IOException {
        if (getRequestByID(request.getID()) == null)
            allRequests.add(request);
        new JsonFileWriter().write(requestsPath + "\\" + request.getID() + ".json", request);
    }

    public void saveAuction(Auction auction) throws IOException {
        if (getAuctionByID(auction.getID()) == null)
            allAuctions.add(auction);
        new JsonFileWriter().write(auctionsPath + "\\" + auction.getID() + ".json", auction);
    }

    public void deleteProduct(Product product) throws IOException {
        Category parent = getCategoryByName(product.getParentCategoryName());
        parent.getProducts().remove(product);
        Seller seller = (Seller) getAccountByUsername(product.getSellerUsername());
        if (seller != null) {
            seller.getProductsID().remove(product.getID());
            saveAccount(seller);
        }
        Auction auction = getAuctionByID(product.getAuctionID());
        if (auction != null) {
            auction.getProductsID().remove(product.getID());
            saveAuction(auction);
        }
        for (Request request : allRequests) {
            if (request.getStatus().equals(Status.PENDING) && request.getProduct() != null && request.getProduct().getID().equals(product.getID())) {
                request.setStatus(Status.DECLINED);
                saveRequest(request);
            }
        }
        updateCategories();
    }

    public void deleteAuction(Auction auction) throws IOException {
        allAuctions.remove(auction);
        Seller seller = (Seller) getAccountByUsername(auction.getSellerUsername());
        seller.getAuctionsID().remove(auction.getID());
        saveAccount(seller);
        for (String productID : auction.getProductsID())
            getProductByID(productID).setAuctionID(null);
        updateCategories();
        File auctionFile = new File(auctionsPath + "\\" + auction.getID() + ".json");
        auctionFile.delete();
    }


    public void saveDiscountCode(DiscountCode discountCode) throws IOException {
        if (getDiscountCodeByCode(discountCode.getCode()) == null)
            allDiscountCodes.add(discountCode);
        new JsonFileWriter().write(discountCodesPath + "\\" + discountCode.getID() + ".json", discountCode);
    }

    public void deleteDiscountCode(DiscountCode discountCode) throws IOException {
        allDiscountCodes.remove(discountCode);
        new File(discountCodesPath + "\\" + discountCode.getID() + ".json").delete();
        for (String buyerName : discountCode.getBuyers().keySet()) {
            Buyer buyer = (Buyer) getAccountByUsername(buyerName);
            if (buyer != null) {
                buyer.getDiscountCodes().remove(discountCode.getCode());
                saveAccount(buyer);
            }
        }
    }

    public void saveSellLog(SellLog sellLog) throws IOException {
        if (getSellLogByID(sellLog.getID()) == null)
            allSellLogs.add(sellLog);
        new JsonFileWriter().write(sellLogsPath + "\\" + sellLog.getID() + ".json", sellLog);
    }

    public void saveBuyLog(BuyLog buyLog) throws IOException {
        if (getBuyLogByID(buyLog.getID()) == null)
            allBuyLogs.add(buyLog);
        new JsonFileWriter().write(buyLogsPath + "\\" + buyLog.getID() + ".json", buyLog);
    }

    public void updateCategories() throws IOException {
        new JsonFileWriter().write(rootPath + "\\root.json", root);
    }

    public void deleteCategory(Category category) throws IOException {
        Category parentCat = getCategoryByName(category.getParentName());
        parentCat.getSubCategories().remove(category);
        changeDeletedCategoryIncludedProducts(category, parentCat);
        updateCategories();
    }

    private void changeDeletedCategoryIncludedProducts(Category deletedCategory, Category destination) {
        for (Product product : deletedCategory.getProducts()) {
            product.setParentCategoryName(destination.getName());
            destination.getProducts().add(product);
        }
        for (Category subCat : deletedCategory.getSubCategories())
            changeDeletedCategoryIncludedProducts(subCat, destination);
    }

    public Account getAccountByUsername(String username) {
        for (Account account : allAccounts)
            if (account.getUsername().equals(username))
                return account;
        return null;
    }

    public Request getRequestByID(String ID) {
        for (Request request : allRequests)
            if (request.getID().equals(ID))
                return request;
        return null;
    }

    public Auction getAuctionByID(String ID) {
        for (Auction auction : allAuctions)
            if (auction.getID().equals(ID))
                return auction;
        return null;
    }

    public BuyLog getBuyLogByID(String ID) {
        for (BuyLog buyLog : allBuyLogs)
            if (buyLog.getID().equals(ID))
                return buyLog;
        return null;
    }

    public SellLog getSellLogByID(String ID) {
        for (SellLog sellLog : allSellLogs)
            if (sellLog.getID().equals(ID))
                return sellLog;
        return null;
    }

    public Product getProductByID(String ID) {
        for (Product product : getAllProducts())
            if (product.getID().equals(ID))
                return product;
        return null;
    }

    public Category getCategoryByName(String name) {
        return getRecursiveCategory(name, root);
    }

    private Category getRecursiveCategory(String name, Category current) {
        if (current.getName().equals(name))
            return current;
        for (Category category : current.getSubCategories()) {
            Category cat = getRecursiveCategory(name, category);
            if (cat != null)
                return cat;
        }
        return null;
    }

    public Product getProductByName(String name) {
        return getRecursiveProduct(name, root);
    }

    private Product getRecursiveProduct(String name, Category current) {
        for (Product product : current.getProducts())
            if (product.getName().equals(name))
                return product;
        for (Category category : current.getSubCategories()) {
            Product product = getRecursiveProduct(name, category);
            if (product != null)
                return product;
        }
        return null;

    }

    public DiscountCode getDiscountCodeByCode(String code) {
        if (code == null)
            return null;
        for (DiscountCode discountCode : allDiscountCodes)
            if (discountCode.getCode().equals(code))
                return discountCode;
        return null;
    }

    public ArrayList<Account> getAllAccounts() {
        return allAccounts;
    }

    public ArrayList<DiscountCode> getAllDiscountCodes() {
        return allDiscountCodes;
    }

    public ArrayList<Product> getAllProducts() {
        return getAllProductsRecursively(root);
    }

    public ArrayList<Product> getAllProductsRecursively(Category category) {
        ArrayList<Product> products = new ArrayList<>(category.getProducts());
        for (Category subCat : category.getSubCategories())
            products.addAll(getAllProductsRecursively(subCat));
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

    public ArrayList<Request> getAllRequests() {
        return allRequests;
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

    public boolean canChangeParentCategory(Category category, Category desired) {
        if (category == null)
            return true;
        if (category.getName().equals("root")) {
            if (desired.getName().equals("root"))
                return true;
            return false;
        }
        if (category.getName().equals(desired.getName()))
            return true;
        return canChangeParentCategory(getCategoryByName(category.getParentName()), desired);
    }

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

    public HashMap<String, Integer> getRandomBuyers() {
        HashMap<String, Integer> arrayList = new HashMap<>();
        for (Account buyer : getAllAccounts()) {
            if (buyer instanceof Buyer && Math.random() < 0.5) {
                arrayList.put(buyer.getUsername(), 0);
            }
        }
        return arrayList;
    }

    public void saveConversation(Conversation conversation) throws IOException {
        if (getConversationByBothSides(conversation.getFirstSide(), conversation.getSecondSide()) == null)
            allConversations.add(conversation);
        new JsonFileWriter().write(conversationsPath + "\\" + conversation.getID() + ".json", conversation);
    }

    public void deleteConversation(Conversation conversation) throws IOException {
        allConversations.remove(conversation);
        new File(conversationsPath + "\\" + conversation.getID() + ".json").delete();
    }

    public Conversation getConversationByBothSides(String senderUsername, String recieverUsername) {
        for (Conversation conversation : allConversations) {
            String firstSide = conversation.getFirstSide();
            String secondSide = conversation.getSecondSide();
            boolean b1 = firstSide.equals(senderUsername) && secondSide.equals(recieverUsername);
            boolean b2 = firstSide.equals(recieverUsername) && secondSide.equals(senderUsername);
            if (b1 || b2) return conversation;
        }
        return null;
    }

    public ArrayList<BuyLog> getAllBuyLogs() {
        return allBuyLogs;
    }

    public ArrayList<SellLog> getAllSellLogs() {
        return allSellLogs;
    }
}

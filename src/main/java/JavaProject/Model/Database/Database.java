package JavaProject.Model.Database;

import JavaProject.Model.Account.Account;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Account.Manager;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Discount.Auction;
import JavaProject.Model.Discount.DiscountCode;
import JavaProject.Model.Log.BuyLog;
import JavaProject.Model.Log.SellLog;
import JavaProject.Model.ProductOrganization.Category;
import JavaProject.Model.ProductOrganization.Product;
import JavaProject.Model.Request.Request;
import JavaProject.Model.Request.Subject;
import JavaProject.Model.Status.Status;
import JavaProject.Model.TripleString;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Database {

    private static Database instance;
    private Category root;
    private ArrayList<Account> allAccounts = new ArrayList<>();
    private ArrayList<Request> allRequests = new ArrayList<>();
    private ArrayList<Auction> allAuctions = new ArrayList<>();
    private ArrayList<BuyLog> allBuyLogs = new ArrayList<>();
    private ArrayList<SellLog> allSellLogs = new ArrayList<>();
    private ArrayList<DiscountCode> allDiscountCodes = new ArrayList<>();
    private final String rootPath = "src/main/resources/Data/Root";
    private final String managersPath = "src/main/resources/Data/Accounts/Managers";
    private final String sellersPath = "src/main/resources/Data/Accounts/Sellers";
    private final String buyersPath = "src/main/resources/Data/Accounts/Buyers";
    private final String requestsPath = "src/main/resources/Data/Requests";
    private final String auctionsPath = "src/main/resources/Data/Auctions";
    private final String buyLogsPath = "src/main/resources/Data/Log/BuyLogs";
    private final String sellLogsPath = "src/main/resources/Data/Log/SellLogs";
    private final String discountCodesPath = "src/main/resources/Data/DiscountCodes";

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
        new File(requestsPath).mkdirs();
        new File(auctionsPath).mkdirs();
        new File(buyLogsPath).mkdirs();
        new File(sellLogsPath).mkdirs();
        new File(discountCodesPath).mkdirs();
        for (File file : new File(rootPath).listFiles())
            root = new JsonFileReader().read(file, Category.class);
        for (File file : new File(managersPath).listFiles())
            allAccounts.add(new JsonFileReader().read(file, Manager.class));
        for (File file : new File(sellersPath).listFiles())
            allAccounts.add(new JsonFileReader().read(file, Seller.class));
        for (File file : new File(buyersPath).listFiles())
            allAccounts.add(new JsonFileReader().read(file, Buyer.class));
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
        if (root == null)
            new JsonFileWriter().write(rootPath + "\\root.json", new Category("root", null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    }

    public boolean managerExists() {
        for (Account account : allAccounts)
            if (account instanceof Manager)
                return true;
        return false;
    }

    public void saveAccount(Account account) throws IOException {
        if (!allAccounts.contains(account))
            allAccounts.add(account);
        if (account instanceof Manager)
            new JsonFileWriter().write(managersPath + "\\" + account.getUsername() + ".json", account);
        if (account instanceof Seller)
            new JsonFileWriter().write(sellersPath + "\\" + account.getUsername() + ".json", account);
        if (account instanceof Buyer)
            new JsonFileWriter().write(buyersPath + "\\" + account.getUsername() + ".json", account);
    }

    // TODO: delete auctions, products, requests for seller
    public void deleteAccount(Account account) {
        allAccounts.remove(account);
        File accountFile = null;
        if (account instanceof Manager)
            accountFile = new File(managersPath + "\\" + account.getUsername() + ".json");
        if (account instanceof Seller) {
            accountFile = new File(sellersPath + "\\" + account.getUsername() + ".json");
//            for (String productID : ((Seller) account).getProductsID())
//                deleteProduct(getProductByID(productID));
//            for (String auctionID : ((Seller) account).getAuctionsID())
//                deleteAuction(getAuctionByID(auctionID));
//            for (String requestID : ((Seller) account).getRequestsID())
//                deleteRequest(getRequestByID(requestID));
        }
        if (account instanceof Buyer)
            accountFile = new File(buyersPath + "\\" + account.getUsername() + ".json");
        accountFile.delete();
    }

    public void saveRequest(Request request) throws IOException {
        if (!allRequests.contains(request))
            allRequests.add(request);
        new JsonFileWriter().write(requestsPath + "\\" + request.getID() + ".json", request);
    }

    public void saveAuction(Auction auction) throws IOException {
        if (!allAuctions.contains(auction))
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
        for (String productID : auction.getProductsID())
            getProductByID(productID).setAuctionID(null);
        updateCategories();
        File auctionFile = new File(auctionsPath + "\\" + auction.getID() + ".json");
        auctionFile.delete();
    }


    public void saveDiscountCode(DiscountCode discountCode) throws IOException {
        if (!allDiscountCodes.contains(discountCode))
            allDiscountCodes.add(discountCode);
        new JsonFileWriter().write(discountCodesPath + "\\" + discountCode.getID() + ".json", discountCode);
    }

    public void deleteDiscountCode(DiscountCode discountCode) {
        allDiscountCodes.remove(discountCode);
        new File(discountCodesPath + "\\" + discountCode.getID() + ".json").delete();
    }

    public void saveSellLog(SellLog sellLog) throws IOException {
        if (!allSellLogs.contains(sellLog))
            allSellLogs.add(sellLog);
        new JsonFileWriter().write(sellLogsPath + "\\" + sellLog.getID() + ".json", sellLog);
    }

    public void saveBuyLog(BuyLog buyLog) throws IOException {
        if (!allBuyLogs.contains(buyLog))
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
                arrayList.add(new TripleString(spec , p1.getSpecs().get(spec), ""));
        } else {
            arrayList.add(new TripleString("brand", p1.getBrand(), p2.getBrand()));
            arrayList.add(new TripleString("seller", p1.getSellerUsername(), p2.getSellerUsername()));
            for (String spec : p1.getSpecs().keySet())
                arrayList.add(new TripleString(spec , p1.getSpecs().get(spec), p2.getSpecs().getOrDefault(spec, "")));
            for (String spec : p2.getSpecs().keySet()) {
                if (p1.getSpecs().containsKey(spec)) continue;
                else arrayList.add(new TripleString(spec , "", p2.getSpecs().get(spec)));
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
}

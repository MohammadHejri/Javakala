import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Account.Manager;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Database.Database;
import JavaProject.Model.Discount.Auction;
import JavaProject.Model.Discount.DiscountCode;
import JavaProject.Model.ProductOrganization.Category;
import JavaProject.Model.ProductOrganization.Comment;
import JavaProject.Model.ProductOrganization.Product;
import JavaProject.Model.Request.Request;
import JavaProject.Model.Request.Subject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class TestCases {
    static Seller[] sellers;
    static Manager[] managers;
    static Buyer[] buyers;
    static Product[] products;
    static ArrayList<Category> categories;
    static ArrayList<Auction> auctions;
    static ArrayList<DiscountCode> discountCodes;
    static ArrayList<Request> requests;


    public static void createAccounts() throws Exception {
        sellers = new Seller[]{new Seller("seller1", "seller1", "seller1", "seller1", "seller1", "seller1", null, "seller1")
                , new Seller("seller2", "seller2", "seller2", "seller2", "seller2", "seller2", null, "seller2")
                , new Seller("seller3", "seller3", "seller3", "seller3", "seller3", "seller3", null, "seller3")};
        managers = new Manager[]{new Manager("manager1", "manager1", "manager1", "manager1", "manager1", "manager1", "manager1")
                , new Manager("manager2", "manager2", "manager2", "manager2", "manager2", "manager2", "manager2")
                , new Manager("manager3", "manager3", "manager3", "manager3", "manager3", "manager3", "manager3")};
        buyers = new Buyer[]{new Buyer("buyer1", "buyer1", "buyer1", "buyer1", "buyer1", "buyer1", "buyer1")
                , new Buyer("buyer2", "buyer2", "buyer2", "buyer2", "buyer2", "buyer2", "buyer2")
                , new Buyer("buyer3", "buyer3", "buyer3", "buyer3", "buyer3", "buyer3", "buyer3")};
        products = new Product[]{new Product("product1", "brand1", 1, "seller1", 11, null, "product1", null, "")
                , new Product("product2", "brand2", 2, "seller2", 22, null, "product2", null, "")
                , new Product("product3", "brand3", 3, "seller3", 33, null, "product3", null, "")};
        categories = new ArrayList<>();
        categories.add(new Category("first", "root", new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        categories.add(new Category("second", "first", new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        categories.add(new Category("third", "second", new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        categories.get(0).getSubCategories().add(categories.get(1));
        categories.get(1).getSubCategories().add(categories.get(2));
        categories.get(0).getProducts().add(products[0]);
        categories.get(1).getProducts().add(products[1]);
        categories.get(2).getProducts().add(products[2]);
        auctions = new ArrayList<>();
        discountCodes = new ArrayList<>();
        auctions.add(new Auction("seller1", "1394-22-2 12:12:12:111", "1395-24-2 12:12:12:111", 12.1, new ArrayList<>()));
        discountCodes.add(new DiscountCode("discountCode", "1394-22-2 12:12:12:111", "1395-24-2 12:12:12:111", 14.1, 15, 12, new HashMap<>()));
        auctions.get(0).getProductsID().add(products[0].getID());
        discountCodes.add(new DiscountCode(discountCodes.get(0)));
        discountCodes.get(0).getBuyers().put(buyers[0].getUsername(), discountCodes.get(0).getMaxUsageNumber());
        requests = new ArrayList<>();
        requests.add(new Request(Subject.ADD_AUCTION, "addAuction"));
        requests.add(new Request(Subject.ADD_PRODUCT, "addProduct"));
        requests.add(new Request(Subject.DELETE_AUCTION, "deleteAuction"));
        requests.add(new Request(Subject.DELETE_PRODUCT, "deleteProduct"));
        requests.add(new Request(Subject.SELLER_REGISTER, "sellerRegister"));
        requests.add(new Request(Subject.EDIT_AUCTION, "edit"));
        requests.add(new Request(Subject.EDIT_PRODUCT, "edit"));
        products[0].getComments().add(new Comment("buyer1", "test", "what the heck?!!!!!"));
       /* Database.getInstance().loadResources();
        Database.getInstance().getCategoryByName("root").getSubCategories().add(categories.get(1));
        Database.getInstance().updateCategories();
        try {
            Database.getInstance().loadResources();
            Database database = Database.getInstance();
            for (int i = 0; i < 3; i++) {
                database.saveAccount(buyers[i]);
                database.saveAccount(sellers[i]);
                database.saveAccount(managers[i]);

            }
            for (Request request : requests) {
                database.saveRequest(request);
            }
            database.saveAuction(auctions.get(0));
            database.saveDiscountCode(discountCodes.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Test
    public void DatabaseTests() throws Exception {
        Database database = Database.getInstance();
        database.loadResources();
        Assert.assertEquals(database.getAccountByUsername("seller1"), sellers[0]);
        Assert.assertEquals(database.getRequestByID("REQ20200626190618108"), requests.get(3));
        Assert.assertEquals(database.getDiscountCodeByCode(discountCodes.get(0).getCode()), discountCodes.get(0));
    }

}

package JavaProject.Model.Request;


import JavaProject.App;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Database.IDGenerator;
import JavaProject.Model.Discount.Auction;
import JavaProject.Model.ProductOrganization.Comment;
import JavaProject.Model.ProductOrganization.Product;
import JavaProject.Model.Status.Status;

public class Request {

    private String ID;
    private Subject subject;
    private String description;
    private Status status = Status.PENDING;
    private String requester;

    private Seller seller;
    private Product product;
    private Auction auction;
    private Comment comment;

    public Request(Subject subject, String description) {
        this.subject = subject;
        this.description = description;
        ID = IDGenerator.getGeneratedID("REQ");
        //requester = App.getSignedInAccount() == null ? "" : App.getSignedInAccount().getUsername();
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
        requester = seller.getUsername();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }
}

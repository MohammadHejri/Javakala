package JavaProject.Model.ProductOrganization;

import JavaProject.Model.Status.Status;

public class Comment {
    private String ID;
    private String commenterName;
    private String title;
    private String content;
    private Status status;
    private boolean isBuyer;

    public Comment(String commenterName, String title, String content) {
        this.commenterName = commenterName;
        this.title = title;
        this.content = content;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

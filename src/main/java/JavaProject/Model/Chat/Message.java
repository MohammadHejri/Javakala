package JavaProject.Model.Chat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    private String ID;
    private String content;
    private String senderUsername;
    private String recieverUsername;

    public Message(String content, String senderUsername, String recieverUsername) {
        this.ID = senderUsername + " " + recieverUsername + " " + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        this.content = content;
        this.senderUsername = senderUsername;
        this.recieverUsername = recieverUsername;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getRecieverUsername() {
        return recieverUsername;
    }

    public void setRecieverUsername(String recieverUsername) {
        this.recieverUsername = recieverUsername;
    }
}

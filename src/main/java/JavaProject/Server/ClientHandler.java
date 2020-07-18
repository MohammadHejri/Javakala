package JavaProject.Server;

import JavaProject.App;
import JavaProject.Controller.BuyerProfileController;
import JavaProject.Controller.ManagerProfileController;
import JavaProject.Controller.RegisterPanelController;
import JavaProject.Controller.SellerProfileController;
import JavaProject.Model.Account.Account;
import JavaProject.Model.Account.Buyer;
import JavaProject.Model.Account.Manager;
import JavaProject.Model.Account.Seller;
import JavaProject.Model.Request.Request;
import JavaProject.Model.Request.Subject;
import JavaProject.Model.Status.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {

    private Socket clientSocket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public ClientHandler(Socket clientSocket, DataOutputStream dataOutputStream, DataInputStream dataInputStream) {
        this.clientSocket = clientSocket;
        this.dataOutputStream = dataOutputStream;
        this.dataInputStream = dataInputStream;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = dataInputStream.readUTF();
                System.out.println("==================================================");
                System.out.println(message);
                System.out.println("==================================================");
                String[] messageParts = message.split("###");
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
                } else if (message.startsWith("signIn")) {
                    respondToClient(getSignInResult(messageParts[1], messageParts[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getSignInResult(String username, String password) {
        Account account = Database.getInstance().getAccountByUsername(username);
        if (!username.matches("\\w+")) {
            return "Use word letters for username";
        } else if (!password.matches("\\w+")) {
            return "Use word letters for password";
        } else if (account == null || !account.getPassword().equals(password)) {
            return "Wrong username or password";
        } else if (account instanceof Seller && ((Seller) account).getStatus().equals(Status.PENDING)) {
            return "Account to be Confirmed";
        }  else if (account instanceof Seller && ((Seller) account).getStatus().equals(Status.DECLINED)) {
            return "Account not allowed";
        } else {
            if (account instanceof Manager)
                return "Success###Manager###" + objectToString(account);
            if (account instanceof Seller)
                return "Success###Seller###" + objectToString(account);
            if (account instanceof Buyer)
                return "Success###Buyer###" + objectToString(account);
        }
        return null;
    }

    private String getSignUpResult(Account account) throws IOException {
        if (!account.getUsername().matches("\\w+")) {
            return "Use word letters for username";
        } else if (!account.getPassword().matches("\\w+")) {
            return "Use word letters for password";
        } else if (!account.getFirstName().matches("\\w+")) {
            return "Use word letters for first name";
        } else if (!account.getLastName().matches("\\w+")) {
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

    private void respondToClient(String response) throws IOException {
        dataOutputStream.writeUTF(response);
        dataOutputStream.flush();
    }

}

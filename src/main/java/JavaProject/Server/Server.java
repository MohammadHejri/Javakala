package JavaProject.Server;

import JavaProject.Model.Account.Account;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Formatter;

public class Server {

    private static final int serverPort = 8080;
    private static final int bankPort = 6666;
    private static Socket socket;
    private static BufferedReader reader;
    private static Formatter writer;
    protected static String shopAccountUsername = "Javakala";
    protected static int shopAccountNumber;

    public static void main(String[] args) throws Exception {
        connentToBank();
        Database.getInstance().loadResources();
        requestCreatingBankAccountForShop();
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(serverPort);
                while (true) {
                    System.out.println("Waiting for client...");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected...");
                    DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                    DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                    new ClientHandler(clientSocket, dataOutputStream, dataInputStream, generateToken()).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static String generateToken() {
        byte[] randomBytes = new byte[24];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().encodeToString(randomBytes);
    }

    private static void connentToBank() throws IOException {
        socket = new Socket("127.0.0.1", bankPort);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new Formatter(socket.getOutputStream());
    }

    private static void requestCreatingBankAccountForShop() throws IOException {
        String message = "create_account Javakala Javakala " + shopAccountUsername + " Javakala Javakala\n";
        writer.format(message);
        writer.flush();
        String ID = reader.readLine();
        try {
            shopAccountNumber = Integer.parseInt(ID);
            System.out.println("Shop bank account number: " + shopAccountNumber);
        } catch (Exception e) {
            System.out.println("Bank account already created");
        }
    }

    protected static void requestCreatingBankAccountForClients(Account account) throws IOException {
        String message = "create_account " + account.getFirstName() + " " + account.getLastName() + " " + account.getUsername() + " " + account.getPassword() + " " + account.getPassword() + "\n";
        writer.format(message);
        writer.flush();
        String response = reader.readLine();
        try {
            int accountNumber = Integer.parseInt(response);
            System.out.println("Client (" + account.getUsername() + ") bank account number: " + accountNumber);
            account.setBankAccountNumber(accountNumber);
        } catch (Exception e) {
            System.out.println(response);
        }
    }

    protected static String requestGettingToken(Account account) throws IOException {
        String message = "get_token " + account.getUsername() + " " + account.getPassword() + "\n";
        writer.format(message);
        writer.flush();
        String response = reader.readLine();
        System.out.println(response);
        return response.startsWith("invalid") ? null : response;
    }

    protected static String requestGettingReceipt(String token, String type, String amount, String fromID, String toID, String description) throws IOException {
        String message = "create_receipt " + token + " " + type + " " + amount + " " + fromID + " " + toID + " " + description + "\n";
        writer.format(message);
        writer.flush();
        String response = reader.readLine();
        System.out.println(response);
        return response;
    }

    protected static String requestPayingReceipt(String payID) throws IOException {
        String message = "pay " + payID + "\n";
        writer.format(message);
        writer.flush();
        String response = reader.readLine();
        System.out.println(response);
        return response;
    }




}

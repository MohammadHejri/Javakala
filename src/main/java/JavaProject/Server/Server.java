package JavaProject.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int serverPort = 8080;

    public static void main(String[] args) throws Exception {
        Database.getInstance().loadResources();
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(serverPort);
                while (true) {
                    System.out.println("Waiting for client...");
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected...");
                    DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                    DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                    new ClientHandler(clientSocket, dataOutputStream, dataInputStream).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}

package JavaProject;

import JavaProject.Model.Account.Account;
import JavaProject.Model.Database.Database;
import JavaProject.Model.ProductOrganization.Cart;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class App extends Application {

    private static final int serverPort = 8080;
    private static Socket socket;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;
    private static String token;
    private static String session;

    private static Scene scene;
    private static Account signedInAccount;

    public static Parent mainPage;
    public static Parent productsPage;
    public static Parent cartPage;

    private static Cart cart = new Cart();

    public static void initProductsPage() {
        try {
            productsPage = loadFXML("productsPage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        connectToServer();
        // Database.getInstance().loadResources();
        mainPage = loadFXML("mainMenu");
        // productsPage = loadFXML("productsPage");
        scene = new Scene(mainPage);
        stage.setScene(scene);
        stage.show();
        //mediaPlayer.play();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        //changeBgMusic();
    }

    public static void setRoot(Parent root) {
        scene.setRoot(root);
        //changeBgMusic();
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Account getSignedInAccount() {
        if (signedInAccount != null)
            signedInAccount = Database.getInstance().getAccountByUsername(signedInAccount.getUsername());
        return signedInAccount;
    }

    public static void setSignedInAccount(Account signedInAccount) {
        App.signedInAccount = signedInAccount;
    }

    public static Cart getCart() {
        return cart;
    }

    public static void setCart(Cart cart) {
        App.cart = cart;
    }

    public static Parent getProductsPage() {
        return productsPage;
    }

    public static void main(String[] args) {
        launch();
    }

    private static int option = 0;

    private static MediaPlayer mediaPlayer = new MediaPlayer(new Media(Paths.get("src/main/resources/APPROJMUSIC/1.mp3").toUri().toString()));

    private static String[] backgroundMusics = new String[]{Paths.get("src/main/resources/APPROJMUSIC/2.mp3").toUri().toString(),
            Paths.get("src/main/resources/APPROJMUSIC/3.mp3").toUri().toString()
            , Paths.get("src/main/resources/APPROJMUSIC/4.mp3").toUri().toString()
            , Paths.get("src/main/resources/APPROJMUSIC/1.mp3").toUri().toString()};

    public static void changeBgMusic(){
        mediaPlayer.stop();
        mediaPlayer = new MediaPlayer(new Media(backgroundMusics[(++option)%4]));
        mediaPlayer.play();
    }

    private static void connectToServer() throws IOException {
        // socket = new Socket("0.tcp.ngrok.io", 11173);
        socket = new Socket("127.0.0.1", 8080);
        dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        dataOutputStream.writeUTF("getToken");
        dataOutputStream.flush();
        String[] response = dataInputStream.readUTF().split("###");
        token = response[0];
        session = response[1];
    }

    public static String getResponseFromServer(String ... messageParts) {
        String message = "";
        for (String messagePart : messageParts) {
            message += "###" + messagePart;
        }
        message += "###" + token + "###" + session;
        message = message.substring(3);
        try {
            int index = 0;
            int length = message.length();
            while (length > 32 * 1024) {
                dataOutputStream.writeUTF(message.substring(index, 32 * 1024 + index));
                dataOutputStream.flush();
                dataInputStream.readUTF();
                index += 32 * 1024;
                length -= 32 * 1024;
            }
            if (length > 0) {
                dataOutputStream.writeUTF(message.substring(index));
                dataOutputStream.flush();
                dataInputStream.readUTF();
            }
            dataOutputStream.writeUTF("END_OF_MESSAGE");
            dataOutputStream.flush();
            String response = dataInputStream.readUTF();
            session = response.substring(response.lastIndexOf("###") + 3);
            return response.substring(0, response.lastIndexOf("###"));
        } catch (Exception e) {
            e.printStackTrace();
            return "Something went wrong";
        }
    }

    public static String uploadFilesToServer(String name, File file, String type) throws IOException {
        dataOutputStream.writeUTF("uploadFile###" + type + "\\" + name + "###" + token + "###" + session);
        dataOutputStream.flush();
        dataInputStream.readUTF();
        dataOutputStream.writeUTF("END_OF_MESSAGE");
        dataOutputStream.flush();
        byte[] array = Files.readAllBytes(Paths.get(file.getPath()));
        dataOutputStream.writeInt(array.length);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[32 * 1024 * 1024];
        int len;
        while ((len = fileInputStream.read(buffer)) > 0)
            dataOutputStream.write(buffer, 0, len);
        dataOutputStream.flush();
        fileInputStream.close();
        String response = dataInputStream.readUTF();
        session = response.substring(response.lastIndexOf("###") + 3);
        return response.substring(0, response.lastIndexOf("###"));
    }

    public static String getFileData(String name, String type) {
        new File("src/main/resources/Data/accountPhoto").mkdirs();
        new File("src/main/resources/Data/productPhoto").mkdirs();
        new File("src/main/resources/Data/productFile").mkdirs();
        try {
            String message = "getFileData###" + name + "###" + type + "###" + token + "###" + session;
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
            dataInputStream.readUTF();
            dataOutputStream.writeUTF("END_OF_MESSAGE");
            dataOutputStream.flush();
            String str = dataInputStream.readUTF();
            String fullName = str.substring(0, str.lastIndexOf("###"));
            if (fullName.startsWith("File not found"))
                throw new Exception("File not found");
            int numberOfBytes = dataInputStream.readInt();
            FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/Data/" + type + "\\" + fullName);
            byte[] buffer = new byte[32 * 1024 * 1024];
            int len;
            while (numberOfBytes > 0 && (len = dataInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, len);
                numberOfBytes -= len;
            }
            fileOutputStream.close();
            session = str.substring(str.lastIndexOf("###") + 3);
            return "src/main/resources/Data/" + type + "\\" + fullName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Something went wrong";
        }

    }

    public static  <T> T stringToObject(String string, Class<T> classOfT) {
        return new Gson().fromJson(string, classOfT);
    }

    public static <T> String objectToString(T object) {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        return gson.toJson(object);
    }

    // Security

    private final static String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789%&*+-/!?:[]{}()";

    private static String randomSaltGenerator() {
        Random random = new Random(alphabet.length());
        String sessionKey = "";
        for (int i = 0; i < 12; i++) {
            sessionKey += alphabet.charAt(random.nextInt(alphabet.length()));
        }
        return sessionKey;
    }

    public static String encryptPassword(String password) throws NoSuchAlgorithmException {
        String encryptedPassword = "";
        for (int i = 0; i < password.length(); i++) {
            int position = alphabet.indexOf(password.charAt(i));
            int newLetterPsition = ((int) (3 * position * position + Math.floor(Math.sqrt(position)) + 15)) % alphabet.length();
            char newLetter = alphabet.charAt(newLetterPsition);
            encryptedPassword += newLetter;
        }
        encryptedPassword = Hash.toHexString(Hash.getSHA(encryptedPassword));
        encryptedPassword = randomSaltGenerator() + encryptedPassword;
        return encryptedPassword;
    }

    static class Hash {
        public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(input.getBytes(StandardCharsets.UTF_8));
        }

        public static String toHexString(byte[] hash) {
            BigInteger number = new BigInteger(1, hash);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        }
    }

}
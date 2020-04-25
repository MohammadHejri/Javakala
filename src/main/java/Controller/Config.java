package Controller;

import Controller.Json.JsonFileReader;

import java.io.FileNotFoundException;

public class Config {
    private static final String configPath = "configurations.json";
    private static Config Instance;
    private String usersPath;
    private String categoryPath;

    private Config() {
        this.usersPath = "Resources/Accounts/";
        this.channelsPath = "Resources/Products";
    }

    public static Config getInstance() {
        if (Instance == null) {
            try {
                JsonFileReader jsonReader = new JsonFileReader();
                Instance = (Config) jsonReader.read("configurations.json", Config.class);
            } catch (FileNotFoundException var1) {
                Instance = new Config();
            }
        }

        return Instance;
    }

    public String getAccountsPath() {
        return usersPath;
    }

    public void setAccountsPath(String usersPath) {
        this.usersPath = usersPath;
    }

    public String getProductsPath() {
        return channelsPath;
    }

    public void setProductsPath(String channelsPath) {
        this.channelsPath = channelsPath;
    }
}

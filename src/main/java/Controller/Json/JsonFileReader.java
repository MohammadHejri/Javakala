package Controller.Json;


import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class JsonFileReader {
    private final Gson gson = new Gson();

    public JsonFileReader() {
    }

    public <T> T read(File file, Class<T> classOfT) throws FileNotFoundException {
        return this.gson.fromJson(new FileReader(file), classOfT);
    }

    public <T> T read(String filePath, Class<T> classOfT) throws FileNotFoundException {
        return this.read(new File(filePath), classOfT);
    }
}

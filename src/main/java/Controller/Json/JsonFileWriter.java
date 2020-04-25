package Controller.Json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;

public class JsonFileWriter {
    private final Gson gson = (new GsonBuilder()).excludeFieldsWithoutExposeAnnotation().create();

    public JsonFileWriter() {
    }

    public <T> void write(T object, String filePath) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write(this.gson.toJson(object));
        writer.close();
    }
}

package JavaProject.Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;

public class JsonFileWriter {

    private final Gson gson;

    public JsonFileWriter() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        gson = builder.create();
    }

    public <T> void write(String filePath, T object) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write(gson.toJson(object));
        writer.close();
    }
}

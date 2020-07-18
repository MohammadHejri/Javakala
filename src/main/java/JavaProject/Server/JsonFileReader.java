package JavaProject.Server;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JsonFileReader {

    private final Gson gson = new Gson();

    public <T> T read(File file, Class<T> classOfT) throws IOException {
        FileReader fileReader = new FileReader(file);
        T object = gson.fromJson(fileReader, classOfT);
        fileReader.close();
        return object;
    }

}

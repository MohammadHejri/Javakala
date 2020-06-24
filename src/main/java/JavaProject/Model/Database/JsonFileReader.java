package JavaProject.Model.Database;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.Gson;

public class JsonFileReader {

    private final Gson gson = new Gson();

    public <T> T read(File file, Class<T> classOfT) throws IOException {
        FileReader fileReader = new FileReader(file);
        T object = gson.fromJson(fileReader, classOfT);
        fileReader.close();
        return object;
    }

}

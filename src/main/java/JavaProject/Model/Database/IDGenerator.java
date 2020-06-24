package JavaProject.Model.Database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IDGenerator {

    public static String getGeneratedID(String type) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = new Date();
        return type + dateFormat.format(date);
    }

}

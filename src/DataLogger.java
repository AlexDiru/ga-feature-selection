import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alex on 19/11/14.
 */
public class DataLogger {

    private static String logFileName;

    public static void init() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        logFileName = "log/" + dateFormat.format(date) + ".log";
    }

    public static void writeln(String text) {
        text += "\n";

        File file = new File(logFileName);

        try {
            if (file.exists())
                FileUtils.writeStringToFile(file, text, true);
             else
                FileUtils.writeStringToFile(file, text);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

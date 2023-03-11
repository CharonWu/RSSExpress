package Tools;

import java.io.*;
import java.util.Properties;

public class RSSExpressProperties {

    private static Properties properties;

    public static void getInstance(){
        if(properties==null){
            properties = new Properties();
        }
    }

    public static void readProperties(){
        try {
            InputStream inputStream = RSSExpressProperties.class.getClassLoader().getResourceAsStream("application.properties");
            Reader reader = new InputStreamReader(inputStream);
            properties.load(reader);
            System.out.println(properties.getProperty("RSSHub"));
            inputStream.close();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

package Tools;

import java.io.*;
import java.util.Properties;

public class RSSExpressProperties {

    private static Properties properties;

    private static String RSSHUB;
    private static String CONNECTION;

    public static void getInstance(){
        if(properties==null){
            properties = new Properties();
        }
    }

    public static void readProperties(){

        readApplicaitonProperties();
        readMongoDBProperties();

    }

    private static void readApplicaitonProperties(){
        try {
            InputStream inputStream = RSSExpressProperties.class.getClassLoader().getResourceAsStream("application.properties");
            Reader reader = new InputStreamReader(inputStream);
            properties.load(reader);
            RSSHUB=properties.getProperty("RSSHub");
            inputStream.close();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void readMongoDBProperties(){
        try {
            InputStream inputStream = RSSExpressProperties.class.getClassLoader().getResourceAsStream("mongodb.properties");
            Reader reader = new InputStreamReader(inputStream);
            properties.load(reader);
            CONNECTION=properties.getProperty("connection");
            inputStream.close();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getRSSHUB(){
        return RSSHUB;
    }

    public static String getConnection(){
        return CONNECTION;
    }

}

package Tools;

import java.io.*;
import java.util.Properties;

public class RSSExpressProperties {

    private static Properties properties;

    //RSSHUB
    private static String RSSHUB;

    //MongoDB
    private static String CONNECTION;

    //Telegram
    private static long CREATER_ID;
    private static String TOKEN;
    private static String ROBOT_NAME;

    public static void getInstance(){
        if(properties==null){
            properties = new Properties();
        }
    }

    public static void readProperties(){

        readApplicaitonProperties();
        readMongoDBProperties();
        readTelegramProperties();

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
    private static void readTelegramProperties(){
        try {
            InputStream inputStream = RSSExpressProperties.class.getClassLoader().getResourceAsStream("telegram.properties");
            Reader reader = new InputStreamReader(inputStream);
            properties.load(reader);
            CREATER_ID=Long.parseLong(properties.getProperty("creater_id"));
            ROBOT_NAME=properties.getProperty("robot_name");
            TOKEN=properties.getProperty("token");
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

    public static long getCreaterId() {
        return CREATER_ID;
    }

    public static String getTOKEN() {
        return TOKEN;
    }

    public static String getRobotName() {
        return ROBOT_NAME;
    }
}

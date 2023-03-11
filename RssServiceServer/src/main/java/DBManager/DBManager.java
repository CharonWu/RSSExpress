package DBManager;

import Models.RSSContent;
import Models.RSSList;
import Tools.RSSExpressProperties;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class DBManager {


    public static void getRSSList(int onwer_id){
        connectDB();
    }

    public static void createRSSList(RSSList RSS_list){

    }

    public static void addRSSContent(RSSContent content){

    }

    public static void removeRSSContent(int index){

    }

    private static boolean connectDB(){
        try (MongoClient mongoClient = MongoClients.create(RSSExpressProperties.getConnection())) {
            List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
            databases.forEach(db -> System.out.println(db.toJson()));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

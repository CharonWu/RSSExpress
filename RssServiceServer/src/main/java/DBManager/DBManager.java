package DBManager;

import Models.RSSContent;
import Models.RSSList;
import Tools.RSSExpressProperties;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class DBManager {

    private static MongoClient mongoClient;

    public static RSSList getRSSList(int owner_id){
        connectDB();
        MongoDatabase RSSExpressDB = mongoClient.getDatabase("RSSExpress");
        MongoCollection<Document> RSS_list_collection = RSSExpressDB.getCollection("RSSLists");
        Document RSS_list_doc = RSS_list_collection.find(new Document("owner_id", owner_id)).first();
        closeDB();

        return new RSSList(owner_id, RSS_list_doc);
    }

    public static void createRSSList(RSSList RSS_list){

    }

    public static void addRSSContent(int owner_id, RSSContent content){

        connectDB();

        MongoDatabase RSSExpressDB = mongoClient.getDatabase("RSSExpress");
        MongoCollection<Document> RSS_list_collection = RSSExpressDB.getCollection("RSSLists");

        Document RSS_list_doc = RSS_list_collection.find(new Document("owner_id", owner_id)).first();

        if(RSS_list_doc==null){
            RSS_list_doc = RSSList.DBInstance(owner_id, content);
            RSS_list_collection.insertOne(RSS_list_doc);
        }else{
            Bson filter = Filters.eq("owner_id", owner_id);
            Bson update = Updates.push("RSS_list", new Document("title", content.getTitle())
                    .append("link", content.getLink())
                    .append("description", content.getDescription())
                    .append("latest_pub_date", content.getLatest_pub_date())
                    .append("item_limit", content.getItem_limit()));
            Document result = RSS_list_collection.findOneAndUpdate(filter, update);
        }

        closeDB();
    }

    public static boolean removeRSSContent(int owner_id, String link){

        connectDB();

        MongoDatabase RSSExpressDB = mongoClient.getDatabase("RSSExpress");
        MongoCollection<Document> RSS_list_collection = RSSExpressDB.getCollection("RSSLists");

        Bson filter = Filters.eq("owner_id", owner_id);
        Bson remove = Updates.pull("RSS_list", new Document("link", link));

        RSS_list_collection.findOneAndUpdate(filter, remove);

        closeDB();

        return true;
    }

    private static boolean connectDB(){
        try  {
            mongoClient = MongoClients.create(RSSExpressProperties.getConnection());
//            List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
//            databases.forEach(db -> System.out.println(db.toJson()));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void closeDB(){
        mongoClient.close();
    }


}

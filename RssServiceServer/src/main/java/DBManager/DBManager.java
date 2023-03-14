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
import kotlin.Pair;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DBManager {

    private static MongoClient mongoClient;

    public static int createAccountRelation(String account_name, String account_id) {
        connectDB();

        MongoDatabase RSSExpressDB = mongoClient.getDatabase("RSSExpress");
        MongoCollection<Document> user_id_list_collection = RSSExpressDB.getCollection(account_name + "_list");

        Document user_id_doc = user_id_list_collection.find(new Document("user_id", account_id)).first();

        int owner_id = -1;
        if (user_id_doc == null) {
            MongoCollection<Document> RSS_list_collection = RSSExpressDB.getCollection("RSSLists");
            owner_id = (int) RSS_list_collection.countDocuments();
            user_id_list_collection.insertOne(new Document("user_id", account_id).append("owner_id", owner_id));
        }

        closeDB();

        return owner_id;
    }

    public static int getAccountRelation(String account_name, String account_id) {
        connectDB();

        MongoDatabase RSSExpressDB = mongoClient.getDatabase("RSSExpress");
        MongoCollection<Document> user_id_list_collection = RSSExpressDB.getCollection(account_name + "_list");

        Document user_id_doc = user_id_list_collection.find(new Document("user_id", account_id)).first();

        int owner_id = user_id_doc.getInteger("owner_id");

        closeDB();

        return owner_id;
    }

    public static RSSList getRSSList(int owner_id) {
        connectDB();
        MongoDatabase RSSExpressDB = mongoClient.getDatabase("RSSExpress");
        MongoCollection<Document> RSS_list_collection = RSSExpressDB.getCollection("RSSLists");
        Document RSS_list_doc = RSS_list_collection.find(new Document("owner_id", owner_id)).first();
        closeDB();

        return new RSSList(owner_id, RSS_list_doc);
    }

    public static void updateRSSLists(List<RSSList> RSS_lists) {

        MongoDatabase RSSExpressDB = mongoClient.getDatabase("RSSExpress");
        MongoCollection<Document> RSS_list_collection = RSSExpressDB.getCollection("RSSLists");

        for (RSSList RSS_list : RSS_lists) {
            int owner_id = RSS_list.getOwner_id();
            List<RSSContent> contents = RSS_list.getRSSList();

            List<Document> documents = new ArrayList<>();

            for (RSSContent content : contents){
                documents.add(new Document("title", content.getTitle())
                        .append("link", content.getLink())
                        .append("description", content.getDescription())
                        .append("latest_pub_date", content.getLatest_pub_date())
                        .append("item_limit", content.getItem_limit()));
            }

            RSS_list_collection.updateOne(Filters.eq("owner_id", owner_id), Updates.set("RSS_list", documents));
        }

    }

    public static List<Pair<String, RSSList>> getUsersRSSList(String account_name) {

        MongoDatabase RSSExpressDB = mongoClient.getDatabase("RSSExpress");
        MongoCollection<Document> user_id_list_collection = RSSExpressDB.getCollection(account_name + "_list");

        ArrayList<Pair<String, Integer>> user_list = new ArrayList<>();
        for (Document doc : user_id_list_collection.find()) {
            user_list.add(new Pair(doc.get("user_id"), doc.getInteger("owner_id")));
        }

        ArrayList<Pair<String, RSSList>> RSS_lists = new ArrayList<>();
        MongoCollection<Document> RSS_list_collection = RSSExpressDB.getCollection("RSSLists");

        for (Pair<String, Integer> pair : user_list) {
            RSS_lists.add(new Pair(pair.getFirst(), new RSSList(pair.getSecond(), RSS_list_collection.find(new Document("owner_id", pair.getSecond())).first())));
        }

        return RSS_lists;
    }


    public static void addRSSContent(int owner_id, RSSContent content) {

        connectDB();

        MongoDatabase RSSExpressDB = mongoClient.getDatabase("RSSExpress");
        MongoCollection<Document> RSS_list_collection = RSSExpressDB.getCollection("RSSLists");

        Document RSS_list_doc = RSS_list_collection.find(new Document("owner_id", owner_id)).first();

        if (RSS_list_doc == null) {
            RSS_list_doc = RSSList.DBInstance(owner_id, content);
            RSS_list_collection.insertOne(RSS_list_doc);
        } else {
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

    public static boolean removeRSSContent(int owner_id, String link) {

        connectDB();

        MongoDatabase RSSExpressDB = mongoClient.getDatabase("RSSExpress");
        MongoCollection<Document> RSS_list_collection = RSSExpressDB.getCollection("RSSLists");

        Bson filter = Filters.eq("owner_id", owner_id);
        Bson remove = Updates.pull("RSS_list", new Document("link", link));

        RSS_list_collection.findOneAndUpdate(filter, remove);

        closeDB();

        return true;
    }

    public static boolean connectDB() {
        try {
            mongoClient = MongoClients.create(RSSExpressProperties.getConnection());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void closeDB() {
        mongoClient.close();
    }


}

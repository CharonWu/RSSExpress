package Models;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

import static java.util.Arrays.asList;

public class RSSList {

    private int owner_id;

    private LinkedList<RSSContent> RSS_list;


    public RSSList(int owner_id, LinkedList<RSSContent> RSS_list){
        this.owner_id=owner_id;
        this.RSS_list = RSS_list;
    }

    public RSSList(int owner_id, Document document){
        this.owner_id=owner_id;
        RSS_list = new LinkedList<>();
        for(Document RSS_content : (List<Document>)document.get("RSS_list")){
            RSS_list.add(new RSSContent(RSS_content.getString("title"), RSS_content.getString("link"), RSS_content.getString("description"), RSS_content.getString("image"), RSS_content.getInteger("item_limit"), RSS_content.getLong("latest_pub_date")));
        }
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public LinkedList<RSSContent> getRSSList() {
        return RSS_list;
    }

    public void setRSSList(LinkedList<RSSContent> RSS_list) {
        this.RSS_list = RSS_list;
    }

    public static Document DBInstance(int owner_id, RSSContent content){
        Random rand = new Random();
        Document RSS_list = new Document("_id", new ObjectId());
        RSS_list.append("owner_id", owner_id)
                .append("RSS_list", asList(new Document("title", content.getTitle())
                        .append("link", content.getLink())
                        .append("description", content.getDescription())
                        .append("latest_pub_date", content.getLatest_pub_date())
                        .append("item_limit", content.getItem_limit())));

        return RSS_list;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        int index = 1;
        for(RSSContent content: RSS_list){
            sb.append(index++);
            sb.append(". ");
            sb.append(content.getTitle());
            sb.append('\n');
            sb.append(content.getLink());
            sb.append('\n');
            sb.append("Description: \n");
            sb.append(content.getDescription());
            sb.append('\n');
            sb.append("Last update time: ");
            sb.append(new Date(content.getLatest_pub_date()));
            sb.append('\n');
        }

        return sb.toString();
    }
}

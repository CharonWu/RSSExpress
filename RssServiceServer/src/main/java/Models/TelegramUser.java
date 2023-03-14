package Models;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Random;

public class TelegramUser extends RSSUser {

    private long telagram_id;

    public TelegramUser(long telagram_id, int owner_id){
        this.telagram_id=telagram_id;
        this.owner_id=owner_id;
    }

    public long getTelagram_id() {
        return telagram_id;
    }

    public void setTelagram_id(long telagram_id) {
        this.telagram_id = telagram_id;
    }

    public static Document DBInstance(int owner_id, long telagram_id){
        Random rand = new Random();
        Document telegram_user = new Document("_id", new ObjectId());
        telegram_user.append("owner_id", owner_id)
                .append("telagram_id", telagram_id);

        return telegram_user;
    }
}

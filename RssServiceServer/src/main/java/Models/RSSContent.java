package Models;

import Tools.TimeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.ArrayList;

public class RSSContent {
    private String title;
    private String link;
    private String description;
    private String image;
    private int item_limit;

    private long latest_pub_date;

    private String latest_link;

    public RSSContent(Document document, String link){
//        Document document = Jsoup.parse(content, Parser.xmlParser());

        title = document.getElementsByTag("title").first().text();
        this.link = link;
        description = document.getElementsByTag("description").first().text();

        Elements itemList = document.getElementsByTag("item");
        latest_link = itemList.first().getElementsByTag("link").first().text();
        try {
            latest_pub_date = TimeUtil.format(itemList.first().getElementsByTag("pubDate").first().text()).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }

    public RSSContent(String title, String link, String description, String image, int item_limit, long latest_pub_date){
        this.title = title;
        this.link = link;
        this.description = description;
        this.image = image;
        this.item_limit = item_limit;
        this.latest_pub_date = latest_pub_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getItem_limit() {
        return item_limit;
    }

    public void setItem_limit(int item_limit) {
        this.item_limit = item_limit;
    }

    public long getLatest_pub_date() {
        return latest_pub_date;
    }

    public void setLatest_pub_date(long latest_pub_date) {
        this.latest_pub_date = latest_pub_date;
    }

    public String getLatest_link() {
        return latest_link;
    }
}

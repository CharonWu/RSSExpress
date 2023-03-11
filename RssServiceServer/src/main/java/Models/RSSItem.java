package Models;

public class RSSItem {

    private String title;
    private String link;
    private long pub_date;

    public RSSItem(String title, String link, long pub_date){
        this.title=title;
        this.link=link;
        this.pub_date=pub_date;
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

    public long getPub_date() {
        return pub_date;
    }

    public void setPub_date(long pub_date) {
        this.pub_date = pub_date;
    }
}

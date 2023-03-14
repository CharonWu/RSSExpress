package RSSAdapter;

import DBManager.DBManager;
import Models.RSSContent;
import Models.RSSItem;
import Models.RSSList;
import Robot.RSSRobot;
import Robot.TelegramRSSRobot;
import Tools.RSSExpressProperties;
import Tools.TimeUtil;
import kotlin.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class RSSAdapter {


    LinkedList<RSSRobot> robots;

    public RSSAdapter() {

        robots = new LinkedList<>();

        RSSExpressProperties.getInstance();
        RSSExpressProperties.readProperties();
        startRobots();

    }

    private void startRobots() {
        TelegramBotsApi telegramBotsApi;
        TelegramRSSRobot telegram_RSS_robot = new TelegramRSSRobot(3600, this);

        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegram_RSS_robot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        robots.add(telegram_RSS_robot);
        robots.getFirst().startRobot();
    }

    public RSSList getRSSList(int owner_id) {
        return DBManager.getRSSList(owner_id);
    }

    public void emptyRSSList(int owner_id) {

    }

    public RSSItem subscribe(int owner_id, String link) {

        URL url;
        HttpURLConnection con;
        try {
            if (link.startsWith("rsshub")) {
                url = new URL(RSSExpressProperties.getRSSHUB() + link.substring(6));
            } else if (link.startsWith("/rsshub")) {
                url = new URL(RSSExpressProperties.getRSSHUB() + link.substring(7));
            } else {
                url = new URL(link);
            }

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            Document document = Jsoup.parse(content.toString(), Parser.xmlParser());

            RSSContent Rss_content = new RSSContent(document, link);
            DBManager.addRSSContent(owner_id, Rss_content);

            Element item = document.getElementsByTag("item").first();
            String item_title = item.getElementsByTag("title").first().text();
            String item_link = item.getElementsByTag("link").first().text();
            long item_pub_date = TimeUtil.format(item.getElementsByTag("pubDate").text()).getTime();

            RSSItem RSS_item = new RSSItem(item_title, item_link, item_pub_date);

            return RSS_item;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    public boolean unsubscribe(int owner_id, String link) {
        return DBManager.removeRSSContent(owner_id, link);
    }

    public List<Pair<String, LinkedList<RSSItem>>> getRSSUpdate(String account_name){
        DBManager.connectDB();
        List<Pair<String, RSSList>> RSS_lists = DBManager.getUsersRSSList(account_name);

        List<RSSList> RSS_updates = new LinkedList<>();
        List<Pair<String, LinkedList<RSSItem>>> sent_to_users_RSS = new LinkedList<>();

        //check each user's RSS list
        for(Pair<String, RSSList> pair: RSS_lists){
            String user_id = pair.getFirst();
            RSSList list = pair.getSecond();

            LinkedList<RSSContent> updated_RSS_contents = new LinkedList<>();
            LinkedList<RSSItem> sent_to_user_RSS = new LinkedList<>();

            //check each RSS in the RSS list
            for(RSSContent content: list.getRSSList()){
                String link = content.getLink();
                Document document = Jsoup.parse(getRSSContentFromLink(link), Parser.xmlParser());

                RSSContent latest_content = new RSSContent(document, link);

                if(latest_content.getLatest_pub_date()>content.getLatest_pub_date()){
                    updated_RSS_contents.add(latest_content);

                    for(Element item : document.getElementsByTag("item")){
                        try {
                            long item_pub_date = TimeUtil.format(item.getElementsByTag("pubDate").text()).getTime();
                            if(item_pub_date>content.getLatest_pub_date()){
                                String item_title = item.getElementsByTag("title").first().text();
                                String item_link = item.getElementsByTag("link").first().text();
                                sent_to_user_RSS.add(new RSSItem(item_title, item_link, item_pub_date));
                            }else {
                                break;
                            }
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }else {
                    updated_RSS_contents.add(content);
                }
            }
            RSSList updated_RSS_list = new RSSList(list.getOwner_id(), updated_RSS_contents);
            RSS_updates.add(updated_RSS_list);
            sent_to_users_RSS.add(new Pair(user_id, sent_to_user_RSS));
        }

        DBManager.updateRSSLists(RSS_updates);
        DBManager.closeDB();

        return sent_to_users_RSS;
    }

    private String getRSSContentFromLink(String link){
        URL url;
        HttpURLConnection con;
        try {
            if (link.startsWith("rsshub")) {
                url = new URL(RSSExpressProperties.getRSSHUB() + link.substring(6));
            } else if (link.startsWith("/rsshub")) {
                url = new URL(RSSExpressProperties.getRSSHUB() + link.substring(7));
            } else {
                url = new URL(link);
            }

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

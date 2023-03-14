package RSSAdapter;

import DBManager.DBManager;
import Models.RSSContent;
import Models.RSSList;
import Robot.RSSRobot;
import Robot.TelegramRSSRobot;
import Tools.RSSExpressProperties;
import kotlin.Pair;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public RSSContent subscribe(int owner_id, String link) {

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

            RSSContent Rss_content = new RSSContent(content.toString(), link);
            DBManager.addRSSContent(owner_id, Rss_content);

            return Rss_content;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    public boolean unsubscribe(int owner_id, String link) {
        return DBManager.removeRSSContent(owner_id, link);
    }

    public List<Pair<String, RSSList>> getRSSUpdate(String account_name){
        DBManager.connectDB();
        List<Pair<String, RSSList>> RSS_lists = DBManager.getUsersRSSList(account_name);

        List<Pair<String, RSSList>> RSS_updates = new LinkedList<>();

        for(Pair<String, RSSList> pair: RSS_lists){
            String user_id = pair.getFirst();
            RSSList list = pair.getSecond();
            LinkedList<RSSContent> updated_RSS_contents = new LinkedList<>();
            for(RSSContent content: list.getRSSList()){
                String link = content.getLink();
                RSSContent latest_content = new RSSContent(getRSSContentFromLink(link), link);
                if(latest_content.getLatest_pub_date()>content.getLatest_pub_date()){
                    updated_RSS_contents.add(latest_content);
                }else {
                    updated_RSS_contents.add(content);
                }
            }
            RSSList updated_RSS_list = new RSSList(list.getOwner_id(), updated_RSS_contents);
            RSS_updates.add(new Pair(user_id, updated_RSS_list));
        }

        DBManager.updateRSSLists(RSS_updates);

        DBManager.closeDB();
        return RSS_updates;
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

package RSSAdapter;

import DBManager.DBManager;
import Models.RSSContent;
import Models.RSSList;
import Tools.RSSExpressProperties;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RSSAdapter {



    public RSSAdapter(){

        RSSExpressProperties.getInstance();
        RSSExpressProperties.readProperties();

    }

    public RSSList getRSSList(int owner_id){

        DBManager.getRSSList(owner_id);

        return null;
    }

    public void emptyRSSList(int owner_id){

    }

    public RSSContent subscribe(int owner_id, String link){


        URL url;
        HttpURLConnection con;
        try{
            if(link.startsWith("rsshub")){
                url =new URL(RSSExpressProperties.getRSSHUB()+link.substring(6));
            }else if(link.startsWith("/rsshub")){
                url =new URL(RSSExpressProperties.getRSSHUB()+link.substring(7));
            }else{
                url =new URL(link);
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

            return new RSSContent(content.toString(), link);
        }catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }

    public void unsubscribe(int owner_id, int index){

    }
}

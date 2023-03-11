package RSSController;

import Models.RSSContent;
import Models.RSSList;
import Tools.RSSExpressProperties;

public class RSSController {



    public RSSController(){

        RSSExpressProperties.getInstance();
        RSSExpressProperties.readProperties();

    }

    public RSSList getRSSList(int owner_id){

        return null;
    }

    public void emptyRSSList(int owner_id){

    }

    public RSSContent subscribe(int owner_id, String link){
        return null;
    }

    public void unsubscribe(int owner_id, int index){

    }
}

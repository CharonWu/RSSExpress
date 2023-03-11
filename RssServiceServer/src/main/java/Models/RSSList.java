package Models;

import DBManager.DBManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class RSSList {

    private int owner_id;

    private LinkedList<RSSContent> RSS_list;


    public RSSList(int owner_id){
        this.owner_id=owner_id;
        RSS_list = new LinkedList<>();
    }

    public void createRSSList(){


        DBManager.createRSSList(this);
    }

    public boolean addRSSContent(RSSContent content){
        for(RSSContent c:RSS_list){
            if(Objects.equals(c.getLink(), content.getLink())){
                return false;
            }
        }
        RSS_list.add(content);
        DBManager.addRSSContent(content);
        return true;
    }

    public boolean removeRSSContent(int index){
        if(index<0||index>=RSS_list.size())
            return false;
        RSS_list.remove(index);
        DBManager.removeRSSContent(index);
        return true;
    }


    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }
}

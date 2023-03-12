package RSSExpress;

import RSSAdapter.RSSAdapter;
import com.google.gson.Gson;

import static spark.Spark.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        RSSAdapter rssAdapter = new RSSAdapter();
        Gson gson = new Gson();

        //return home page of RSSReader
        get("/", (req, res)->{
            return "RSS home";
        });

        //get Rss subscribe list
        get("/rsslist/:owner_id", (req, res)->{
            return gson.toJson(rssAdapter.getRSSList(Integer.parseInt(req.params(":owner_id"))));
        });

        //empty Rss subscribe list
        delete("/rsslist", (req, res)->{
            return "delete rsslist";
        });

        //subscribe new Rss content
        post("/subscribe", (req, res)->{
            return gson.toJson(rssAdapter.subscribe(Integer.parseInt(req.queryParams("owner_id")), req.queryParams("link")));
        });

        //unsubscribe new Rss content
        patch("/unsubscribe", (req, res)->{
            return gson.toJson(rssAdapter.unsubscribe(Integer.parseInt(req.queryParams("owner_id")), req.queryParams("link")));
        });
    }
}

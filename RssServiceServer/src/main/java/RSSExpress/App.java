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

        get("/hello", (req, res) -> {
            return "Hello World, RSSExpress";
        });

        //return home page of RSSReader
        get("/", (req, res)->{
            return "RSS home";
        });

        //get Rss subscribe list
        get("/rsslist", (req, res)->{
            rssAdapter.getRSSList(0);
            return "get rsslist";
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
        delete("/unsubscribe", (req, res)->{
            return "unsub";
        });
    }
}

package RSSExpress;

import RSSController.RSSController;

import static spark.Spark.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        RSSController RSS_controller = new RSSController();

        get("/hello", (req, res) -> {
            return "Hello World, RSSExpress";
        });

        //return home page of RSSReader
        get("/", (req, res)->{
            return "RSS home";
        });

        //get Rss subscribe list
        get("/rsslist", (req, res)->{
            return "get rsslist";
        });

        //empty Rss subscribe list
        delete("/rsslist", (req, res)->{
            return "delete rsslist";
        });

        //subscribe new Rss content
        post("/subscribe", (req, res)->{
            return "sub";
        });

        //unsubscribe new Rss content
        delete("/unsubscribe", (req, res)->{
            return "unsub";
        });
    }
}

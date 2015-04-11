import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.Bot;
import stream.BroadcastQueue;
import stream.LikeUpdate;
import tinder.TinderManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static spark.Spark.*;

public class Main {

    public static final String SUCCESS = "{\"success\": true}";
    public static final String FAILURE = "{\"success\": false}";

    public static void main(String[] kittensOnFire) throws SQLException, ClassNotFoundException {

        ScheduledExecutorService jobPool = Executors.newScheduledThreadPool(16);
        BroadcastQueue bQueue = new BroadcastQueue(jobPool);
        staticFileLocation("/public");

        Class.forName("org.postgresql.Driver");
        Connection dbConnection = DriverManager.getConnection("jdbc:postgresql://ec2-54-163-225-82.compute-1.amazonaws.com:5432/d22fcq21bok1ph?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", "efdsfdamscmvwq", "TDoX-BcUdEaLSTHqCbFWtNe3ZO");
        new TinderManager(jobPool, Bot.all(dbConnection), bQueue, dbConnection);


        before((req, res) -> res.type("text/json"));

        post("/stream", (req, res) -> {
            bQueue.addRequest(req);
            return null;
        });

        post("/:bot/message/:id", (req, res) -> {
            // Check if valid
            if(false) {
                halt(400, FAILURE);
            }
            // Send message
            //jobPool.submit(()-> )
            return SUCCESS;
        });

        put("/:bot/location", (req, res) -> {
            // Change thing
            return SUCCESS;
        });

        get("/all", (req, res) -> {
            JsonObject response = new JsonObject();
            JsonArray bots = new JsonArray();
            Bot.all(dbConnection).forEach((bot) -> bots.add(bot.toJsonTree()));
            response.add("bots", bots);
            return response.toString();
        });
    }
}

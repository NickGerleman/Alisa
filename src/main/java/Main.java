import stream.BroadcastQueue;
import stream.LikeUpdate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static spark.Spark.*;

public class Main {

    public static final String SUCCESS = "{\"success\": true}";
    public static final String FAILURE = "{\"success\": false}";

    public static void main(String[] kittensOnFire) {

        ScheduledExecutorService jobPool = Executors.newScheduledThreadPool(16);
        BroadcastQueue bQueue = new BroadcastQueue(jobPool);
        staticFileLocation("/public");

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
            return "{}";
        });


        for (; ; ) {
            bQueue.broadcastUpdate(new LikeUpdate("Amanda White", "Aaron", "http://images.gotinder.com/518d666a2a00df0e490000b9/84x84_pct_0_29.5312464_540_540_5c1d3231-5a75-4a07-91ff-5c012716583f.jpg"));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

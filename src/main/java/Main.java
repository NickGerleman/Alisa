import stream.BroadcastQueue;
import stream.SampleUpdate;

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
            bQueue.broadcastUpdate(new SampleUpdate());
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

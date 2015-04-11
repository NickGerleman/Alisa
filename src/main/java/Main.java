import stream.BroadcastQueue;
import stream.SampleUpdate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static spark.Spark.*;

public class Main {
    public static void main(String[] kittensOnFire) {
        ScheduledExecutorService jobPool = Executors.newScheduledThreadPool(16);
        BroadcastQueue bQueue = new BroadcastQueue(jobPool);
        staticFileLocation("/public");

        post("/stream", (req, res) -> {
            bQueue.addRequest(req);
            return null;
        });

        for (;;) {
            bQueue.broadcastUpdate(new SampleUpdate());
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

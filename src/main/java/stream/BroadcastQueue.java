package stream;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.JsonModel;
import spark.Request;
import util.ConcurrentTimeoutMap;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Manages connections and streaming updates to them
 */
public class BroadcastQueue {
    private ExecutorService jobPool;
    private ConcurrentTimeoutMap<String, Session> sessions = new ConcurrentTimeoutMap<>();
    private ConcurrentLinkedDeque<JsonModel> broadcastQueue = new ConcurrentLinkedDeque<>();
    private Optional<Future> broadcastFuture = Optional.empty();

    public BroadcastQueue(ExecutorService jobPool) {
        this.jobPool = jobPool;
    }

    public void addRequest(Request request) {
        String id = request.session(true).id();
        if (sessions.containsKey(id)) {
            sessions.get(id).setReq(request);
            sessions.changeTimeout(id, Duration.ofMinutes(30));
        } else {
            sessions.putWithTimeout(id, new Session(id, request), Duration.ofMinutes(30));
        }
    }

    public void broadcastUpdate(JsonModel update) {
        broadcastQueue.addLast(update);
        if (!broadcastFuture.isPresent() || broadcastFuture.get().isDone()) {
            sendBroadcast();
        }
    }

    private void sendBroadcast() {
        broadcastFuture = Optional.of(jobPool.submit(() -> {
            while (!broadcastQueue.isEmpty()) {
                JsonModel toBroadcast = broadcastQueue.pollFirst();
                for (Session session : sessions.values()) {
                    session.queueOrSendUpdate(toBroadcast);
                }
            }
        }));
    }

    private class Session {
        private final String sessionId;
        private final Deque<JsonModel> updateQueue = new ConcurrentLinkedDeque<>();
        private AsyncContext context;
        private int connCount = 0;

        public Session(String sessionId, Request req) {
            this.sessionId = sessionId;
            setReq(req);
        }

        public void setReq(Request req) {
            connCount++;
            this.context = req.raw().startAsync();
            context.getResponse().setContentType("text/json");
            if (!updateQueue.isEmpty()) {
                sendUpdates();
            }
            context.addListener(new AsyncListener() {
                @Override
                public void onComplete(AsyncEvent asyncEvent) throws IOException {
                    startTimeout();
                }

                @Override
                public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                    sendUpdates();
                }

                @Override
                public void onError(AsyncEvent asyncEvent) throws IOException {
                    startTimeout();
                }

                @Override
                public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
                }

                private void startTimeout() {
                    connCount--;
                    sessions.changeTimeout(sessionId, Duration.ofSeconds(30));
                }
            });
        }

        public void queueOrSendUpdate(JsonModel update) {
            updateQueue.addLast(update);
            if (connCount != 0) {
                sendUpdates();
            }
        }

        private void sendUpdates() {
            JsonObject response = new JsonObject();
            JsonArray updates = new JsonArray();
            while (!updateQueue.isEmpty()) {
                updates.add(new Gson().toJsonTree(updateQueue.pollFirst()));
            }
            response.add("updates", updates);

            try {
                PrintWriter writer = context.getResponse().getWriter();
                writer.println(response.toString());
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            context.complete();
        }
    }
}

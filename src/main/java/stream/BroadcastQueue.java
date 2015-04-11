package stream;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
    private ConcurrentLinkedDeque<Update> broadcastQueue = new ConcurrentLinkedDeque<>();
    private Optional<Future> broadcastFuture = Optional.empty();

    public BroadcastQueue(ExecutorService jobPool) {
        this.jobPool = jobPool;
    }

    public void addRequest(Request request) {
        String id = request.session(true).id();
        if (sessions.containsKey(id)) {
            sessions.get(id).setReq(request);
        } else {
            sessions.putWithTimeout(id, new Session(id, request), Duration.ofMinutes(30));
        }
    }

    public void broadcastUpdate(Update update) {
        broadcastQueue.addLast(update);
        if (!broadcastFuture.isPresent() || broadcastFuture.get().isDone()) {
            sendBroadcast();
        }
    }

    private void sendBroadcast() {
        broadcastFuture = Optional.of(jobPool.submit(() -> {
            while (!broadcastQueue.isEmpty()) {
                Update toBroadcast = broadcastQueue.pollFirst();
                for (Session session : sessions.values()) {
                    session.queueOrSendUpdate(toBroadcast);
                }
            }
        }));
    }

    private class Session {
        private final String sessionId;
        private final Deque<Update> updateQueue = new ArrayDeque<>();
        private AsyncContext context;

        public Session(String sessionId, Request req) {
            this.sessionId = sessionId;
            setReq(req);
        }

        public void setReq(Request req) {
            this.context = req.raw().startAsync();
            context.getResponse().setContentType("text/json");
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
                    if (!updateQueue.isEmpty()) {
                        sendUpdates();
                    }
                }

                private void startTimeout() {
                    context = null;
                    sessions.putWithTimeout(sessionId, Session.this, Duration.ofSeconds(30));
                }
            });
        }

        public void queueOrSendUpdate(Update update) {
            updateQueue.addLast(update);
            if (context != null) {
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

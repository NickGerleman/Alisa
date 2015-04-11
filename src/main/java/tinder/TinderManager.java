package tinder;

import model.Bot;
import model.JsonModel;
import model.Photo;
import model.User;
import stream.BroadcastQueue;
import stream.LikeUpdate;
import stream.MatchUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TinderManager {
    private ScheduledExecutorService jobPool;
    private BroadcastQueue bQueue;

    public TinderManager(ScheduledExecutorService jobPool, List<Bot> bots, BroadcastQueue bQueue, Connection connection) {
        this.jobPool = jobPool;
        this.bQueue = bQueue;

        bots.forEach((bot) -> jobPool.schedule(() -> {
            CleverbotProfile profile = new CleverbotProfile(bot.getName(), bot.getAuthToken());
            profile.autoLike().forEach((user) -> {
                if (user.matched) {
                    try {
                        addMatch(bot, user, connection);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                Photo mainPhoto = user.getPhotos().get(0);
                for (Photo photo : user.getPhotos()) {
                    if (photo.isMain()) {
                        mainPhoto = photo;
                    }
                }
                JsonModel likeUpdate = new LikeUpdate(user.getName(), bot.getId(), mainPhoto.getUrl84());
                bQueue.broadcastUpdate(likeUpdate);
            });
        }, 30, TimeUnit.SECONDS));

        bots.forEach((bot) -> {
            // GET UPDATES
        });

    }

    private void addMatch(Bot bot, OtherUser user, Connection conn) throws SQLException {
        PreparedStatement smt = conn.prepareStatement("INSERT INTO match(bot_id, user_id) VALUES (?, ?)");
        smt.setInt(1, bot.getId());
        smt.setString(2, user.getId());
        smt.execute();
        User jsonUser = new User(user.getId(), user.getName(), user.getGenderNumber(), 18);
        jsonUser.retrievePhotos(conn);
        bQueue.broadcastUpdate(new MatchUpdate(bot.getId(), jsonUser));
    }
}

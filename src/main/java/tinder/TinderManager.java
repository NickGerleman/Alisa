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
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
        int age = (int)(Duration.between(Instant.parse(user.getBirthday()), Instant.now()).get(ChronoUnit.SECONDS) / 60 / 60 / 24 / 365);
        PreparedStatement smt = conn.prepareStatement("INSERT INTO \"user\"(id, gender, age, name) VALUES(?, ?, ?, ?)");
        smt.setString(1, user.getId());
        smt.setInt(2, user.getGenderNumber());
        smt.setInt(3, age);
        smt.setString(4, user.getName());
        smt.execute();
        for (Photo photo : user.getPhotos()) {
            smt = conn.prepareStatement("INSERT INTO photo(id, user_id, main, url_640, url_320, url_172, url_84) VALUES (?, ?, ?, ?, ?, ?, ?)");
            smt.setString(1, photo.getId());
            smt.setString(2, user.getId());
            smt.setBoolean(3, photo.isMain());
            smt.setString(4, photo.getUrl640());
            smt.setString(5, photo.getUrl320());
            smt.setString(6, photo.getUrl172());
            smt.setString(7, photo.getUrl84());
            smt.execute();
        }
        smt = conn.prepareStatement("INSERT INTO match(bot_id, user_id) VALUES (?, ?)");
        smt.setInt(1, bot.getId());
        smt.setString(2, user.getId());
        smt.execute();
        User jsonUser = new User(user.getId(), user.getName(), user.getGenderNumber(), age);
        jsonUser.retrievePhotos(conn);
        bQueue.broadcastUpdate(new MatchUpdate(bot.getId(), jsonUser));
    }
}

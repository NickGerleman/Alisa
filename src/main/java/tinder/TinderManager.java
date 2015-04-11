package tinder;

import model.Bot;
import model.JsonModel;
import model.Photo;
import model.User;
import stream.BroadcastQueue;
import stream.LikeUpdate;
import stream.MatchUpdate;

import java.sql.*;
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
                try {
                    addUser(user, connection);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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

        bots.forEach((bot) -> jobPool.schedule(() -> {
            try {
                Timestamp lastUpdated = getTimestamp(connection, bot.getId());
                if (lastUpdated == null) {
                    lastUpdated = Timestamp.from(Instant.now().minus(Duration.ofDays(7)));
                }
                CleverbotProfile profile = new CleverbotProfile(bot.getName(), bot.getAuthToken());
                List<Update> updates = profile.getUpdates(lastUpdated.toInstant().toString());
                for (Update update : updates) {
                    try {
                        String theirId = update.getId().replace(bot.getTinderId(), "");
                        User user = User.get(theirId, connection);
                        // Fix Later
                        if (user == null) {
                            continue;
                        }
                        OtherUser otherUser = new OtherUser(
                                user.getId(),
                                user.getGender(),
                                user.getName(),
                                user.getPhotos(),
                                Instant.now().minus(user.getAge() * 60 * 60 * 24 * 365, ChronoUnit.SECONDS).toString()
                        );
                        addMatch(bot, otherUser, connection);
                        for (Message message : update.getMessage()) {
                            addMessage(message, connection);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                updateTimestamp(connection, bot.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 6, TimeUnit.SECONDS));

        bots.forEach((bot) -> jobPool.scheduleWithFixedDelay(() -> {
            Records botRecords = Tinder.parseAllUpdates(Tinder.getAuthToken(bot.getAuthToken()));
            botRecords.getUsers().forEach((otherUser) -> {
                try {
                    addUser(otherUser, connection);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            botRecords.getMessages().forEach((message) -> {
                try {
                    addMessage(message, connection);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }, 0, 10, TimeUnit.MINUTES));


    }

    private void addMessage(Message message, Connection conn) throws SQLException {
        PreparedStatement smt = conn.prepareStatement("SELECT * FROM message WHERE id = ?");
        smt.setString(1, message.getMessageID());
        if (smt.execute()) {
            return;
        }
        smt = conn.prepareStatement("INSERT INTO message(id, text, \"timestamp\", from_id, to_id) VALUES (?, ?, ?, ?, ?)" );
        smt.setString(1, message.getMessageID());
        smt.setString(2, message.getMessage());
        smt.setTimestamp(3, new Timestamp(message.getTimestamp()));
        smt.setString(4, message.getFromID());
        smt.setString(5, message.getToID());
        smt.execute();
        model.Message updateMessage = new model.Message("1", message.getFromID(), message.getToID(), message.getMessage(), new Timestamp(message.getTimestamp()));
        bQueue.broadcastUpdate(updateMessage);
    }

    private void addUser(OtherUser user, Connection conn) throws SQLException {
        PreparedStatement smt = conn.prepareStatement("SELECT * FROM \"user\" WHERE id = ?");
        smt.setString(1, user.getId());
        if (smt.execute()) {
            return;
        }
        int age = (int)(Duration.between(Instant.parse(user.getBirthday()), Instant.now()).get(ChronoUnit.SECONDS) / 60 / 60 / 24 / 365);
        smt = conn.prepareStatement("INSERT INTO \"user\"(id, gender, age, name) VALUES(?, ?, ?, ?)");
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
    }

    private void addMatch(Bot bot, OtherUser user, Connection conn) throws SQLException {
        PreparedStatement smt = conn.prepareStatement("SELECT * FROM match where bot_id = ? AND user_id = ?");
        smt.setInt(1, bot.getId());
        smt.setString(2, user.getId());
        if (smt.execute()) {
            return;
        }
        int age = (int)(Duration.between(Instant.parse(user.getBirthday()), Instant.now()).get(ChronoUnit.SECONDS) / 60 / 60 / 24 / 365);
        addUser(user, conn);
        smt = conn.prepareStatement("INSERT INTO match(bot_id, user_id) VALUES (?, ?)");
        smt.setInt(1, bot.getId());
        smt.setString(2, user.getId());
        smt.execute();
        User jsonUser = new User(user.getId(), user.getName(), user.getGenderNumber(), age);
        jsonUser.retrievePhotos(conn);
        bQueue.broadcastUpdate(new MatchUpdate(bot.getId(), jsonUser));
    }

    private void updateTimestamp(Connection conn, int botId) throws SQLException {
        PreparedStatement smt = conn.prepareStatement("UPDATE \"update\" SET time=current_timestamp WHERE bot_id = ?");
        smt.setInt(1, botId);
        if (!smt.execute()) {
            smt = conn.prepareStatement("INSERT INTO \"update\"(time, bot_id) VALUES (CURRENT_TIMESTAMP , ?)");
            smt.setInt(1, botId);
            smt.execute();
        }
    }

    private Timestamp getTimestamp(Connection conn, int botId) throws SQLException {
        PreparedStatement smt = conn.prepareStatement("SELECT * FROM \"update\" WHERE bot_id = ? ORDER BY \"time\" DESC LIMIT 1");
        smt.setInt(1, botId);
        ResultSet rs = smt.executeQuery();
        if (!rs.next()) {
            return null;
        }
        return rs.getTimestamp("time");
    }
}

package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Bot extends JsonModel {
    private final String name;
    private final int id;
    private final String tinderId;
    private final double latitude;
    private final double longitude;
    private List<User> matchedUsers;
    private final transient String authToken;

    public Bot(int id, String name, String authToken, Double latitude, Double longitude, String tinderId) {
        this.id = id;
        this.name = name;
        this.authToken = authToken;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tinderId = tinderId;
    }

    public static List<Bot> all(Connection conn) throws SQLException {
        ArrayList<Bot> bots = new ArrayList<>();
        Statement smt = conn.createStatement();
        ResultSet results = smt.executeQuery("SELECT * FROM bot");
        while (results.next()) {
            bots.add(new Bot(
                    results.getInt("id"),
                    results.getString("name"),
                    results.getString("auth_token"),
                    results.getDouble("latitude"),
                    results.getDouble("longitude"),
                    results.getString("tinder_id")
                    ));
        }
        for (Bot bot : bots) {
            bot.retrieveMatches(conn);
        }
        return bots;
    }

    public static Bot get(Connection conn, int id) throws SQLException {
        Bot bot = null;
        PreparedStatement smt = conn.prepareStatement("SELECT * FROM bot WHERE bot.id = ?");
        smt.setInt(1, id);
        ResultSet results = smt.executeQuery();
        if (results.next()) {
            bot = (new Bot(
                    results.getInt("id"),
                    results.getString("name"),
                    results.getString("auth_token"),
                    results.getDouble("latitude"),
                    results.getDouble("longitude"),
                    results.getString("tinder_id")
            ));
        }
        if (bot!= null) {
            bot.retrieveMatches(conn);
        }
        return bot;
    }

    public void retrieveMatches(Connection conn) throws SQLException {
        matchedUsers = new ArrayList<>();
        PreparedStatement smt = conn.prepareStatement("SELECT * FROM \"user\" INNER JOIN match on match.user_id = \"user\".id WHERE match.bot_id = ? ORDER BY \"user\".name");
        smt.setInt(1, id);
        ResultSet rs = smt.executeQuery();
        while (rs.next()) {
            matchedUsers.add(new User(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getInt("gender"),
                    rs.getInt("age")
            ));
        }
        for (User user : matchedUsers) {
            user.retrievePhotos(conn);
            user.retrieveMessages(conn);
        }
    }

    public String getName() {
        return name;
    }

    public String getAuthToken() {
        return authToken;
    }

    public int getId() {
        return id;
    }

    public String getTinderId() {
        return tinderId;
    }
}

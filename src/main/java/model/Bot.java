package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Bot extends JsonModel {
    private String name;
    private int id;
    private List<User> matchedUsers;

    public Bot(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<Bot> all(Connection conn) throws SQLException {
        ArrayList<Bot> bots = new ArrayList<>();
        Statement smt = conn.createStatement();
        ResultSet results = smt.executeQuery("SELECT * FROM bot");
        while (results.next()) {
            bots.add(new Bot(
                    results.getInt("id"),
                    results.getString("name")
                    ));
        }
        for (Bot bot : bots) {
            bot.retrieveMatches(conn);
        }
        return bots;
    }

    public void retrieveMatches(Connection conn) throws SQLException {
        matchedUsers = new ArrayList<>();
        PreparedStatement smt = conn.prepareStatement("SELECT * FROM \"user\" INNER JOIN match on match.user_id = \"user\".id WHERE match.bot_id = ?");
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
    }
}

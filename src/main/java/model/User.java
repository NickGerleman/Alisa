package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class User extends JsonModel {

    private final String id;
    private final String name;
    private final int gender;
    private final int age;
    private List<Photo> photos;
    private List<Message> messages;

    public User(String id, String name, int gender, int age) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    public void retrievePhotos(Connection conn) throws SQLException {
        photos = new ArrayList<>();
        PreparedStatement smt = conn.prepareStatement("SELECT * FROM photo WHERE user_id = ?");
        smt.setString(1, id);
        ResultSet rs = smt.executeQuery();
        while (rs.next()) {
            photos.add(new Photo(
                    rs.getString("id"),
                    rs.getString("user_id"),
                    rs.getBoolean("main"),
                    rs.getString("url_640"),
                    rs.getString("url_320"),
                    rs.getString("url_172"),
                    rs.getString("url_84")
            ));
        }
    }

    public void retrieveMessages(Connection conn) throws SQLException {
        messages = new ArrayList<>();
        PreparedStatement smt = conn.prepareStatement("SELECT * FROM message WHERE from_id = ? OR to_id = ? ORDER BY timestamp");
        smt.setString(1, id);
        smt.setString(2, id);
        ResultSet rs = smt.executeQuery();
        while (rs.next()) {
            messages.add(new Message(
                    rs.getString("id"),
                    rs.getString("from_id"),
                    rs.getString("to_id"),
                    rs.getString("text"),
                    rs.getTimestamp("timestamp")
            ));
        }
    }

    public static User get(String id, Connection conn) throws SQLException {
        PreparedStatement smt = conn.prepareStatement("SELECT * FROM \"user\" WHERE id = ?");
        smt.setString(1, id);
        ResultSet rs = smt.executeQuery();
        if (rs.next()) {
            return new User(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getInt("gender"),
                    rs.getInt("age")
            );
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public List<Message> getMessages() {
        return messages;
    }
}

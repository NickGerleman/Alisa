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

    public void retrieveMessages(Connection conn) {

    }
}

package model;

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

    public void retrievePhotos() {

    }
}

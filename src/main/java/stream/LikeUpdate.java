package stream;

import model.JsonModel;

/**
 * Signifies a user was liked
 */
public class LikeUpdate extends JsonModel {
    private final String type = "like";
    private final String name;
    private final String image;
    private final String bot;

    public LikeUpdate(String name, String bot, String image) {
        this.name = name;
        this.bot = bot;
        this.image = image;
    }
}

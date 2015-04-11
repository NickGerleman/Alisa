package stream;

import model.JsonModel;
import model.User;

/**
 * Created by nick on 4/11/15.
 */
public class MatchUpdate extends JsonModel {
    private final String type = "like";
    private final int bot;
    private final User user;

    public MatchUpdate(int bot, User user) {
        this.bot = bot;
        this.user = user;
    }

}

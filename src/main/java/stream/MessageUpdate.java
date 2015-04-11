package stream;

import model.JsonModel;
import model.Message;

public class MessageUpdate extends JsonModel {
    private final String type = "message";
    private final int bot;
    private final Message message;

    public MessageUpdate(int bot, Message message) {
        this.bot = bot;
        this.message = message;
    }
}

package tinder;

/**
 * Created by Zach on 4/11/2015.
 */
public class Message {
    private String toID;
    private String fromID;
    private String message;
    public Message(String toID, String fromID, String message) {
        this.toID = toID;
        this.fromID = fromID;
        this.message = message;
    }


    public String getToID() {
        return toID;
    }

    public String getFromID() {
        return fromID;
    }

    public String getMessage() {
        return message;
    }
}

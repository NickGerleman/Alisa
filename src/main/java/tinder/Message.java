package tinder;

/**
 * Created by Zach on 4/11/2015.
 */
public class Message {
    private String toID;
    private String fromID;
    private String message;
    private long timestamp;
    private String messageID;
    private String lastActivityDate;

    public Message(String toID, String fromID, String message, long timestamp, String messageID) {
        this.toID = toID;
        this.fromID = fromID;
        this.timestamp = timestamp;
        this.messageID = messageID;
        this.lastActivityDate = lastActivityDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessageID() {
        return messageID;
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

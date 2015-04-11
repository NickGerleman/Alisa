package model;

import java.sql.Timestamp;

public class Message extends JsonModel implements Comparable<Message> {
    private final String id;
    private final String from;
    private final String to;
    private final String text;
    private final Timestamp timestamp;

    public Message(String id, String from, String to, String text, Timestamp timeStamp) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.text = text;
        this.timestamp = timeStamp;
    }

    @Override
    public int compareTo(Message other) {
        return timestamp.compareTo(other.timestamp);
    }
}

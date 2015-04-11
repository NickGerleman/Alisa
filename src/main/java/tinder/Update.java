package tinder;

import java.util.ArrayList;

public class Update {
	String id;
	ArrayList<Message> message;
	long timestamp;
	String matchID;

	public Update(String id, ArrayList<Message> message,
			long timestamp, String matchID) {
		super();
		this.id = id;
		this.message = message;
		this.timestamp = timestamp;
		this.matchID = matchID;
	}
	public String getId() {
		return id;
	}
	public ArrayList<Message> getMessage() {
		return message;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public String getMatchId(){
		return matchID;
	}

	@Override
	public String toString() {
		return "Update{" +
				"id='" + id + '\'' +
				", timestamp=" + timestamp +
				", matchID='" + matchID + '\'' +
				", message='" + message.size() + '\'' +
				'}';
	}
}

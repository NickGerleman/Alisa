package tinder;

public class Update {
	String id;
	String to;
	String from;
	String message;
	long timestamp;
	String matchID;
	
	public Update(String id, String to, String from, String message,
			long timestamp, String matchID) {
		super();
		this.id = id;
		this.to = to;
		this.from = from;
		this.message = message;
		this.timestamp = timestamp;
		this.matchID = matchID;
	}
	public String getId() {
		return id;
	}
	public String getTo() {
		return to;
	}
	public String getFrom() {
		return from;
	}
	public String getMessage() {
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
				", to='" + to + '\'' +
				", from='" + from + '\'' +
				", message='" + message + '\'' +
				", timestamp=" + timestamp +
				", matchID='" + matchID + '\'' +
				'}';
	}
}

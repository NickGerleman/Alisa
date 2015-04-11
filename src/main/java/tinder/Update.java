package tinder;

import java.util.ArrayList;

public class Update {
	String id;
	ArrayList<Message> message;
	String matchID;

	public Update(String id, ArrayList<Message> message,
			 String matchID) {
		super();
		this.id = id;
		this.message = message;
		this.matchID = matchID;
	}
	public String getId() {
		return id;
	}
	public ArrayList<Message> getMessage() {
		return message;
	}

	public String getMatchId(){
		return matchID;
	}
	public String getToID(){
		if(message.size()!=0)
			return message.get(0).getToID();
		return "";
	}

	@Override
	public String toString() {
		return "Update{" +
				"id='" + id + '\'' +
				", matchID='" + matchID + '\'' +
				", message='" + message.size() + '\'' +
				", toID='" + getToID() + '\'' +
				'}';
	}
}

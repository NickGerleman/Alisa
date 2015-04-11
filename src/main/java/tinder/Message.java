package tinder;

public class Message {
public String text;
public long timestamp;

public Message(long time, String message){
	text=message;
	timestamp=time;
}

public String getText() {
	return text;
}

public long getTimestamp() {
	return timestamp;
}

}

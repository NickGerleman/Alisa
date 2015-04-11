package tinder;

import java.util.ArrayList;

public class Conversation {
	public String sendingProfile;
	public String receivingProfile;
	public ArrayList<Message> sent;
	public ArrayList<Message> received;
	
	public Conversation(String send, String receive){
		sendingProfile = send;
		receivingProfile = receive;
		sent = new ArrayList<Message>();
	}
	
	public void newSent(String message){
		sent.add(new Message(System.currentTimeMillis(), message));
	}
	public void newReceived(String message){
		received.add(new Message(System.currentTimeMillis(), message));
	}
}

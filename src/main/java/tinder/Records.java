package tinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zach on 4/11/2015.
 */
public class Records {

    public List<Message> messages;
    public List<OtherUser> users;

    public Records(){
        messages = new ArrayList<Message>();
        users = new ArrayList<OtherUser>();
    }
    public void addMessage(Message message){
        messages.add(message);
    }

    public void addUser(OtherUser user){
        users.add(user);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<OtherUser> getUsers() {
        return users;
    }
}

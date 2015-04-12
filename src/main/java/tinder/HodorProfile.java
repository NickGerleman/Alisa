package tinder;

/**
 * Created by Zach on 4/11/2015.
 */
public class HodorProfile extends Profile{

    @Override
    public String sendResponse(String userId, String theirMessage) {
        sendMessage(userId,"Hodor.");
        return "Hodor";
    }
}

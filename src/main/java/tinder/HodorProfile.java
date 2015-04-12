package tinder;

import java.util.Random;

/**
 * Created by Zach on 4/11/2015.
 */
public class HodorProfile extends Profile{

    @Override
    public String sendResponse(String userId, String theirMessage) {

        //note a single Random object is reused here
        Random randomGenerator = new Random();
        int numHodor = randomGenerator.nextInt(20);
        StringBuilder sb = new StringBuilder();
        sb.append("Hodor");
        for(int i = 0;i<numHodor;i++){
            sb.append(" Hodor");
            if(sb.length()%7==3)
                sb.append(".");
        }
        sb.append(".");
        sendMessage(userId,sb.toString());
        return sb.toString();
    }
}

package tinder;

import chatterbot.ChatterBotSession;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by Zach on 4/11/2015.
 */
public class HodorProfile extends Profile{

    public HodorProfile(String name, String facebookToken){
        super.profileName = name;
        super.facebookCookie = facebookToken;
        this.updateAuthCookie();
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        timestamp = df.format(new Date());
    }

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

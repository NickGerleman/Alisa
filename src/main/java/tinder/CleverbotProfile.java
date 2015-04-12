package tinder;

import chatterbot.ChatterBot;
import chatterbot.ChatterBotFactory;
import chatterbot.ChatterBotSession;
import chatterbot.ChatterBotType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;

public class CleverbotProfile extends Profile {
	public HashMap<String, ChatterBotSession> chatterbots;

	public CleverbotProfile(String name, String facebookToken){
		super.profileName = name;
		super.facebookCookie = facebookToken;
		this.updateAuthCookie();
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		timestamp = df.format(new Date());
		chatterbots = new HashMap<String,ChatterBotSession>();
	}

	/*
	Sends a response to the user maintaining a session with cleverbot
	 */
	public String sendResponse(String userId, String theirMessage){
		String response = "";
		if (!chatterbots.containsKey(userId)){
			ChatterBotFactory factory = new ChatterBotFactory();
			try {
				Random randomGenerator = new Random();
				int numHodor = randomGenerator.nextInt(1);
				ChatterBot bot;
				if(numHodor==0) {
					bot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
				}
				else{
					bot = factory.create(ChatterBotType.CLEVERBOT);
				}

				chatterbots.put(userId, bot.createSession());
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		try {

			System.out.println("Chatterbots = null?" + chatterbots==null);
			ChatterBotSession botsession  = chatterbots.get(userId);
			response = botsession.think(theirMessage);
			super.sendMessage(userId,response);
		}catch (Exception e){
			e.printStackTrace();
		}
		return response;
	}

	public static void main(String[] args){
		Profile eliza = new CleverbotProfile("Eliza","CAAGm0PX4ZCpsBANU9X4Ko87f2M4m3dsjrAV5bgZCWcZBn8NVRx0fgAtMrSUNwbzZAv5oPgdO2nkyjlraJJsapNpJhr1OfTLeR9biWHDaq60QMJ5RpGtWffoi5ZA901aL9ia7h6XjuzyYTZCjLKQZB6rjcd9SVRLhTZC1TVxA7ZAxm1GQY8DqkvZByezy4ibg9m2uvgpd40XJZCmghqLZAF3VDlpa");
		String response = eliza.sendResponse("54ca7af5eed36d21180a3aff5529692e2bcf0989376e66ef","Can I tongue-punch your fartbox?");
		System.out.println(response);
	}
}

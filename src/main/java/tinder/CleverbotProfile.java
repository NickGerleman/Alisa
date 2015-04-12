package tinder;

import chatterbot.ChatterBot;
import chatterbot.ChatterBotFactory;
import chatterbot.ChatterBotSession;
import chatterbot.ChatterBotType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class CleverbotProfile extends Profile {
	public HashMap<String, ChatterBot> chatterbots;

	public CleverbotProfile(String name, String facebookToken){
		super.profileName = name;
		super.facebookCookie = facebookToken;
		this.updateAuthCookie();
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		timestamp = df.format(new Date());
		chatterbots = new HashMap<String,ChatterBot>();
	}

	/*
	Sends a response to the user maintaining a session with cleverbot
	 */
	public String sendResponse(String userId, String theirMessage){
		String response = "";
		if (chatterbots.containsKey(userId)){
			ChatterBotFactory factory = new ChatterBotFactory();
			try {
				chatterbots.put(userId, factory.create(ChatterBotType.CLEVERBOT));
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		try {
			ChatterBot bot = chatterbots.get(userId);
			ChatterBotSession botsession = bot.createSession();
			response = botsession.think(theirMessage);
			super.sendMessage(userId,response);
		}catch (Exception e){
			e.printStackTrace();
		}
		return response;
	}
}

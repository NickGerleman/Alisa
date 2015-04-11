package tinder;

import chatterbot.*;

/*
 * Runs and checks for updates on all of the profiles.
 * Updates database of messages/etc
 * handles communication to cleverbot
 */
public class ProfileManager {

	public static void main(String[] args) throws Exception {
		  ChatterBotFactory factory = new ChatterBotFactory();

	        ChatterBot bot1 = factory.create(ChatterBotType.CLEVERBOT);
	        ChatterBotSession bot1session = bot1.createSession();

	        ChatterBot bot2 = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
	        ChatterBotSession bot2session = bot2.createSession();

	        String s = "Il fukin rek ya m8";
	        while (true) {

	            System.out.println("bot1> " + s);

	            s = bot2session.think(s);
	            System.out.println("bot2> " + s);

	            s = bot1session.think(s);
	        }
	}

	
}

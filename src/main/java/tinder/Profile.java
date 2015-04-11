package tinder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public abstract class Profile {
	public String profileName;
	public String facebookCookie;
	public String authCookie;
	public String timestamp;
	
	public void updateAuthCookie(){
		authCookie = Tinder.getAuthToken(facebookCookie);
	}
	
	public boolean updateLocation(double lat, double lon){
		return Tinder.ping(lat, lon, authCookie);
	}
	
	public List<Update> getUpdates(String timeStamp){
		return Tinder.update(authCookie, timeStamp);
	}

	public List<Update> getUpdates(){
		List<Update> updates = Tinder.update(authCookie,this.timestamp);
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		timestamp = df.format(new Date());
		return updates;
	}
	
	public List<OtherUser> autoLike(){


		List<OtherUser> recs = Tinder.getUsers(authCookie);
		
		for(int i = 0; i<recs.size(); i++){
			recs.get(i).matched = Tinder.like(recs.get(i).getId(),authCookie);
			System.out.println("Liked "+recs.get(i).getName());
		}
		
		return recs;
	}
	
	public boolean sendMessage(String userID, String message){
		try {
			Tinder.sendMessage(userID, authCookie, message);
		}catch(Exception e){
			updateAuthCookie();
			return false;
		}
		return true;
	}


}


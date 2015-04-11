package tinder;

import java.util.ArrayList;
import java.util.List;

public abstract class Profile {
	public String profileName;
	public String facebookCookie;
	public String authCookie;
	
	public void updateAuthCookie(){
		authCookie = Tinder.getAuthToken(facebookCookie);
	}
	
	public boolean updateLocation(double lat, double lon){
		return Tinder.ping(lat,lon,authCookie);
	}
	
	public boolean getUpdates(){
		boolean sucessful = false;
		
		
		
		return sucessful;
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
		boolean sucess = false;
		
		return sucess;
	}

}


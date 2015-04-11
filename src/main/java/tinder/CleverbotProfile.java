package tinder;

public class CleverbotProfile extends Profile {

	public CleverbotProfile(String name, String facebookToken){
		super.profileName = name;
		super.facebookCookie = facebookToken;
		this.updateAuthCookie();
	}
}

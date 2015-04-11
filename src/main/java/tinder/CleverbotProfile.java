package tinder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CleverbotProfile extends Profile {

	public CleverbotProfile(String name, String facebookToken){
		super.profileName = name;
		super.facebookCookie = facebookToken;
		this.updateAuthCookie();
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		timestamp = df.format(new Date());
	}
}

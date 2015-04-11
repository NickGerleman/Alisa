package tinder;

public class CleverbotProfile extends Profile {

	public CleverbotProfile(String name, String facebookToken){
		super.profileName = name;
		super.facebookCookie = facebookToken;
		this.updateAuthCookie();
	}
	
	public static void main(String[] args) {
		Profile aaron = new CleverbotProfile("Aaron", "CAAGm0PX4ZCpsBALZAsIzklU998ibZCBE1BObvwjFP4dW6wpLAOt4mbl5ylFNaP3h2vsMTMBTlkbdoWU5NPSNPwRcqQ69hxBDAhX4vQ7xhZB37WOsN5KPuFXpw0QL9YF38H8fKKpZCgnGmQhZAko5MyI2qCeBLs03JZCDp0lh2Jqd7ZCZA63oygjsR7H0xZAtKUykaBFGqZCY0NHBZAb7v7huPCMI");
		System.out.println(aaron.autolike(200));
	}


}

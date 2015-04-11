package cleverbot;


import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;

public class CleverBot {

	String cookie;
	String jsessionID;
	String ais;
	String cleverbotref;
	String cleverbotref2;
	String rps;
	String utmt;
	
	CookieStore cookieStore;
	public static void main(String[] args) {
		BasicClientCookie cookie = new BasicClientCookie("name", "value");
	

	}
	
	public static String cookie(){
		String cookie = "";
		
		return cookie;
	}
	
	public CleverBot(){
		cookieStore = new BasicCookieStore();
		BasicClientCookie cookie = new BasicClientCookie("utma", "223276361.357039040.1428745595.1428745595.1428");
		BasicClientCookie cookie2 = new BasicClientCookie("utmb", "223276361.1.10.1428745595");
		BasicClientCookie cookie3 = new BasicClientCookie("utmc", "223276361");
		BasicClientCookie cookie4 = new BasicClientCookie("utmz", "223276361.1428745595.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
		BasicClientCookie cookie5 = new BasicClientCookie("utmt", "1");
		BasicClientCookie cookie6 = new BasicClientCookie("unam", "e42ade3-14ca7dfaf4d-6e2a93fc-1");
		BasicClientCookie cookie7 = new BasicClientCookie("utma", "-1");
		cookie.setDomain(".cleverbot.com");
		cookie2.setDomain(".cleverbot.com");
		cookie3.setDomain(".cleverbot.com");
		cookie4.setDomain(".cleverbot.com");
		cookie5.setDomain(".cleverbot.com");
		cookie6.setDomain(".cleverbot.com");
		cookie7.setDomain(".cleverbot.com");
		cookie.setPath("/webservicemin");
		cookie2.setPath("/webservicemin");
		cookie3.setPath("/webservicemin");
		cookie4.setPath("/webservicemin");
		cookie4.setPath("/webservicemin");
		cookie5.setPath("/webservicemin");
		cookie6.setPath("/webservicemin");
		cookie7.setPath("/webservicemin");
		cookieStore.addCookie(cookie);
		cookieStore.addCookie(cookie2);
		cookieStore.addCookie(cookie3);
		cookieStore.addCookie(cookie4);
		cookieStore.addCookie(cookie5);
		cookieStore.addCookie(cookie6);
		cookieStore.addCookie(cookie7);
		
		
		RequestConfig globalConfig = RequestConfig.custom()
		        .setCookieSpec(CookieSpecs.STANDARD)
		        .build();
		CloseableHttpClient httpclient = HttpClients.custom()
		        .setDefaultRequestConfig(globalConfig)
		        .setDefaultCookieStore(cookieStore)
		        .build();
		RequestConfig localConfig = RequestConfig.copy(globalConfig)
		        .setCookieSpec(CookieSpecs.STANDARD)
		        .build();
		HttpPost httpPost = new HttpPost("/webservicemin");
		httpPost.setConfig(localConfig);
		
		
	}

}

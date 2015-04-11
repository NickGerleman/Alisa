package tinder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import model.Photo;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Tinder {
	public static String ua = "Tinder Android Version 4.0.6";
	public static String platform = "android";
	public static String osVersion = "21";
	public static String appVersion = "770";
	public static String contentType = "application/json";
	public static String locale = "en";
	/*
	 * Sends a message to the given account
	 */
	public boolean sendMessage(){
		return true;
	}
	
	public static void setHeaders(AbstractHttpMessage entity){
		entity.setHeader("platform",platform);
		entity.setHeader("User-Agent",ua);
		entity.setHeader("os-version",osVersion);
		entity.setHeader("app-version",appVersion);
		entity.setHeader("Content-Type", contentType);
		entity.setHeader("Host","api.gotinder.com");
		entity.setHeader("Connection", "Keep-Alive");
		entity.setHeader("Accept-Encoding","gzip");
	}
	public static void setHeaders(AbstractHttpMessage entity, String token){
		entity.setHeader("platform", platform);
		entity.setHeader("User-Agent", ua);
		entity.setHeader("X-Auth-Token", token);
		entity.setHeader("os-version", osVersion);
		entity.setHeader("app-version", appVersion);
		entity.setHeader("Content-Type", contentType);
		entity.setHeader("Host","api.gotinder.com");
		entity.setHeader("Connection", "Keep-Alive");
		entity.setHeader("Accept-Encoding","gzip");
	}
	
	/*
	 * Returns everything from the authentication endpoint
	 * needs the facebook token from wherever
	 */
	public static String auth(String facebookCookie) throws ClientProtocolException, IOException{
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		
			HttpPost post = new HttpPost("https://api.gotinder.com/auth");
			StringBuilder sb = new StringBuilder();
			sb.append("{\"facebook_token\":\"");
			sb.append(facebookCookie);
			sb.append("\",\"locale\":\"");
			sb.append(locale);
			sb.append("\"}");
			setHeaders(post);
			post.setEntity(new StringEntity(sb.toString()));
			
			
			
	ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
				@Override
				public String handleResponse(final HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8)
								: null;
					} else {
						if (status == 404) {
							System.err.println("Error: 404. Page not found.");
							return "";
						}
						if (status <= 500 && status < 600) {
							if(status == 500){
								return "ERR:500";
							}
							System.err.println("Return status: "+status);
							return "ERR:500s";
						} else {
							throw new ClientProtocolException(
									"Unexpected response status: " + status);
						}
					}
				}

			};

			
			String bleh = httpclient.execute(post,responseHandler);
			System.out.println(bleh);
		
			return bleh;
	}

	public static String getAuthToken(String facebookToken){
		String token = "";
		String response = "";
		try {
			response = auth(facebookToken);
		} catch (Exception e) {
			System.err.println("Couldn't get authentication token :(");
			e.printStackTrace();
			return "";
		}
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(response);
			token = (String) json.get("token");
			//System.out.println(bloop);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return token;
	}
	
	/*
	 * Updates the user's location
	 */
	public static boolean ping(double lat, double lon, String tinderToken){
		boolean success = false;
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost("https://api.gotinder.com/user/ping");
		StringBuilder sb = new StringBuilder();
		//judge me
		sb.append("{\"lat\":");
		sb.append(lat);
		sb.append(",\"lon\":");
		sb.append(lon);
		sb.append("}");
		
		setHeaders(post,tinderToken);
		try {
			post.setEntity(new StringEntity(sb.toString()));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
			@Override
			public String handleResponse(final HttpResponse response)
					throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8)
							: null;
				} else {
					if (status == 404) {
						System.err.println("Error: 404. Page not found.");
						return "";
					}
					if (status <= 500 && status < 600) {
						if(status == 500){
							return "ERR:500";
						}
						System.err.println("Return status: "+status);
						return "ERR:500s";
					} else {
						throw new ClientProtocolException(
								"Unexpected response status: " + status);
					}
				}
			}

		};

		
		String response="";
		try {
			response = httpclient.execute(post,responseHandler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONParser parser = new JSONParser();
		String token = "0";
		try {
			JSONObject json = (JSONObject) parser.parse(response);
			token = json.get("status").toString();
			//System.out.println(token);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(token);
		if(token.equals("200")){
			success = true;
		}
		
		return success;
	}
	
	public static List<OtherUser> getUsers(String tinderToken){
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet("https://api.gotinder.com/user/recs");
		setHeaders(get,tinderToken);
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
			@Override
			public String handleResponse(final HttpResponse response)
					throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8)
							: null;
				} else {
					if (status == 404) {
						System.err.println("Error: 404. Page not found.");
						return "";
					}
					if (status <= 500 && status < 600) {
						if(status == 500){
							return "ERR:500";
						}
						System.err.println("Return status: "+status);
						return "ERR:500s";
					} else {
						throw new ClientProtocolException(
								"Unexpected response status: " + status);
					}
				}
			}

		};
		
		String response = "";
		try {
			response = httpclient.execute(get,responseHandler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(response);
		JSONParser parser = new JSONParser();
		ArrayList<OtherUser> recs = new ArrayList<OtherUser>();
		try {
			JSONObject json = (JSONObject) parser.parse(response);
			JSONArray arr = (JSONArray) json.get("results");
			for(int i = 0; i<(int) arr.size(); i++){
				JSONObject person = (JSONObject) arr.get(i);
				String id = (String) person.get("_id");
				String name = (String) person.get("name");
				int gender = Integer.parseInt(person.get("gender").toString());
				ArrayList<Photo> photos = new ArrayList<Photo>();
				JSONArray phot = (JSONArray) person.get("photos");
				String birthdate = person.get("birth_date").toString();
				for(int j=0;j<phot.size();j++){
					JSONObject pic = (JSONObject) phot.get(j);
					String m ="";
					if(pic.containsKey("main"))
						m=(pic.get("main").toString());
					boolean main = m.equals("true")||m.equals("main");
					JSONArray processed = (JSONArray) pic.get("processedFiles");
					JSONObject processedPhoto = (JSONObject) processed.get(0);
					String url640 = processedPhoto.get("url").toString();
					processedPhoto = (JSONObject) processed.get(1);
					String url320 = processedPhoto.get("url").toString();
					processedPhoto = (JSONObject) processed.get(2);
					String url172 = processedPhoto.get("url").toString();
					processedPhoto = (JSONObject) processed.get(3);
					String url84 = processedPhoto.get("url").toString();

					Photo picture = new Photo(pic.get("id").toString(),id,main,url640,url320,url172,url84);
					photos.add(picture);
				}
				
				//System.out.println(id);
				recs.add(new OtherUser(id,gender,name,photos,birthdate));
			}
			
			//System.out.println(token);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return recs;
	}
	
	/*
	 * likes a profile id
	 * true if they like us, false if not
	 */
	public static boolean like(String id, String tinderToken){
		boolean like = false;
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		StringBuilder sb = new StringBuilder();
		sb.append("https://api.gotinder.com/like/");
		sb.append(id);
		HttpGet get = new HttpGet(sb.toString());
		setHeaders(get,tinderToken);
		
		try {
			String response = httpclient.execute(get,responseHandler);
			if(response.contains("true"))
				like=true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return like;
	}
	
	public static ArrayList<Update> update(String tinderToken,String timeStamp){
		ArrayList<Update> updates = new ArrayList<Update>();
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost("https://api.gotinder.com/updates");
		setHeaders(post, tinderToken);
		StringBuilder sb = new StringBuilder();
		sb.append("{\"last_activity_date\":\"");
		sb.append(timeStamp);
		sb.append("\"}");

		try {
			post.setEntity(new StringEntity(sb.toString()));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String response = "";
		try {
			response = httpclient.execute(post,responseHandler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONParser parser = new JSONParser();
		JSONObject json;
		try {
			json = (JSONObject) parser.parse(response);
			System.out.println(response);
			JSONArray arr = (JSONArray) json.get("matches");
			for(int i = 0;i<arr.size();i++){
				JSONObject updated = (JSONObject) arr.get(i);
				String id = (String) updated.get("_id");
				ArrayList<Message> massages = new ArrayList<Message>();
				JSONArray messages = (JSONArray) updated.get("messages");
				long timestamp = 0;
				String matchID = "";
				for(int j = 0; j<messages.size();j++){
					JSONObject message = (JSONObject) messages.get(j);
					String fromID = (String) message.get("from");
					String messageID = (String) message.get("_id)");
					String toID = (String) message.get("to");
					matchID = (String) message.get("match_id");
					String messageText = (String) message.get("message");
					timestamp = Long.parseLong(message.get("timestamp").toString());
					massages.add(new Message(toID,fromID,messageText));


				}
				if(matchID.equals("")){
					matchID = id;
				}
				updates.add(new Update(id,massages,timestamp,matchID));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return updates;
	}
	
	public static boolean sendMessage(String id, String tinderToken, String message){
		boolean success = true;
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		StringBuilder sb = new StringBuilder();
		sb.append("https://api.gotinder.com/user/matches/");
		sb.append(id);
		HttpPost post = new HttpPost(sb.toString());
		setHeaders(post,tinderToken);
		
		sb.setLength(0);
		sb.append("{\"message\": \"");//IDGAF

		sb.append(message);
		sb.append("\"}");
		System.out.println(sb.toString());
		try {
			post.setEntity(new StringEntity(sb.toString()));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String response="";
		try {
			response = httpclient.execute(post,responseHandler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return success;
	}
	public static void main(String[] args){
		String authToken = getAuthToken("CAAGm0PX4ZCpsBANU9X4Ko87f2M4m3dsjrAV5bgZCWcZBn8NVRx0fgAtMrSUNwbzZAv5oPgdO2nkyjlraJJsapNpJhr1OfTLeR9biWHDaq60QMJ5RpGtWffoi5ZA901aL9ia7h6XjuzyYTZCjLKQZB6rjcd9SVRLhTZC1TVxA7ZAxm1GQY8DqkvZByezy4ibg9m2uvgpd40XJZCmghqLZAF3VDlpa");
		//System.out.println("AuthToken=" + authToken);
		ping(42.0301381,-93.6521859 , authToken);
		List<Update> arr = update(authToken, "2015-04-11T08:32:21.016Z");
		List<OtherUser> arr2 = getUsers(authToken);
		for(int i =0;i<arr.size();i++) {
			System.out.println(arr.get(i));
		}
		String daniel = "54ca7af5eed36d21180a3aff5529692e2bcf0989376e66ef";
		sendMessage(daniel, authToken, "I'm a real boy");
		System.out.println();
	}

	public static void sendToken(String tinderToken){
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		StringBuilder sb = new StringBuilder();
		HttpPost post = new HttpPost("https://api.gotinder.com/sendtoken");
		setHeaders(post, tinderToken);
		try {
			post.setEntity(new StringEntity("{\"phone_number\": \"+1515-822-8210\"}"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String response="";
		try {
			response = httpclient.execute(post,responseHandler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(response);
	}

	private static ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
		@Override
		public String handleResponse(final HttpResponse response)
				throws ClientProtocolException, IOException {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8)
						: null;
			} else {
				if (status == 404) {
					System.err.println("Error: 404. Page not found.");
					return "";
				}
				if (status <= 500 && status < 600) {
					if(status == 500){
						return "ERR:500";
					}
					System.err.println("Return status: "+status);
					return "ERR:500s";
				} else {
					throw new ClientProtocolException(
							"Unexpected response status: " + status);
				}
			}
		}

	};
}

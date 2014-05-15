package backend;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
//import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import calendar.CalendarItem;
 
public class httppusher {
 
	private final String USER_AGENT = "Mozilla/5.0";
	
	final String URL = "http://insight.exomodal.com:80/collectionapi/calendar_items";
	final String TOKEN = "9900aa";
	
	final String DBtemptype = "status";
	final int DBoperatorid = 23; 
 
	// HTTP GET request
	public void sendGet() {
 
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(URL);
 
		// add request header
		request.setHeader("X-Auth-Token", TOKEN);
 
		HttpResponse response;
		
		//System.out.println("\nSending 'GET' request to URL : " + url);
		//System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		try {
			response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			 
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			System.out.println(result.toString());
		} catch (IOException e) {
			System.out.println("error: http get");
		}
 
		
 
	}
 
	// HTTP POST request
	public void sendPost(CalendarItem item){
 
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(URL);
 
		// add header
		post.setHeader("X-Auth-Token", TOKEN);

		//String content = "{\"operatorid\":23,\"templatetype\":\"status\",\"typeid\":2,\"id\":7,\"allDay\":false,\"start\":1399970000,\"end\":1399973000,\"title\":\"HOI blala\"}";
		
		StringEntity se;
		try {
			se = new StringEntity(item.toHTTPString());
			se.setContentEncoding("UTF-8");
		    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		    post.setEntity(se);
		} catch (UnsupportedEncodingException e) {
			System.out.println("error: sendPost 1");
		}

		HttpResponse response;
		
		System.out.println(item.toHTTPString());
		
		
		try {
			response = client.execute(post);
		} catch (IOException e) {
			System.out.println("error: sendPost 2");
		}
		
		//System.out.println("\nSending 'POST' request to URL : " + URL);
		//System.out.println("Post parameters : " + post.getEntity());
		//System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
 
		//BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
 
		//StringBuffer result = new StringBuffer();
		//String line = "";
		//while ((line = rd.readLine()) != null) {
		//	result.append(line);
		//}
 
		//System.out.println(result.toString());
 
	}
	
	public void deletePost(){
		HttpDelete del = new HttpDelete(URL);
		del.setHeader("X-Auth-Token", TOKEN);
	}
	
	public static void main(String[] args) throws Exception {
		 
		httppusher http = new httppusher();
 
		//System.out.println("Testing 1 - Send Http GET request");
		http.sendGet();
 
		//System.out.println("\nTesting 2 - Send Http POST request");
		//http.sendPost();
 
	}
 
}
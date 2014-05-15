package backend;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.entity.StringEntity;

import calendar.CalendarItem;
 
public class httppusher {
 
	private final String USER_AGENT = "Mozilla/5.0";
	
	final String URL = "http://insight.exomodal.com:80/collectionapi/calendar_items";
	final String TOKEN = "9900aa";
	
	final String DBtemptype = "status";
	final int DBoperatorid = 23; 
 
	/**
	 * Returnt een string met de calendar_items collection
	 * @return String zoals: [{"_id":"2QCGtYTdxrcQezJsM","operatorid":3}]
	 */
	public String sendGet() {
 
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(URL);
		
		// add request header
		request.setHeader("X-Auth-Token", TOKEN);
 
		HttpResponse response;

		String result = "";
		
		try {
			response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			result = rd.readLine();
		} catch (IOException e) {
			System.out.println("error: http get");
		}
 
		return result;
	}
 
	// HTTP POST request
	public void sendPost(CalendarItem item){
 
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(URL);
 
		// add header
		post.setHeader("X-Auth-Token", TOKEN);

		//String test = "{\"operatorid\":23,\"templatetype\":\"status\",\"typeid\":2,\"id\":7,\"allDay\":false,\"start\":1399970000,\"end\":1399973000,\"title\":\"HOI blala\"}";
		
		StringEntity se;
		try {
			se = new StringEntity(item.toHTTPString());
			//se.setContentEncoding("UTF-8");
		    //se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
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
 
	}
	
	public static void main(String[] args) throws Exception {
		 
		httppusher http = new httppusher();
 
		//System.out.println("Testing 1 - Send Http GET request");
		System.out.println(http.sendGet());
		//http.sendGet();
		//System.out.println("\nTesting 2 - Send Http POST request");
		//http.sendPost();
 
	}
 
}
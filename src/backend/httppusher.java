package backend;
 
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.entity.StringEntity;

import calendar.CalendarItem;
 
public class httppusher {
 
	private String url;
	private String token;
	
	final String DBtemptype = "status";
	final int DBoperatorid = 23; 
	
	private Properties properties;
	
	public httppusher(String propertiesFile){
		this.loadProperties(propertiesFile);
	}
 
	/**
	 * Returnt een string met de calendar_items collection
	 * @return String zoals: [{"_id":"2QCGtYTdxrcQezJsM","operatorid":3}]
	 */
	public String sendGet() {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		
		// add request header
		request.setHeader("X-Auth-Token", token);
 
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
	
	/**
	 * Vraag een object op aan de applicatie
	 * @param id
	 * @return
	 */
	public CalendarItem sendGet(String id){
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url + '/' + id);
		
		// add request header
		get.setHeader("X-Auth-Token", token);
 
		HttpResponse response;
		CalendarItem result = null;
		try {
			response = client.execute(get);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			ArrayList<CalendarItem> lijst = Splitter.split(rd.readLine());
			result = (lijst.size() > 0) ? lijst.get(0) : null;
		} catch (IOException e) {
			System.out.println("error: http get");
		}
		return result;
	}
 
	/**
	 * Stuur een nieuw object naar de applicatie
	 * @param item
	 */
	public void sendPost(CalendarItem item){
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
 
		// add header
		post.setHeader("X-Auth-Token", token);

		//String test = "{\"operatorid\":23,\"templatetype\":\"status\",\"typeid\":2,\"id\":7,\"allDay\":false,\"start\":1399970000,\"end\":1399973000,\"title\":\"HOI blala\"}";
		StringEntity se;
		try {
			se = new StringEntity(item.toHTTPString());
		    post.setEntity(se);
		    client.execute(post);
		} catch (UnsupportedEncodingException e) {
			System.err.println("error: httppusher sendPost 1");
		} catch (IOException e) {
			System.err.println("error: httppusher sendPost 2");
		} 
	}
	
	public void deleteAll(){
		
	}
	
	/**
	 * Verwijder een object met een bepaald id uit de applicatie
	 * @param id
	 */
	public void sendDelete(String id) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpDelete del = new HttpDelete(url + '/' + id);
		
		// add request header
		del.setHeader("X-Auth-Token", token);
 
		try {
			client.execute(del);
		} catch (IOException e) {
			System.err.println("error: http del " + e.getMessage());
		}
	}
	
	/**
	 * Send een update over http
	 * @param id
	 */
	public void sendUpdate(String id) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPut put = new HttpPut(url + '/' + id);
		
		// add request header
		put.setHeader("X-Auth-Token", token);
 
		StringEntity se;
		try {
			se = new StringEntity("{\"$set\":{\"opmerkingen\":\"Geupdate door pusher!\"}}");
			put.setEntity(se);
			client.execute(put);
		} catch (IOException e) {
			System.err.println("error: http update " + e.getMessage());
		}
	}
	
	private void loadProperties(String file){
    	try {
    		FileInputStream in = new FileInputStream(file);
            properties.load(in);
            token = properties.getProperty("http.token");
            url = properties.getProperty("http.url");
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());

        } catch (IOException e){
        	System.err.println(e.getMessage());
        }
    	
    }
	
	public static void main(String[] args) throws Exception {
		 
		httppusher http = new httppusher("database.properties");
 
		//System.out.println("Testing 1 - Send Http GET request");
		//System.out.println(http.sendGet());
		//http.sendDelete("MLzTkF9EBLjk4br8A");
		//http.sendUpdate("gZ6WjM4TyjfEkGYw7");
		System.out.println(http.sendGet("gZ6WjM4TyjfEkGYw7").toString());
		//http.sendGet();
		//System.out.println("\nTesting 2 - Send Http POST request");
		//http.sendPost();
 
	}
 
}
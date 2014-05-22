package backend;
 
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
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
 
public class HttpPusher {
 
	private String url;
	private String token;
	private String temptype;
	private int operatorid;
	
	private Properties httpprops;
	
	public HttpPusher(String propertiesFile){
		httpprops = new Properties();
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
			System.err.println("error: http get " + e.getMessage());
		}
 
		return result;
	}
	
	/**
	 * Vraag een object op aan de applicatie
	 * @param id
	 * @return
	 */
	public CalendarItem sendGet(CalendarItem item){
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url + '/' + item.getMondoID());
		
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
			System.err.println("error: http get " + e.getMessage());
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
 
		System.out.println("url: " + this.toHTTPString(item));
		// add header
		post.setHeader("X-Auth-Token", token);

		//String test = "{\"operatorid\":23,\"templatetype\":\"status\",\"typeid\":2,\"id\":7,\"allDay\":false,\"start\":1399970000,\"end\":1399973000,\"title\":\"HOI blala\"}";
		StringEntity se;
		try {
			se = new StringEntity(this.toHTTPString(item));
		    post.setEntity(se);
		    client.execute(post);
		} catch (UnsupportedEncodingException e) {
			System.err.println("error: httppusher sendPost 1");
		} catch (IOException e) {
			System.err.println("error: httppusher sendPost 2");
		} 
	}
	
	public void deleteAll(){
		//TODO geen implementatie
	}
	
	/**
	 * Verwijder een object met een bepaald id uit de applicatie
	 * @param id
	 */
	public void sendDelete(CalendarItem item) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpDelete del = new HttpDelete(url + '/' + item.getMondoID());
		
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
	public void sendUpdate(CalendarItem item) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPut put = new HttpPut(url + '/' + item.getMondoID());
		
		// add request header
		put.setHeader("X-Auth-Token", token);
 
		StringEntity se;
		try {
			String updateQuery = "{\"$set\":" + this.toHTTPString(item) + "}";
			//String updateQuery = "{\"$set\":{\"typeid\":1}}";
			se = new StringEntity(updateQuery);
			put.setEntity(se);
			client.execute(put);
		} catch (IOException e) {
			System.err.println("error: http update " + e.getMessage());
		}
	}
	
	public String getConfiguration(){
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("http://insight.exomodal.com:80/collectionapi/calendar_configuration");
		
		// add request header
		request.setHeader("X-Auth-Token", token);
 
		HttpResponse response;

		String result = "";
		
		try {
			response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			result = rd.readLine();
		} catch (IOException e) {
			System.err.println("error: http get " + e.getMessage());
		}
 
		return result;
	}
	
	public String getListConfiguration(){
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("http://insight.exomodal.com:80/collectionapi/list_configuration");
		
		// add request header
		request.setHeader("X-Auth-Token", token);
 
		HttpResponse response;

		String result = "";
		
		try {
			response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			result = rd.readLine();
		} catch (IOException e) {
			System.err.println("error: http get " + e.getMessage());
		}
 
		return result;
	}
	
	public String getDropdownConfiguration(){
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("http://insight.exomodal.com/collectionapi/calendar_dropdown");
		
		// add request header
		request.setHeader("X-Auth-Token", token);
 
		HttpResponse response;

		String result = "";
		
		try {
			response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			result = rd.readLine();
		} catch (IOException e) {
			System.err.println("error: http get " + e.getMessage());
		}
 
		return result;
	}
	
	/**
     * Laad de connectie eigenschappen uit een properties bestand
     */
    private void loadProperties(String prop){
    	try {
    		FileInputStream in = new FileInputStream(prop);
            httpprops.load(in);
            token = httpprops.getProperty("http.token");
            url = httpprops.getProperty("http.url");
            temptype = httpprops.getProperty("http.temptype");
            operatorid = 23; //httpprops.getProperty("http.operatorid");
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());

        } catch (IOException e){
        	System.err.println(e.getMessage());
        }
    }
    
    public String toHTTPString(CalendarItem item){
		SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy 'om' HH:mm");
		String date = "\"" + sdf.format((item.getBeschikbaarOp() - (ETAcalculator.HOUR * 2))*1000) + "\"";
		
		//Resulteerd in een string zoals: "{\"operatorid\":23,\"templatetype\":\"status\",\"typeid\":2,
		//\"id\":7,\"allDay\":false,\"start\":1399970000,\"end\":1399973000,\"title\":\"HOI b\"}";
		String result = 
				"{\"" 	+ CalendarItem.OPERATORID 	+ "\":" 	+ this.operatorid 		+ ',' +
				"\"" 	+ CalendarItem.TEMPLATETYPE + "\":\"" 	+ this.temptype 		+ "\"," +
				"\"" 	+ CalendarItem.TYPEID 		+ "\":" 	+ item.getStatus() 		+ ',' +
				"\"" 	+ CalendarItem.ID 			+ "\":" 	+ Hasher.hash(item.getBookingnr(), item.getContainernr()) + ',' +
				"\"" 	+ CalendarItem.CALLS 		+ "\":\"" 	+ item.getBookingnr() 	+ "\"," +
				"\"" 	+ CalendarItem.ALLDAY 		+ "\":" 	+ false 				+ ',' +
				"\"" 	+ CalendarItem.START 		+ "\":" 	+ item.getStart() 		+ ',' +
				"\"" 	+ "startlist" 				+ "\":" 	+ item.getStart()*1000	+ ',' +
				"\"" 	+ CalendarItem.END 			+ "\":" 	+ item.getEind() 		+ ',' +
				"\"" 	+ CalendarItem.BOOKINGNR 	+ "\":" 	+ item.getBookingnr() 	+ ',' +
				"\"" 	+ CalendarItem.CONTAINERNR 	+ "\":\"" 	+ item.getContainernr() + "\"," +
				"\"" 	+ CalendarItem.MRN 			+ "\":\"" 	+ item.getMRN()			+ "\"," +
				"\"" 	+ CalendarItem.KARTONS 		+ "\":" 	+ item.getKartons() 	+ ',' +
				"\"" 	+ CalendarItem.TITEL 		+ "\":\"" 	+ item.getKartons() 	+ "\"," +
				"\"" 	+ CalendarItem.UNITS 		+ "\":" 	+ item.getUnits() 		+ ',' +
				"\"" 	+ CalendarItem.BESCHIKBAAR 	+ "\":" 	+ date 					+ ',' +
				"\"" 	+ CalendarItem.GASMETING 	+ "\":" 	+ item.getGasmeting() 	+ ',' +
				"\"" 	+ CalendarItem.CATEGORIE 	+ "\":\"" 	+ item.getCategorie() 	+ "\"," +
				"\"" 	+ CalendarItem.OPMERKINGEN 	+ "\":\"" 	+ item.getOpmerkingen() + "\"}";
		return result;
	}
    
    
	
	public static void main(String[] args) throws Exception {
		 
		HttpPusher http = new HttpPusher("database.proprties");
 
		//System.out.println(http.sendGet());
		//http.sendGet();
		//http.sendPost();
		
		System.out.println(http.getDropdownConfiguration());
 
	}
 
}
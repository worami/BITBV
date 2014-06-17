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
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public void sendNotificatie(String bericht){
		String urlnew = "http://insight.exomodal.com/collectionapi/notifications";
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(urlnew);
 
		String test = "{\"userid\":\"KcwyZZvzqmYxR2a2B\",\"type\":\"alert\",\"timestamp\":1402992903000,\"message\":\"Dit is een test van wouter\",\"read\":false}";

		// add header
		post.setHeader("X-Auth-Token", token);
		//System.out.println("token: " + token);

		//String test = "{\"operatorid\":23,\"templatetype\":\"status\",\"typeid\":2,\"id\":7,\"allDay\":false,\"start\":1399970000,\"end\":1399973000,\"title\":\"HOI blala\"}";
		StringEntity se;
		try {
			se = new StringEntity(this.toNotificationHTTP(bericht));
		    post.setEntity(se);
		    client.execute(post);
		} catch (UnsupportedEncodingException e) {
			System.err.println("error: httppusher sendPost 1");
		} catch (IOException e) {
			System.err.println("error: httppusher sendPost 2");
		} 
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		HttpGet request = new HttpGet("http://insight.exomodal.com:80/collectionapi/list_rows");
		
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
	
	public String getNotifications(){
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("http://insight.exomodal.com/collectionapi/notifications");
		
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
            temptype = httpprops.getProperty("http.temptype"); //"statustransport"; //
            operatorid = 23; //httpprops.getProperty("http.operatorid");
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());

        } catch (IOException e){
        	System.err.println(e.getMessage());
        }
    }
    
    public void fakeUpdate(CalendarItem item, int status){
    	
    }
    
    private void loadConfiguration(String config){
    	
    }
    
    public String toHTTPString(CalendarItem item){
		//Zet de datum beschikbaar op om in een string
    	SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy 'om' HH:mm");
		String date = "\"" + sdf.format((item.getBeschikbaarOp() - (ETAcalculator.HOUR * 2))*1000) + "\"";
		
		SimpleDateFormat sdftwee = new SimpleDateFormat("EEE d MMM yyyy 'om' HH:mm");
		String datestart = "\"" + sdf.format((item.getStart() - (ETAcalculator.HOUR * 2))*1000) + "\"";
		
		//Zet de titel van een calendar item
		String title = "- " + item.getContainernr();
		//Zet spoed in de naam als er spoed is
		if(item.getSpoed()){
			title = title + " SPOED!";
		}
		
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
				"\"" 	+ "startlist" 				+ "\":" 	+ datestart				+ ',' +
				"\"" 	+ CalendarItem.END 			+ "\":" 	+ item.getEind() 		+ ',' +
				"\"" 	+ CalendarItem.BOOKINGNR 	+ "\":" 	+ item.getBookingnr() 	+ ',' +
				"\"" 	+ CalendarItem.CONTAINERNR 	+ "\":\"" 	+ item.getContainernr() + "\"," +
				"\"" 	+ CalendarItem.MRN 			+ "\":\"" 	+ item.getMRN()			+ "\"," +
				"\"" 	+ CalendarItem.KARTONS 		+ "\":" 	+ item.getKartons() 	+ ',' +
				"\"" 	+ CalendarItem.TITEL 		+ "\":\"" 	+ title				 	+ "\"," +
				"\"" 	+ CalendarItem.UNITS 		+ "\":" 	+ item.getUnits() 		+ ',' +
				"\"" 	+ CalendarItem.BESCHIKBAAR 	+ "\":" 	+ date 					+ ',' +
				"\"" 	+ CalendarItem.GASMETING 	+ "\":" 	+ item.getGasmeting() 	+ ',' +
				"\"" 	+ CalendarItem.CATEGORIE 	+ "\":\"" 	+ item.getCategorie() 	+ "\"," +
				"\"" 	+ CalendarItem.OPMERKINGEN 	+ "\":\"" 	+ item.getOpmerkingen() + "\"," + 
				"\""	+ CalendarItem.SPOED		+ "\":"		+ item.getSpoed()		+ "," +
				"\"" 	+ CalendarItem.DROPOFFDOCK	+ "\":"		+ item.getDropoffdock() + "," + 
				"\"" 	+ CalendarItem.PICKUPDOCK 	+ "\":"		+ item.getPickupdock()	+ "," +
				"\""	+ "selectionvalue"			+ "\":\""	+ item.getContainernr() + "\"}";
		System.out.println(result);
		return result;
	}
    /**
    - id: integer;              (When selected operatorid all users will get the same id for this message)
    - userid: String;           (Optional, only when operatorid is not specified)
    - operatorid: integer;      (Optional, only when userid is not specified, will not be stored in the collection: only used for collection API)
    - type: String;             (warning, alert)
    - timestamp: long;
    - title: HTML/String;       (Optional, only for type: alert, default: Alert)
    - message: HTML/String;
    - read: boolean;
    - readtimestamp: long;      (Optional, only when read is true)  **/
    public String toNotificationHTTP(String bericht){
    	String result = "{" +
    			//"\"" 	+ "id" 			+ "\":" 		+ this.operatorid 	'+ ',' +
    			"\"" 	+ "userid"	 	+ "\":\"" 		+ "KcwyZZvzqmYxR2a2B"+ "\"," +
    			"\"" 	+ "type" 		+ "\":\"" 		+ "alert"	 		+ "\"," +
    			"\"" 	+ "timestamp" 	+ "\":" 		+ 1402054601000l	+ "," +
    			//"\"" 	+ "title" 		+ "\":\"" 		+ "bericht"			+ "\"," +
    			"\"" 	+ "message" 	+ "\":\"" 		+ bericht 			+ "\"," +
    			"\"" 	+ "read" 		+ "\":"			+ false				+ "}";
    	return result;
    }
    
	
	public static void main(String[] args) throws Exception {
		 
		HttpPusher http = new HttpPusher("database.proprties");
		//http.sendNotificatie("ik ben het!");
		
		//System.out.println("notify: " + http.getNotifications());
		//System.out.println(http.sendGet());
		//http.sendGet();
		//http.sendPost();
		HttpPusher httplist = new HttpPusher("httplist.proprties");
		System.out.println(httplist.getListConfiguration());
		
		//System.out.println(http.getDropdownConfiguration());
		
		//String config = '{ "operatorid": 23, "templatetype": "announcementslist", "headercolors": { "text": "#ffffff", "background": "#2c3e50" }, "rowsperpage": 2, "filter": { "field": "date", "value": 1 }, "fields": [ { "type": "textfield", "mandatory": true, "name": "bookingNR", "label": "Boekingnummer", "width": 10 }, { "type": "textfield", "mandatory": true, "name": "containerNR", "label": "Containernummer", "width": 10  }, { "type": "textfield", "mandatory": true, "name": "mrn", "label": "MRN", "width": 10  }, { "type": "number", "mandatory": true, "name": "kartons", "label": "Kartons", "width": 10  }, { "type": "number", "mandatory": true, "name": "units", "label": "Units", "width": 10  }, { "type": "datepicker", "name": "startdate", "label": "Geplande datum", "width": 10  }, { "type": "datepicker", "name": "beschikbaarop", "label": "Beschikbaar op", "width": 10  }, { "type": "checkbox", "name": "gasmeting", "label": "Gasmeting", "width": 10, "default": false }, { "type": "dropdown", "name": "categorie", "label": "Categorie", "width": 10, "dropdownid":1 }, { "type": "textarea", "name": "opmerkingen", "label": "Opmerkingen", "width": 10, "default": false } ], "editable": true, "deletable": true, "creatable": false, "onsessionchange": { "validationvalue": "BITBV", "enabled": true } }';
 
	}
 
}
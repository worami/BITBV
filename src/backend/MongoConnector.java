package backend;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import calendar.CalendarItem;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;

public class MongoConnector {

	MongoClient mongo;
	DB db;
	
	Properties props = new Properties();
	final String DBprops = "database.proprties";
	
	final String DBtemptype = "bitbv";
	final int DBoperatorid = 4; 
	
	public MongoConnector(){
		this.loadProperties();
		this.Connect();
	}
	
	private void Connect(){
		try {
			mongo = new MongoClient();
			db = mongo.getDB(props.getProperty("mongo.db"));
		} catch (UnknownHostException e) {
			System.err.println(e.getMessage());
		}
//		//if (db.authenticate(props.getProperty("mongo.user"), props.getProperty("mongo.passwd").toCharArray())){
//			System.out.println("Loged in");
//		} else {
//			System.err.println("Wrong user/password");
//		}
	}
	
	public List<String[]> getData(){
		List<String[]> result = new ArrayList<String[]>();
		DBCollection coll = db.getCollection("calendar_items");
		System.out.println("Count: " + coll.getCount());
		
		BasicDBObject query = new BasicDBObject();

		DBCursor cursor = coll.find(query);

		try {
		   while(cursor.hasNext()) {
			   DBObject next = cursor.next();
			   
			   System.out.println(next.toString());
			   
			   //Object type = next.get("templatetype");
		       //System.out.println(type.toString());
		   }
		} finally {
		   cursor.close();
		}
		System.out.println(coll.findOne());
		return result;
	}
	
	private DBObject getOrder(int bookingnr){
		DBObject result = null;
		DBCollection col = db.getCollection("calendar_items");
		BasicDBObject query = new BasicDBObject("bookingnr", bookingnr);
		DBCursor cursor = col.find(query);
		try {
			   while(cursor.hasNext()) {
			       	result = cursor.next();
			   }
			} finally {
			   cursor.close();
			}
		return result;
	}
	
	/**
	 * Kijkt of 
	 * @param bookingnr
	 * @return
	 */
	public boolean bookingInDB(int bookingnr){
		return (getOrder(bookingnr) != null);
	}
	
	public void putCalendarItem(CalendarItem item){
		if(!bookingInDB(item.getBookingnr())){
			System.out.println("Yeah, nieuw claendar item!");
			BasicDBObject put = new BasicDBObject("operatorid", DBoperatorid).
					append("templatetype", DBtemptype).
					append("typeid", 5).
					//append("id", 12345).
					//append("calls", "12345").
					append("allDay", false).
					append("start", item.getStart()).
					append("end", item.getEind()).
					append("bookingnr", item.getBookingnr()).
					append("containernr", item.getContainernr()).
					append("mrn", item.getMRN()).
					append("kartons", item.getKartons()).
					append("units", item.getUnits()).
					append("beschikbaarop", item.getBeschikbaarOp()).
					append("gasmeting", item.getGasmeting()).
					append("categorie", item.getCategorie()).
					append("opmerkingen", item.getOpmerkingen());
			
			DBCollection coll = db.getCollection("calendar_items");
			coll.insert(put);
			
			/** check of het item in de db staat */
			if(!put.equals(coll.findOne())){
				System.err.println("Calendar put mislukt: " + coll.findOne().toString());
			} else {
				System.out.println("Calendar put");
			}
		} 
	}
	
	public void updateCaledarItem(int bookingnr){
		BasicDBObject newDocument = new BasicDBObject().append("$set", new BasicDBObject().append("clients", 110));
	 
		BasicDBObject searchQuery = new BasicDBObject().append("hosting", "hostB");
	 
		db.getCollection("calendar_items").update(searchQuery, newDocument);
	}
	
	/**
	 * Print de collections van de db
	 */
	private void getCollections(){
		Set<String> colls = db.getCollectionNames();

		for (String s : colls) {
		    System.out.println(s);
		}
	}
	
	private void loadProperties(){
    	try {
    		FileInputStream in = new FileInputStream(DBprops);
            props.load(in);

        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());

        } catch (IOException e){
        	System.err.println(e.getMessage());
        }
    	
    }
}

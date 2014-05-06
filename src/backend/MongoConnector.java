package backend;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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
	
	public void putCalendarItem(int containernr){
		BasicDBObject put = new BasicDBObject("operatorid", DBoperatorid).
				append("templatetype", DBtemptype).
				append("typeid", 5).
				append("id", 12345).
				append("calls", "12345").
				append("allDay", false).
				append("start", 1399385700).
				append("end", 1399385880);
				
		DBCollection coll = db.getCollection("calendar_items");
		coll.insert(put);
		if(!put.equals(coll.findOne())){
			System.err.println("Calendar put mislukt");
		}
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

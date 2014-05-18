package dbconnections;

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
	
	final String CALENDARITEMS = "calendar_items";
	
	final String OPERATORID = "operatorid";
	final String TEMPLATETYPE = "templatetype";
	final String TYPEID = "typeid";
	final String ID = "id";
	final String CALLS = "calls";
	final String ALLDAY = "allDay";
	final String START = "start";
	final String END = "end";
	final String BOOKINGNR = "bookingnr";
	final String CONTAINERNR = "containernr";
	final String MRN = "mrn";
	final String KARTONS = "kartons";
	final String UNITS = "units";
	final String BESCHIKBAAR= "beschikbaarop";
	final String GASMETING = "gasmeting";
	final String CATEGORIE = "categorie";
	final String OPMERKINGEN = "opmerkingen";
	
	final String DBtemptype = "status";
	final int DBoperatorid = 23; 
	
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
		DBCollection coll = db.getCollection(CALENDARITEMS);
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
	
	private DBObject getCalendarItem(CalendarItem item){
		DBObject result = null;
		DBCollection col = db.getCollection(CALENDARITEMS);
		BasicDBObject query = new BasicDBObject(BOOKINGNR, item.getBookingnr()).append(CONTAINERNR, item.getContainernr());
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
	 * Kijkt of booking met container al in db staat
	 * @param item
	 * @return
	 */
	public boolean bookingInDB(CalendarItem item){
		return (getCalendarItem(item) != null);
	}
	
	public void putCalendarItem(CalendarItem item){
		if(!bookingInDB(item)){
			System.out.println("Yeah, nieuw calendar item!");
			newCalendarItem(item);
			
			/** check of het item in de db staat *//*
			if(!put.equals(coll.findOne())){
				System.err.println("Calendar put mislukt: " + coll.findOne().toString());
			} else {
				System.out.println("Calendar put");
			}*/
		} else {
			//System.out.println("Yeah, update calendar item!");
			updateCalendarItem(item);
		}
	}
	
	private void newCalendarItem(CalendarItem item){
		BasicDBObject put = newBasicDBObject(item);
		DBCollection coll = db.getCollection(CALENDARITEMS);
		coll.insert(put);
	}
	
	private void updateCalendarItem(CalendarItem item){
		//BasicDBObject newDocument = new BasicDBObject();
		
		//newDocument.append("$set", newBasicDBObject(item));
		//BasicDBObject searchQuery = new BasicDBObject().append(BOOKINGNR, item.getBookingnr());
		DBObject updated = getCalendarItem(item);
		//System.out.println("Calendar " + updated.toString());
		//db.getCollection(CALENDARITEMS).update(searchQuery, newDocument);
		db.getCollection(CALENDARITEMS).save(updateDBObject(item, updated));
	}
	
	private DBObject updateDBObject(CalendarItem item, DBObject updated){
		 updated.put(OPERATORID, DBoperatorid);
		 updated.put(TEMPLATETYPE, DBtemptype);
		 updated.put(TYPEID, 5);
				//put(ID, 12345).
				//put(CALLS, "12345").
		 updated.put(ALLDAY, false);
		 updated.put(START, item.getStart());
		 updated.put(END, item.getEind());
		 updated.put(BOOKINGNR, item.getBookingnr());
		 updated.put(CONTAINERNR, item.getContainernr());
		 updated.put(MRN, item.getMRN());
		 updated.put(KARTONS, item.getKartons());
		 updated.put(UNITS, item.getUnits());
		 updated.put(BESCHIKBAAR, item.getBeschikbaarOp());
		 updated.put(GASMETING, item.getGasmeting());
		 updated.put(CATEGORIE, item.getCategorie());
		 updated.put(OPMERKINGEN, item.getOpmerkingen());
		 
		 return updated;
	}
	
	private BasicDBObject newBasicDBObject(CalendarItem item){
		return new BasicDBObject(OPERATORID, DBoperatorid).
			append(TEMPLATETYPE, DBtemptype).
			append(TYPEID, 5).
			//append(ID, 12345).
			//append(CALLS, "12345").
			append(ALLDAY, false).
			append(START, item.getStart()).
			append(END, item.getEind()).
			append(BOOKINGNR, item.getBookingnr()).
			append(CONTAINERNR, item.getContainernr()).
			append(MRN, item.getMRN()).
			append(KARTONS, item.getKartons()).
			append(UNITS, item.getUnits()).
			append(BESCHIKBAAR, item.getBeschikbaarOp()).
			append(GASMETING, item.getGasmeting()).
			append(CATEGORIE, item.getCategorie()).
			append(OPMERKINGEN, item.getOpmerkingen());
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
	
	public void deleteObjects(){
		BasicDBObject searchQuery = new BasicDBObject().append(TEMPLATETYPE, DBtemptype);
		db.getCollection(CALENDARITEMS).remove(searchQuery);
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

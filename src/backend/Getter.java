package backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import calendar.CalendarItem;

public class Getter {
	
	Connector insight;
	Connector own;
	httppusher http;
	
	public Getter(){
		insight = new Connector("database.proprties");
		http = new httppusher("database.properties");
		own = new Connector("own.proprties");
	}
	
	/**
	 * Synchroniseert heel veel data vanuit allemaal db's
	 */
	public void synchronize(){
		String mongo = http.sendGet();
		System.out.println(mongo);
		if(mongo != null){
			for(CalendarItem c : Splitter.split(mongo)){
				//c.setBeschikbaarOp(0); //zet en bereken een nieuwe beschikbaar op
				System.out.println("Item; " + c.getContainernr());
				this.putCalandarItemOwn(c);
			}
		}
		for(CalendarItem c : this.getCalendarListInsight()){
			http.sendPost(c);
			
			System.out.println(c.toString());
			//http.sendPost(c);
		}
	}
	
	public static boolean bevat(List<CalendarItem> lijst, int hashkey){
		boolean result = false;
		for(CalendarItem c : lijst){
			if(Hasher.hash(c.getBookingnr(), c.getContainernr()) == hashkey){
				result = true;
			}
		}
		return result;
	}
	
	private List<CalendarItem> getCalendarListInsight(){
    	List<CalendarItem> result = new ArrayList<CalendarItem>();
    	String getContainersInsightQuery = "SELECT `Booking`, `Pickup`, `ContainerNumber`, `ImportDocNr`, `NumberColli`, arrivalPickup, gateOut, importGateInPickup FROM `modalitycontainerstatisticsinfocumm` WHERE `Client` = 'TIMBAL' AND `Pickup` > 1398942018000";
    	ResultSet rs = insight.query(getContainersInsightQuery);
    	
    	List<String[]> queryResult = insight.sqlGet(getContainersInsightQuery);
    	
    	/**
    	for(String[] s : queryResult){
    		int bookingnr = s[0].g; 
			long start = rs.getLong(2)/1000;//in insight staat de tijd in ms
			String containernr = rs.getString(3);
			String mrn = rs.getString(4);
			int kartons = rs.getInt(5);
			long arrivalPickup = rs.getLong(6)/1000;//in insight staat de tijd in ms
			long gateOut = rs.getLong(7)/1000;//in insight staat de tijd in ms
			long importGateInPickup = rs.getLong(8)/1000;//in insight staat de tijd in ms
    	}**/
    	
    	try {
			while(rs.next()){
				int bookingnr = rs.getInt(1); 
				long start = rs.getLong(2)/1000;//in insight staat de tijd in ms
				String containernr = rs.getString(3);
				String mrn = rs.getString(4);
				int kartons = rs.getInt(5);
				long arrivalPickup = rs.getLong(6)/1000;//in insight staat de tijd in ms
				long gateOut = rs.getLong(7)/1000;//in insight staat de tijd in ms
				long importGateInPickup = rs.getLong(8)/1000;//in insight staat de tijd in ms
				
				long eta =  ETAcalculator.eta(start, arrivalPickup, gateOut, importGateInPickup);
				
				CalendarItem booking;
				
				ResultSet rsown = own.query("SELECT planned, eta, units, gasmeting, categorie, status, opmerkingen FROM `test` WHERE hashkey = " + Hasher.hash(bookingnr, containernr));
				
				if(rsown.next()){
					start = rsown.getLong(1);
					
					int units = rsown.getInt(3);
					boolean gasmeting = rsown.getBoolean(4);
					char categorie = rsown.getString(5).toCharArray()[0];
					int status = rsown.getInt(6);
					String opmerkingen = rsown.getString(7);
					booking = new CalendarItem(bookingnr, start, containernr, mrn, kartons, units, eta, gasmeting, categorie, status, opmerkingen);
				} else {
					booking = new CalendarItem(bookingnr, start, containernr, mrn, kartons, eta);
				}
				
				//sluit de own db connectie, zou eignelijk in connecotr moeten
				own.Close();
				
				result.add(booking);
			}
		} catch (SQLException e) {
			System.out.println("error getter getCaelndatList: " + e.getMessage());
		}
    	//sluit de insigt db connectie, zou eignelijk in connector moeten
    	insight.Close();
    	return result;
    }
	
	private void putCalandarItemOwn(CalendarItem item){
		try {
			ResultSet result = own.query("SELECT * FROM test WHERE hashkey = " + Hasher.hash(item.getBookingnr(), item.getContainernr()));
			if (result.next()){
				own.putQuery("UPDATE test SET " + 
						"planned = " + item.getStart() + 
						", eta = " + item.getBeschikbaarOp() +
						", units = " + item.getUnits() +
						", gasmeting = " + item.getGasmeting() +
						", categorie = \"" + item.getCategorie() +
						"\", status = " + item.getStatus() + 
						", opmerkingen = \"" + item.getOpmerkingen() + 
						"\" WHERE hashkey = " + Hasher.hash(item.getBookingnr(), item.getContainernr()));
			} else {
				own.putQuery("INSERT INTO test (hashkey, planned, eta, units, gasmeting, categorie, status, opmerkingen) VALUES (" + 
						Hasher.hash(item.getBookingnr(), item.getContainernr()) + ", " +
						item.getStart() + ", " +
						item.getBeschikbaarOp() + ", " +
						item.getUnits() + ", " +
						item.getGasmeting() + ", " +
						"\""+ item.getCategorie() + "\", " +
						item.getStatus() + ", " +
						"\"" + item.getOpmerkingen() + "\")");
			}
			//Maak alle items die eerder ingepland staan dan dat ze beschikbaar zijn rood
			if(item.getBeschikbaarOp() < item.getStart()){
				//own.putQuery("UPDATE test SET status = 2 WHERE hashkey = " + Hasher.hash(item.getBookingnr(), item.getContainernr()));
			}
		} catch (SQLException e) {
			System.err.println("error putalendaritem: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		Getter get = new Getter();
		get.synchronize();
	}

}

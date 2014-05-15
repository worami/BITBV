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
		http = new httppusher();
		own = new Connector("own.proprties");
	}
	
	public void synchronize(){
		for(CalendarItem c : Splitter.split(http.sendGet())){
			this.putCalandarItemOwn(c);
		}
		
		for(CalendarItem c : this.getCalendarListInsight()){
			http.sendPost(c);
			
			//if(this.bevat(getCalendarListOwn(), hashkey)){
				
			//} else {
				
			//}
			
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
    	ResultSet rs = insight.query("SELECT `Booking`, `Pickup`, `ContainerNumber`, `ImportDocNr`, `NumberColli` FROM `modalitycontainerstatisticsinfocumm` WHERE `Client` = 'TIMBAL' AND `Pickup` > 1398942018000");
    	
    	try {
			while(rs.next()){
				int bookingnr = rs.getInt(1);
				long start = rs.getLong(2);
				String containernr = rs.getString(3);
				String mrn = rs.getString(4);
				int kartons = rs.getInt(5);
				
				CalendarItem booking;
				
				ResultSet rsown = own.query("SELECT planned, eta, units, gasmeting, categorie, status, opmerkingen FROM `test` WHERE hashkey = " + Hasher.hash(bookingnr, containernr));
				
				if(rsown.next()){
					start = rsown.getLong(1);
					long eta = rsown.getLong(2);
					int units = rsown.getInt(3);
					boolean gasmeting = rsown.getBoolean(4);
					char categorie = rsown.getString(5).toCharArray()[0];
					int status = rsown.getInt(6);
					String opmerkingen = rsown.getString(7);
					booking = new CalendarItem(bookingnr, start, containernr, mrn, kartons, units, eta, gasmeting, categorie, status, opmerkingen);
				} else {
					booking = new CalendarItem(bookingnr, start, containernr, mrn, kartons);
				}
				
				own.Close();
				
				result.add(booking);
			}
		} catch (SQLException e) {
			System.out.println("error getter getCaelndatList: " + e.getMessage());
		}
    	insight.Close();
    	return result;
    }
	
	private List<CalendarItem> getCalendarListOwn(int hashkey){
		List<CalendarItem> result = new ArrayList<CalendarItem>();
    	ResultSet rs = own.query("SELECT hashkey, planned, eta, units, gasmeting, categorie, status, opmerkingen FROM `test` WHERE hashkey = " + hashkey);
    	try {
			while(rs.next()){
				CalendarItem booking = new CalendarItem(rs.getInt(1), rs.getLong(2), rs.getString(3), rs.getString(4), rs.getInt(5));
				result.add(booking);
			}
		} catch (SQLException e) {
			System.out.println("error getter getCaelndatListown: " + e.getMessage());
		}
    	own.Close();
    	return result;
	}
	
	private void putCalandarItemOwn(CalendarItem item){
		try {
			ResultSet result = own.query("SELECT * FROM test WHERE hashkey = " + Hasher.hash(item.getBookingnr(), item.getContainernr()));
			if (result.next()){
				own.putQuery("UPDATE test SET " + 
						" planned = " + item.getStart() + 
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
		} catch (SQLException e) {
			System.err.println("error putalendaritem: " + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		Getter get = new Getter();
		CalendarItem item = new CalendarItem(12345, 1300000000, "TEST 123456 7", "TEST MRN", 1234, 12345, 1300000000, false, 'D', 1, "Test opmerking");
		
		get.putCalandarItemOwn(item);
	}

}

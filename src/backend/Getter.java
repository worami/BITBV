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
		
		for(CalendarItem c : this.getCalendarListInsight()){
			if(this.getCalendarListOwn().contains(c)){
				
			} else {
				http.sendPost(c);
			}
			
			System.out.println(c.toString());
			//http.sendPost(c);
		}
	}
	
	private List<CalendarItem> getCalendarListInsight(){
    	List<CalendarItem> result = new ArrayList<CalendarItem>();
    	ResultSet rs = insight.query("SELECT `Booking`, `Pickup`, `ContainerNumber`, `ImportDocNr`, `NumberColli` FROM `modalitycontainerstatisticsinfocumm` WHERE `Client` = 'TIMBAL' AND `Pickup` > 1398942018000");
    	try {
			while(rs.next()){
				CalendarItem booking = new CalendarItem(rs.getInt(1), rs.getLong(2), rs.getString(3), rs.getString(4), rs.getInt(5));
				result.add(booking);
			}
		} catch (SQLException e) {
			System.out.println("error getter getCaelndatList: " + e.getMessage());
		}
    	insight.Close();
    	return result;
    }
	
	private List<CalendarItem> getCalendarListOwn(){
		List<CalendarItem> result = new ArrayList<CalendarItem>();
    	ResultSet rs = own.query("SELECT `Booking`, `Pickup`, `ContainerNumber`, `ImportDocNr`, `NumberColli` FROM `modalitycontainerstatisticsinfocumm` WHERE `Client` = 'TIMBAL' AND `Pickup` > 1398942018000");
    	try {
			while(rs.next()){
				CalendarItem booking = new CalendarItem(rs.getInt(1), rs.getLong(2), rs.getString(3), rs.getString(4), rs.getInt(5));
				result.add(booking);
			}
		} catch (SQLException e) {
			System.out.println("error getter getCaelndatList: " + e.getMessage());
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

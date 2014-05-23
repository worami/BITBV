package dbconnections;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import backend.ETAcalculator;
import calendar.CalendarItem;

public class InsightConnector extends Connector {
	
	final String vanaf = "1398942018000"; //Datum vanaf waar de db wordt gelezen
	
	public InsightConnector(String properties) {
		super(properties);
	}
	
	/**
	 * Een lijst met calendarItems vanuit de modality db
	 * @return
	 */
	public List<CalendarItem> getCalendarList(){
    	List<CalendarItem> result = new ArrayList<CalendarItem>();
    	this.Connect();
    	try {
			rs = st.executeQuery("SELECT "
					+ "`Booking`, "
					+ "`Pickup`, "
					+ "`ContainerNumber`, "
					+ "`ImportDocNr`, "
					+ "`NumberColli` "
					+ "FROM  " + this.getTabel() + " "
					+ "WHERE `Client` = 'TIMBAL' AND `Pickup` > " + this.vanaf);
			while(rs.next()){
				//System.out.println("Calendar start: " + rs.getLong(2) + " CNR: " + rs.getString(3));
				CalendarItem booking = new CalendarItem(
						rs.getInt(1), 
						rs.getLong(2)/1000 + 3*ETAcalculator.DAY, 
						rs.getString(3), 
						rs.getString(4), 
						rs.getInt(5), 0);
				result.add(booking);
			}
		} catch (SQLException e) {
			System.err.println("Error getCalendarList: " + e.getMessage());
		}
    	this.Close();
    	return result;
    }
	
	/**
	 * Funcite om de ETA variebelen mee op te vragen uit de database van een bepaald calendarItem
	 * @param item waarvan je de variabelen wilt hebben
	 * @return Array met drie long's, long[0] = arrivalPickup, long[1] = gateOuten long[2] = importGateInPickup
	 */
	public long[] getETAinfo(CalendarItem item){
		long[] result = new long[4];
		this.Connect();
    	try {
			rs =st.executeQuery("SELECT "
					+ "pickup, "
					+ "arrivalPickup, "
					+ "gateOut, "
					+ "importGateInPickup "
					+ "FROM " + getTabel() + " "
					+ "WHERE `Client` = 'TIMBAL' "
					+ "AND Booking = " + item.getBookingnr() 
					+ " AND ContainerNumber = \"" + item.getContainernr() + "\"");
			if(rs.next()){
				result[0] = rs.getLong(1)/1000;
				result[1] = rs.getLong(2)/1000;
				result[2] = rs.getLong(3)/1000;
				result[3] = rs.getLong(4)/1000;
 			}
		} catch (SQLException e) {
			System.err.println("Error insightConnector getETAinfo: " + e.getMessage());
		}
    	this.Close();
		return result;
	}
	
	public static void updateDate(){
		OwnConnector date = new OwnConnector("database.proprties");
		SimpleDateFormat fromDB = new SimpleDateFormat("dd-MM-yyyy HH:mm Z");
		String datum;
		String tijd;
		int booking;

		List<String> queries = new ArrayList<String>();
		
		date.Connect();
		try {
			date.rs = date.st.executeQuery("SELECT "
					+ "PickupDate, "
					+ "PickupTime, "
					+ "Booking "
					+ "FROM "  + date.getTabel()
					+ " WHERE Client = 'TIMBAL' AND Pickup IS NULL");
			while(date.rs.next()){
				datum = date.rs.getString(1);
				tijd = date.rs.getString(2);
				booking = date.rs.getInt(3);
				if(datum.length() >1 && tijd.length() > 1 && booking > 100){
					Date nieuw = fromDB.parse(datum + " " + tijd + " +0200");
					queries.add("UPDATE " + date.getTabel() + " SET " + 
						"Pickup = " + nieuw.getTime() +  " WHERE Booking = " + booking);
				}
			}
		} catch (SQLException e) {
			System.err.println("error ownconnection getcalendardate: " + e.getMessage());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		date.Close();
		
		for(String s : queries){
			date.Connect();
			try {
			    date.st.executeUpdate(s);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			date.Close();
		}
		
	}
	
	public static void main(String[] args){
		updateDate();
	}

}

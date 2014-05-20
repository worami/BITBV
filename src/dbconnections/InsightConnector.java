package dbconnections;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
				System.out.println("Calendar start: " + rs.getLong(2) + " CNR: " + rs.getString(3));
				CalendarItem booking = new CalendarItem(
						rs.getInt(1), 
						rs.getLong(2)/1000, 
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
		long[] result = new long[3];
		this.Connect();
    	try {
			rs =st.executeQuery("SELECT "
					+ "arrivalPickup, "
					+ "gateOut, "
					+ "importGateInPickup "
					+ "FROM " + getTabel() + " "
					+ "WHERE `Client` = 'TIMBAL' "
					+ "AND Booking = " + item.getBookingnr() 
					+ " AND ContainerNumber = \"" + item.getContainernr() + "\"");
			if(rs.next()){
				result[0] = rs.getLong(1);
				result[1] = rs.getLong(2);
				result[2] = rs.getLong(3);
 			}
		} catch (SQLException e) {
			System.err.println("Error insightConnector getETAinfo: " + e.getMessage());
		}
    	this.Close();
		return result;
	}

}

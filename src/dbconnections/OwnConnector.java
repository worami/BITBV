package dbconnections;

import java.sql.SQLException;

import calendar.CalendarItem;
import backend.Hasher;

public class OwnConnector extends Connector {

	public OwnConnector(String properties) {
		super(properties);
	}
	
	/**
	 * Update het calendarItem met de data beschikbaar in de eigen db
	 * @param item
	 */
	public void getCalendarData(CalendarItem item){
		this.Connect();
		try {
			rs = st.executeQuery("SELECT "
					+ "planned, "
					+ "eta, "
					+ "units, "
					+ "gasmeting, "
					+ "categorie, "
					+ "status, "
					+ "opmerkingen, "
					+ "mongoid "
					+ "FROM "  + this.getTabel()
					+ " WHERE hashkey = " + Hasher.hash(item.getBookingnr(), item.getContainernr()));
			if(rs.next()){
				item.setStart(rs.getLong(1));
				item.setUnits(rs.getInt(3));
				item.setGasmeting(rs.getBoolean(4));
				item.setCategorie(rs.getString(5).toCharArray()[0]);
				item.setStatus(rs.getInt(6));
				item.setOpmerkingen(rs.getString(7));
				item.setMondoID(rs.getString(8));
			}
		} catch (SQLException e) {
			System.err.println("error ownconnection getcalendardate: " + e.getMessage());
		}
		this.Close();
	}
	
	/**
	 * Update de info over een calendarItem in de db of maak een nieuwe aan
	 * @param item
	 */
	public void putCalendarItem(CalendarItem item){
		if(calendarItemBestaat(item)){
			updateCalendarItem(item);
		} else {
			newCalendarItem(item);
		}
	}

	/**
	 * Maak een nieuw calendarItem aan in de db
	 * @param item
	 */
	private void newCalendarItem(CalendarItem item){
		this.Connect();
		try {
			st.executeUpdate("INSERT INTO " + this.getTabel() + " (hashkey, planned, eta, units, gasmeting, categorie, status, opmerkingen, mongoid) VALUES (" + 
					Hasher.hash(item.getBookingnr(), item.getContainernr()) + ", " +
					item.getStart() + ", " +
					item.getBeschikbaarOp() + ", " +
					item.getUnits() + ", " +
					item.getGasmeting() + ", " +
					"\""+ item.getCategorie() + "\", " +
					item.getStatus() + ", " +
					"\"" + item.getOpmerkingen() + "\", " +
					"\"" + item.getMondoID() + "\")");
		} catch (SQLException e) {
			System.err.println("error ownconnector newCalendarItem: " + e.getMessage());
		}
		this.Close();
	}
	
	/**
	 * update de info van een calendarItem in de db
	 * @param item
	 */
	private void updateCalendarItem(CalendarItem item){
		this.Connect();
		try {
			st.executeUpdate("UPDATE " + this.getTabel() + " SET " + 
					"planned = " + item.getStart() + 
					", eta = " + item.getBeschikbaarOp() +
					", units = " + item.getUnits() +
					", gasmeting = " + item.getGasmeting() +
					", categorie = \"" + item.getCategorie() +
					"\", status = " + item.getStatus() + 
					", opmerkingen = \"" + item.getOpmerkingen() + 
					"\", mongoid = \"" + item.getMondoID() +
					"\" WHERE hashkey = " + Hasher.hash(item.getBookingnr(), item.getContainernr()));
		} catch (SQLException e) {
			System.err.println("error ownConnector updateCalendarItem: " + e.getMessage());
		}
		this.Close();
	}
	
	/**
	 * kijk of er al een calendaritem in de db bestaat met hetzelfde containernummer en bookingnummer
	 * @param item
	 * @return
	 */
	public boolean calendarItemBestaat(CalendarItem item){
		boolean result = false;
		this.Connect();
		try {
			rs = st.executeQuery("SELECT * FROM " + this.getTabel()
					+ " WHERE hashkey = " + Hasher.hash(item.getBookingnr(), item.getContainernr()));
			result = rs.next();
		} catch (SQLException e) {
			System.err.println("error calendarItemBestaat: " + e.getMessage());
		}
		this.Close();
		return result; 
	}
}

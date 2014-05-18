package dbconnections;

import java.sql.ResultSet;
import java.sql.SQLException;

import calendar.CalendarItem;
import backend.Hasher;

public class OwnConnector extends Connector {

	public OwnConnector(String properties) {
		super(properties);
	}
	
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
					+ "opmerkingen "
					+ "FROM `test` "
					+ "WHERE hashkey = " + Hasher.hash(item.getBookingnr(), item.getContainernr()));
			if(rs.next()){
				item.setStart(rs.getLong(1));
				item.setUnits(rs.getInt(3));
				item.setGasmeting(rs.getBoolean(4));
				item.setCategorie(rs.getString(5).toCharArray()[0]);
				item.setStatus(rs.getInt(6));
				item.setOpmerkingen(rs.getString(7));
			}
		} catch (SQLException e) {
			System.err.println("eroor ownconnectio getcalendardate: " + e.getMessage());
		}
		this.Close();
	}
	
	public void putCalendarItem(CalendarItem item){
		if(calendarItemBestaat(item)){
			updateCalendarItem(item);
		} else {
			newCalendarItem(item);
		}
	}

	private void newCalendarItem(CalendarItem item){
		this.Connect();
		try {
			st.executeUpdate("INSERT INTO test (hashkey, planned, eta, units, gasmeting, categorie, status, opmerkingen) VALUES (" + 
					Hasher.hash(item.getBookingnr(), item.getContainernr()) + ", " +
					item.getStart() + ", " +
					item.getBeschikbaarOp() + ", " +
					item.getUnits() + ", " +
					item.getGasmeting() + ", " +
					"\""+ item.getCategorie() + "\", " +
					item.getStatus() + ", " +
					"\"" + item.getOpmerkingen() + "\")");
		} catch (SQLException e) {
			System.err.println("error ownconnector newCalendarItem: " + e.getMessage());
		}
		this.Close();
	}
	
	private void updateCalendarItem(CalendarItem item){
		this.Connect();
		try {
			st.executeUpdate("UPDATE test SET " + 
					"planned = " + item.getStart() + 
					", eta = " + item.getBeschikbaarOp() +
					", units = " + item.getUnits() +
					", gasmeting = " + item.getGasmeting() +
					", categorie = \"" + item.getCategorie() +
					"\", status = " + item.getStatus() + 
					", opmerkingen = \"" + item.getOpmerkingen() + 
					"\" WHERE hashkey = " + Hasher.hash(item.getBookingnr(), item.getContainernr()));
		} catch (SQLException e) {
			System.err.println("error ownConnector updateCalendarItem: " + e.getMessage());
		}
		this.Close();
	}
	
	public boolean calendarItemBestaat(CalendarItem item){
		boolean result = false;
		this.Connect();
		try {
			rs = st.executeQuery("SELECT * FROM test "
					+ "WHERE hashkey = " + Hasher.hash(item.getBookingnr(), item.getContainernr()));
			result = rs.next();
		} catch (SQLException e) {
			System.err.println("error calendarItemBestaat: " + e.getMessage());
		}
		this.Close();
		return result; 
	}
}

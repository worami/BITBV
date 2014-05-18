package backend;

import java.util.List;

import calendar.CalendarItem;
import dbconnections.InsightConnector;
import dbconnections.OwnConnector;

public class Getter {
	
	InsightConnector insight;
	OwnConnector own;
	httppusher http;
	
	public Getter(){
		insight = new InsightConnector("database.proprties");
		http = new httppusher("database.properties");
		own = new OwnConnector("own.proprties");
	}
	
	/**
	 * Synchroniseert heel veel data vanuit allemaal db's
	 */
	public void synchronize(){
		updateLokaleDB();
		updateETA();
		pushNaarApplicatie();
	}
	
	private void updateLokaleDB(){
		String mongo = http.sendGet();
		if(mongo != null){
			for(CalendarItem c : Splitter.split(mongo)){
				//update de lokale db
				own.putCalendarItem(c);
			}
		}
	}
	
	private void updateETA(){
		for(CalendarItem item : this.getCompleteCalendarList()){
			if(item.getBeschikbaarOp() < item.getStart()){
				item.setStatus(2);
			}
		}
	}
	
	private void pushNaarApplicatie(){
		for(CalendarItem item : this.getCompleteCalendarList()){
			http.sendPost(item);
		}
	}
		
	public List<CalendarItem> getCompleteCalendarList(){
		List<CalendarItem> result = insight.getCalendarList();
		for(CalendarItem item : result){
			own.getCalendarData(item);
			
			//Update de ETA informatie
			long[] etavar = insight.getETAinfo(item);
			item.setBeschikbaarOp(ETAcalculator.eta(item.getStart(), etavar[0], etavar[1], etavar[2]));
		}
		return result;
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
	
	public static void main(String[] args) {
		Getter get = new Getter();
		get.synchronize();
	}

}

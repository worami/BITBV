package backend;

import java.util.List;

import calendar.CalendarItem;
import dbconnections.InsightConnector;
import dbconnections.OwnConnector;

public class Getter {
	
	InsightConnector insight;
	OwnConnector own;
	HttpPusher http;
	HttpPusher httplist;
	
	
	public Getter(){
		insight = new InsightConnector("database.proprties");
		http = new HttpPusher("database.proprties");
		own = new OwnConnector("own.proprties");
		httplist = new HttpPusher("httplist.proprties");
	}
	
	/**
	 * Synchroniseert heel veel data vanuit allemaal db's
	 */
	public void synchronize(){
		updateLokaleDB();
		updateETA();
		updateStart();
		pushNaarApplicatie();
	}
	
	private void updateLokaleDB(){
		String mongo = http.sendGet();
		//System.out.println(mongo);
		if(mongo != null){
			for(CalendarItem c : Splitter.split(mongo)){
				//update de lokale db
				own.putCalendarItem(c);
			}
		}
	}
	
	private void updateETA(){
		for(CalendarItem item : this.getCompleteCalendarList()){	
			if(item.getStatus() == CalendarItem.STATUSACTIEVEREIST){
				if(item.getBeschikbaarOp()-item.getStart() > ETAcalculator.DAY){
					item.setStatus(CalendarItem.STATUSVERTRAGING); 
					item.setOpmerkingen(item.getOpmerkingen() + " Error te vroeg ingepland");
				} 
			} if(item.getStatus() == CalendarItem.STATUSGOEDGEKEURD){
				if(item.getBeschikbaarOp()-item.getStart() <= ETAcalculator.DAY && item.getBeschikbaarOp()-item.getStart() >= 0 ){
					item.setStatus(CalendarItem.STATUSVOORSTELSPOED);
				} else if(item.getBeschikbaarOp() > item.getStart()){
					item.setStatus(CalendarItem.STATUSVERTRAGING);
					item.setOpmerkingen(item.getOpmerkingen() + " Error er is een vertraging bij de NS");
				}
			}
			own.putCalendarItem(item);
			http.sendUpdate(item);
		}
	}
	
	private void updateStart(){
		List<CalendarItem> lijst = this.getCompleteCalendarList();
		for(CalendarItem item : lijst){
			
			if(item.getStatus() == CalendarItem.STATUSLEEG){
				item.setStart(PlanningCalculator.calculateFirstPossibility(item.getStart()));
				PlanningCalculator.moveToFirstFreeTimeSlot(item, lijst);
			}
			own.putCalendarItem(item);
			http.sendUpdate(item);
		}
	}
	
	/**
	 * Push alle calendarItems naar de applicatie
	 * Er kunnen nieuwe tussen zitten
	 */
	private void pushNaarApplicatie(){
		for(CalendarItem item : this.getCompleteCalendarList()){
			http.sendPost(item);
		}
	}
	
	/** 
	 * Verwijder al onze (Temptype status en typeid 23) calendaritems uit de applicatie
	 */
	public void ruimDatabaseOp(){
		for(CalendarItem item : Splitter.split(http.sendGet())){
			//System.out.println(item.toString());
			http.sendDelete(item);
		}
		own.clearDatabase();
	}
		
	public List<CalendarItem> getCompleteCalendarList(){
		List<CalendarItem> result = insight.getCalendarList();
		for(CalendarItem item : result){
			own.getCalendarData(item);
			
			//Update de ETA informatie
			long[] etavar = insight.getETAinfo(item);
			item.setBeschikbaarOp(ETAcalculator.eta(etavar[0], etavar[1], etavar[2], etavar[3]));
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
		
		
		//get.ruimDatabaseOp();
		//System.out.println(get.http.sendGet());
		
	}

}

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
	Mailer mail;
	
	public Getter(){
		insight = new InsightConnector("database.proprties");
		http = new HttpPusher("database.proprties");
		own = new OwnConnector("own.proprties");
		httplist = new HttpPusher("httplist.proprties");
		mail = new Mailer("database.proprties");
	}
	
	/**
	 * Synchroniseert heel veel data vanuit allemaal db's
	 */
	public void synchronize(){
		updateLokaleDB();
		//updateETA();
		updateStatus();
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
	
	private void updateStatus(){
		for(CalendarItem item : this.getCompleteCalendarList()){
			boolean update = insight.getIsBezorgd(item);
			if(item.getStatus() == CalendarItem.STATUSACTIEVEREIST){
				//doet niks
			}
			if(item.getStatus() == CalendarItem.STATUSVOORSTEL){
				//Zet op spoed als een dag voor beschikbaar op wordt ingepland
				if(item.getStart() > (item.getBeschikbaarOp()-ETAcalculator.DAY) && item.getStart() < item.getBeschikbaarOp()){
					item.setSpoed(true);
					update = true;
				}
			}
			if(item.getStatus() == CalendarItem.STATUSGOEDGEKEURD || item.getStatus() == CalendarItem.STATUSVOORSTEL ){
				if(item.getStart() < item.getBeschikbaarOp() && !item.getSpoed()){
					item.setStatus(CalendarItem.STATUSVERTRAGING);
					mail.composeMail(item, "Deze container is vertraagd, plan deze opnieuw in");
					update = true;
				} else if(item.getStart() <= (item.getBeschikbaarOp()-ETAcalculator.DAY) && item.getSpoed()){
					item.setStatus(CalendarItem.STATUSVERTRAGING);
					mail.composeMail(item, "Deze container is vertraagd, plan deze opnieuw in");
					update = true;
				}
			}
			//update alle item waar iets aan is veranderd
			if(update){
				own.putCalendarItem(item);
				http.sendUpdate(item);
			}
		}
	}
	private void updateStart(){
		List<CalendarItem> lijst = this.getCompleteCalendarList();
		for(CalendarItem item : lijst){
			if(item.getStatus() == CalendarItem.STATUSACTIEVEREIST){
				item.setStart(PlanningCalculator.calculateFirstPossibility(item.getStart()));
				PlanningCalculator.moveToFirstFreeTimeSlot(item, lijst);
				own.putCalendarItem(item);
				http.sendUpdate(item);
			}
		}
	}
	
	private void updateGasPercentage(){
		System.out.println(GasCalculator.calculate(this.getCompleteCalendarList(),'C', 0, 999999999999L));
	}
	
	/**
	 * Push alle calendarItems naar de applicatie
	 * Er kunnen nieuwe tussen zitten
	 */
	private void pushNaarApplicatie(){
		for(CalendarItem item : this.getCompleteCalendarList()){
			http.sendPost(item);
			httplist.sendPost(item);
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
		//voeg ook de oude lijst toe
		//result.addAll(insight.getCalendarListOud());
		for(CalendarItem item : result){
			own.getCalendarData(item);
			
			//Update de ETA informatie
			long[] etavar = insight.getETAinfo(item);
			item.setBeschikbaarOp(ETAcalculator.eta(etavar[0], etavar[1], etavar[2], etavar[3]));
		}
		return result;
	}
	
	/**
	 * 
	 * @param lijst
	 * @param hashkey
	 * @return
	 */
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
		//get.mail.composeMail(get.getCompleteCalendarList().get(1), "Dit was een random item!");
		//get.updateGasPercentage();
		
		//get.ruimDatabaseOp();
		//System.out.println(get.http.sendGet());
		
	}

}

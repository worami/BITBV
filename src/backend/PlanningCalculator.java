package backend;

import java.util.*;

import calendar.CalendarItem;

public class PlanningCalculator {
	
	private static final int MAX_HOUR_SEPARATION = 13;
	private static final int MAX_HALFHOUR_SEPARATION = 27; //2 x bovenstaande + 1
	private static final int MAX_15MIN_SEPARATION = 55; //2 x bovenstaande + 1
	
	
	public static void main(String[] args) {
		//test getFirstPossibility
		/*Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 4, 20, 13, 12);
		System.out.println(calculateFirstPossibility(calendar.getTimeInMillis()/1000));
		calendar.set(2014, 4, 18, 6, 51);
		System.out.println(calculateFirstPossibility(calendar.getTimeInMillis()/1000));
		calendar.set(2014, 4, 17, 2, 9);
		System.out.println(calculateFirstPossibility(calendar.getTimeInMillis()/1000));
		calendar.set(2014, 4, 20, 2, 9);
		System.out.println(calculateFirstPossibility(calendar.getTimeInMillis()/1000));
		calendar.set(2014, 4, 20, 21, 9);
		System.out.println(calculateFirstPossibility(calendar.getTimeInMillis()/1000));
		calendar.set(2014, 4, 23, 21, 9);
		System.out.println(calculateFirstPossibility(calendar.getTimeInMillis()/1000));*/
	}
	
	/**
	 * Levert de eerstvolgende mogelijke bezorgtijd op. Verwacht aangeleverde tijd in seconden.
	 * @param st
	 * @return
	 */
	public static long calculateFirstPossibility(long time) { //TODO deze methode is lelijk, kan een stuk netter opgeschreven worden.
		long result;
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time*1000);
		//System.out.println("Voor: " + cal.getTime() + "; " + time);
		
		if (cal.get(Calendar.HOUR_OF_DAY) < 6) {
			//Bezorgtijd is voor 6 uur, zet tijd op 6 uur. Dit verlaat de container alleen.
			cal.set(Calendar.HOUR_OF_DAY, 6);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
		
		if (cal.get(Calendar.HOUR_OF_DAY) > 20 ||
				(cal.get(Calendar.HOUR_OF_DAY) == 20 && cal.get(Calendar.MINUTE) > 0)
			) {
			//Bezorgtijd is na 8 uur 's avonds, verlaat tijd tot volgende dag 6 uur 's ochtends
			cal.add(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 6);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
		
		while (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			//Als de dag zaterdag of zondag is, zet de dag op maandag
			cal.add(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 6);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
		
		result = cal.getTimeInMillis()/1000;
		//System.out.println("Na:   " + cal.getTime() + "; " + result);
		
		return result;
	}
	
	/**
	 * Verplaatst container toMove naar achteren indien er binnen één uur ervoor of één uur er na containers gepland staan.
	 * Zoekt automatisch een goede timeslot om de container neer te zetten. 
	 * @param toMove Mogelijk te verplaatsen container
	 */
	public static void moveToFirstFreeTimeSlot(CalendarItem toMove, List<CalendarItem> list) {
		int separation; //scheiding tussen twee containers; 60 betekent een container per uur, 30 betekent een container per halfuur, 15 betekent een container per kwartier. Geen andere waarden in zetten.
		Calendar time = Calendar.getInstance();
		Calendar initialTime = Calendar.getInstance();
		time.setTimeInMillis(calculateFirstPossibility(toMove.getStart())*1000);
		initialTime.setTimeInMillis(calculateFirstPossibility(toMove.getStart())*1000);
		
		//Calendar toMoveCal = Calendar.getInstance();
		//toMoveCal.setTimeInMillis(toMove.getStart()*1000);
				
		//TreeMap<CalendarItem, Calendar> map = new TreeMap<CalendarItem,Calendar>();
		int dayCounter = 0;
		ArrayList<Calendar> arlist = new ArrayList<Calendar>();
		for (CalendarItem item : list) {
			if (!item.equals(toMove)) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(item.getStart()*1000);
				if (time.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
						&& time.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
						&& time.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH)) {
					dayCounter++;
				}
				
				arlist.add(cal);
			}
		}
		
		//zet de separation op de juiste waarde
		if (dayCounter > MAX_15MIN_SEPARATION) {
			separation = 0; //als er echt veel te veel containers zijn, wordt de separation 0 en wordt hij gewoon ingepland op de eerstvolgende mogelijkheid
		} else if (dayCounter > MAX_HALFHOUR_SEPARATION) {
			separation = 15; //als er redelijk veel containers zijn, wordt hij ingepland op het eerstvolgende mogelijke kwartier
		} else if (dayCounter > MAX_HOUR_SEPARATION) {
			separation = 30; //als er lichtelijk veel containers zijn, wordt hij ingepland op het eerstvolgende mogelijke halfuur
		} else {
			separation = 60; //als er weinig containers zijn, wordt hij ingepland op het eerstvolgende mogelijke uur.
		}
		
		boolean found = false;
		
		
		
		while (!found && separation > 0) { //als separation == 0 heeft het toch geen zin om de hele loop te doorlopen.
			boolean okay = true;
			
			Calendar beforeCal = (Calendar) time.clone(); //is een Calendar met een tijd van SEPARATION minuten voor time.
			beforeCal.add(Calendar.MINUTE, separation*-1);
			
			Calendar afterCal = (Calendar) time.clone(); //is een Calendar met een tijd van SEPARATION minuten na time.
			afterCal.add(Calendar.MINUTE, separation);
			
			for (Calendar c : arlist) {
				if (c.after(beforeCal) && c.before(afterCal)) {
					okay = false;
				}
			}
			
			if (okay) {
				found = true;
			} else {
				time.add(Calendar.MINUTE, 15);
				time.setTimeInMillis(calculateFirstPossibility(time.getTimeInMillis()/1000)*1000);
			}
		}
		
		//kijk tenslotte of de container niet over een dag heen is getild; als dit wel zo is wordt de container weer teruggeplaatst naar de eerstvolgende mogelijkheid die in het begin is berekend.
		if (time.get(Calendar.DAY_OF_MONTH) == initialTime.get(Calendar.DAY_OF_MONTH)) {
			toMove.setStart(time.getTimeInMillis()/1000);
		} else {
			toMove.setStart(initialTime.getTimeInMillis()/1000);
		}
	}
}

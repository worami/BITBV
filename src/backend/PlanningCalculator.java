package backend;

import java.util.*;

import calendar.CalendarItem;

public class PlanningCalculator {
	
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
		System.out.println("Voor: " + cal.getTime() + "; " + time);
		
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
		System.out.println("Na:   " + cal.getTime() + "; " + result);
		
		return result;
	}
	
	/**
	 * Verplaatst container toMove naar achteren indien er binnen één uur ervoor of één uur er na containers gepland staan.
	 * Zoekt automatisch een goede timeslot om de container neer te zetten. 
	 * @param toMove Mogelijk te verplaatsen container
	 */
	public static void moveToFirstFreeTimeSlot(CalendarItem toMove, List<CalendarItem> list) {
		
		//TreeMap<CalendarItem, Calendar> map = new TreeMap<CalendarItem,Calendar>();
		ArrayList<Calendar> arlist = new ArrayList<Calendar>();
		for (CalendarItem item : list) {
			if (!item.equals(toMove)) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(item.getStart()*1000);
				//map.put(item, cal);
				
				arlist.add(cal);
			}
		}
		
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(calculateFirstPossibility(toMove.getStart())*1000);
		boolean found = false;
		
		
		
		while (!found) {
			boolean okay = true;
			
			Calendar beforeCal = (Calendar) time.clone(); //is een Calendar met een tijd van 1 uur voor time.
			beforeCal.add(Calendar.HOUR_OF_DAY, -1);
			
			Calendar afterCal = (Calendar) time.clone(); //is een Calendar met een tijd van 1 uur na time.
			afterCal.add(Calendar.HOUR_OF_DAY, 1);
			
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
		
		toMove.setStart(time.getTimeInMillis()/1000);
	}
}

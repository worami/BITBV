package backend;

import java.util.*;

public class PlanningCalculator {
	
	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, 4, 20, 13, 12);
		System.out.println(getFirstPossibility(calendar.getTimeInMillis()/1000));
		calendar.set(2014, 4, 18, 6, 51);
		System.out.println(getFirstPossibility(calendar.getTimeInMillis()/1000));
		calendar.set(2014, 4, 17, 2, 9);
		System.out.println(getFirstPossibility(calendar.getTimeInMillis()/1000));
		calendar.set(2014, 4, 20, 2, 9);
		System.out.println(getFirstPossibility(calendar.getTimeInMillis()/1000));
		calendar.set(2014, 4, 20, 21, 9);
		System.out.println(getFirstPossibility(calendar.getTimeInMillis()/1000));
		calendar.set(2014, 4, 23, 21, 9);
		System.out.println(getFirstPossibility(calendar.getTimeInMillis()/1000));
		
		
	}
	
	/**
	 * Levert de eerstvolgende mogelijke bezorgtijd op. Verwacht aangeleverde tijd in seconden.
	 * @param st
	 * @return
	 */
	public static long getFirstPossibility(long time) {
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
}

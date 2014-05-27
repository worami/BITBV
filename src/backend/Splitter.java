package backend;

import java.util.*;
import calendar.CalendarItem;

/**
 * Klasse om die enorme string te splitten in ContainerItems
 * @author alexander
 *
 */
public class Splitter {
	private static String testString1 = "[{Dit is String 1},{Dit is String 2},{Dit is String, 3},{Dit is String 4}]";
	private static HttpPusher pusher = new HttpPusher("database.proprties");
	private static boolean printMode = false; //hiermee toggle je of de methode split() output moet leveren - kan helpen met debuggen
	
	public static void main(String[] args) {
		long timeStart = System.currentTimeMillis();
		ArrayList<CalendarItem> lijst = split(pusher.sendGet());
		for (CalendarItem i:lijst) {
			System.out.println(i);
		}
		long time = System.currentTimeMillis() - timeStart;
		System.out.println("Time elapsed: " + time + "ms");
		
	}
	
	public static ArrayList<CalendarItem> split(String str) {
		ArrayList<CalendarItem> result = new ArrayList<CalendarItem>();
		str = str.length() > 2 ? str.substring(2, str.length()-2) : "";
		
		Scanner scanner = new Scanner(str);
		scanner.useDelimiter("\\},\\{");
		while(scanner.hasNext()) {
			String next = scanner.next();
			if (next.contains("\"operatorid\":23") && next.contains("\"templatetype\":\"statussupplychain\"")) {
				String[] fields_s = next.split(",");
				TreeMap<String,String> fields_t = new TreeMap<String,String>();
				for (String x:fields_s) {
					int colon = x.indexOf(":");
					String key = x.substring(1,colon - 1);
					
					String value = x.substring(colon + 1);
					if (value.charAt(0) == '"') {
						value = value.substring(1, value.length()-1);
					}
					if (printMode) {
						String print = key + "                    ";
						print = print.substring(0,21);
						print+=value;
						System.out.println(print);
					}
					
					fields_t.put(key, value);
				}	
				result.add(makeCalendarItem(fields_t));
				if (printMode) {
					System.out.println();
				}
			}
		}
		scanner.close();
		return result;
	}
	/**
	 * Levert een CalendarItem op met als variabelen de meegegeven TreeMap.
	 * LET OP: kan verscheidene Exceptions gooien bij foutieve invoer.
	 * Als er een foutieve invoer is, levert deze methode een ContainerItem op met veel dingen op 0
	 * @param map Een TreeMap met als key de naam van velden, en als value de waarde van velden.
	 * @return Een CalendarItem met als variabelen de meegegeven TreeMap
	 */
	private static CalendarItem makeCalendarItem(TreeMap<String, String> map) {
		//default waardes
		int bookingnr = 0;
		long start = 0;
		int kartons = 0;
		int status = 0;
		int units = 0;
		long beschikbaarop = 0;
		boolean gasmeting = false;
		char categorie = 'C';
		boolean spoed = false;
		int pickupdock = 0;
		int dropoffdock = 0;
		String mrn = map.get(CalendarItem.MRN); //deze mag evt leeg zijn
		String opmerkingen = map.get(CalendarItem.OPMERKINGEN); //deze mag null zijn
		
		//Alle getallen checken - als map.get() een null oplevert komt hier ergens een numberformatexception
		try {
			bookingnr = Integer.parseInt(map.get(CalendarItem.BOOKINGNR));
			start = Long.parseLong(map.get(CalendarItem.START));
			kartons = Integer.parseInt(map.get(CalendarItem.KARTONS));
			status = Integer.parseInt(map.get(CalendarItem.TYPEID));
			units = Integer.parseInt(map.get(CalendarItem.UNITS));
		} catch (NumberFormatException e) {
			System.out.println("NumberFormatException in makeCalendarItem: " + e);
		}
		
		//Alle booleans checken - deze moeten per stuk
		if (map.get(CalendarItem.GASMETING) != null) {
			gasmeting = Boolean.parseBoolean(map.get(CalendarItem.GASMETING));
		} else {
			System.out.println("Boolean gasmeting niet gevonden in makeCalendarItem()");
		}
		
		//Containernr en mongoid checken
		String containernr = map.get(CalendarItem.CONTAINERNR); //deze mag absoluut niet leeg zijn
		String mongoid = map.get(CalendarItem.MONGOID); //deze kan geen null zijn, maar kan geen kwaad om te checken
		if (containernr == null || mongoid == null) {
			System.out.println("Er was in makeCalendarItem een containernr of mongoid op null");
			return null;
		}
		
		//Categorie checken
		try {
			categorie = map.get(CalendarItem.CATEGORIE).charAt(0);
		} catch (NullPointerException e) {
			System.out.println("Er is een char die niet gelezen kon worden in makeCalendarItem(): " + e);
		}
		
		//Spoed
		try {
			spoed = Boolean.parseBoolean(map.get(CalendarItem.SPOED));
		} catch (NullPointerException e) {
			System.out.println("Er is een char die niet gelezen kon worden in makeCalendarItem(): " + e);
		}
		
		//pickup-/dropoffdock
		try {
			pickupdock = Integer.parseInt(CalendarItem.PICKUPDOCK);
			dropoffdock = Integer.parseInt(CalendarItem.DROPOFFDOCK);
			
			spoed = Boolean.parseBoolean(map.get(CalendarItem.SPOED));
		} catch (NumberFormatException e) {
			System.out.println("Er is een char die niet gelezen kon worden in makeCalendarItem(): " + e);
		}
		
		return new CalendarItem(bookingnr, start, containernr, mrn, kartons, units, beschikbaarop, gasmeting, categorie, status, opmerkingen, mongoid, spoed, pickupdock, dropoffdock);
	}
}

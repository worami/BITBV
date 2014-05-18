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
		
		Scanner scanner = new Scanner(str).useDelimiter("\\},\\{");
		while(scanner.hasNext()) {
			String next = scanner.next();
			if (next.contains("\"operatorid\":23") && next.contains("\"templatetype\":\"status\"")) {
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
		
		return result;
	}
/**
 * @require DAT ALLE SHIT KLOPT YO
 * @ensure DAT ALLE SHIT HIERNA MOGELIJK OOK KLOPT YO!!1
 */
	private static CalendarItem makeCalendarItem(TreeMap<String, String> map) {
		int bookingnr = Integer.parseInt(map.get(CalendarItem.BOOKINGNR));
		long start = Long.parseLong(map.get(CalendarItem.START));
		String containernr = map.get(CalendarItem.CONTAINERNR);
		String mrn = map.get(CalendarItem.MRN);
		int kartons = Integer.parseInt(map.get(CalendarItem.KARTONS));
		int units = Integer.parseInt(map.get(CalendarItem.UNITS));
		long beschikbaarop = 0; //De beschikbaar op wordt later aangepast
		//long beschikbaarop = Long.parseLong(map.get(CalendarItem.BESCHIKBAAR));
		boolean gasmeting = Boolean.parseBoolean(map.get(CalendarItem.GASMETING));
		char categorie = map.get(CalendarItem.CATEGORIE).charAt(0);
		//om met fouten in status om te gaan
		int status = 0;
		try {
			status = Integer.parseInt(map.get(CalendarItem.TYPEID));
		} catch (NumberFormatException e) {
			System.err.println("fout in status (Splitter makeCalendarItem): " + e.getMessage());
		}
		String opmerkingen = map.get(CalendarItem.OPMERKINGEN);
		String mongoid = map.get(CalendarItem.MONGOID);
		
		return new CalendarItem(bookingnr, start, containernr, mrn, kartons, units, beschikbaarop, gasmeting, categorie, status, opmerkingen, mongoid);
	}
}

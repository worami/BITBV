package backend;

import java.util.*;
import calendar.*;

/**
 * Klasse om die enorme string te splitten in ContainerItems
 * @author alexander
 *
 */
public class Splitter {
	static String testString1 = "[{Dit is String 1},{Dit is String 2},{Dit is String, 3},{Dit is String 4}]";
	
	public static void main(String[] args) {
		split(testString1);
	}
	
	public static ArrayList<CalendarItem> split(String str) {
		ArrayList<CalendarItem> result = new ArrayList<CalendarItem>();
		str = str.substring(2, str.length()-2);
		
		Scanner scanner = new Scanner(str).useDelimiter("\\},\\{");
		while(scanner.hasNext()) {
			System.out.println(scanner.next());
		}
		
		return result;
	}
}

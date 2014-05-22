package backend;

import java.util.List;
import calendar.CalendarItem;

/**
 * Klasse voor het berekenen van het aantal gasgemeten containers in een list.
 * @author alexander
 *
 */
public class GasCalculator {
	
	/**
	 * Berekent het percentage van alle containers van een bepaalde categorie binnen een bepaalde range dat gasgemeten moet worden.
	 * @param list Lijst met alle containers
	 * @param categorie Categorie waar naar gekeken moet worden
	 * @param begin Begin van de range waar naar gekeken moet worden, in seconden
	 * @param eind Eind van de range waar naar gekeken moet worden, in seconden
	 * @return Een double met daarin het percentage containers waarvan aangegeven is dat ze gasgemeten moeten worden
	 */
	public static double calculate(List<CalendarItem> list, char categorie, long begin, long eind) {
		double result = 0.0;
		
		int total = 0; //totale aantal containers van de meegegeven categorie binnen de meegegeven range
		int gasmeting = 0; //aantal gasgemeten containers van de meegegeven categorie binnen de meegegeven range
		for (CalendarItem item : list) {
			if (item.getStart() >= begin && item.getStart() <= eind && item.getCategorie() == categorie) {
				total++;
				if (item.getGasmeting()) {
					gasmeting++;
				}
			}
		}
		try {
			result = (double) gasmeting / total;
		} catch (ArithmeticException e) {
			System.out.println("In GasCalculator.calculate() is een divide by zero opgetreden. Dit kan liggen aan een lege list of aan een verkeerd gedefinieerde range.");
		}
		
		return result;
	}
}

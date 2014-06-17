package ui;

import java.io.*;

import calendar.CalendarItem;
import backend.*;

public class GetterTUI {
	private static Getter getter = new Getter();
	private static final boolean PRINTTIME = true;
	
	private static final String MENU = 
			"s ...... Synchronize\n" +
			"r ...... Ruim database op\n" + 
			"x ...... Sluit programma\n" + 
			"u ...... Update status\n" +
			"! ...... Update spoed\n" +
			"b ...... Bericht";
	
	public static void main(String[] args) throws IOException {
		while(true) {
			System.out.println(MENU);
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String input = br.readLine().toLowerCase();
			try {
				long start = System.currentTimeMillis();
				if (input.charAt(0) == 's') {
					synchronize();
				} else if (input.charAt(0) == 'r') {
					ruimDatabaseOp();
				} else if (input.charAt(0) == 'x') {
					sluitAf();
				} else if (input.charAt(0) == 'u') {
					int status = Integer.parseInt(input.charAt(2) + "");
					//System.out.println("status: " + status);
					update(status);
				} else if (input.charAt(0) == '!'){
					System.out.println("unit: " + input.charAt(2));
					updateSpoed(input.charAt(2));
				} else if (input.charAt(0) == 'b'){
					bericht(input.toString().substring(2));
				} else{
					nietHerkend();
				}
				long time = System.currentTimeMillis() - start;
				if (PRINTTIME) {
					System.out.println("Elapsed time (ms): " + time);
				}
			} catch (StringIndexOutOfBoundsException e) {
				nietHerkend();
			}
		}
	}

	private static void synchronize() {
		getter.synchronize();
	}

	private static void ruimDatabaseOp() {
		getter.ruimDatabaseOp();
		System.out.println("Opgeruimd.");
	}

	private static void sluitAf() {
		System.out.println("Sluit systeem af.");
		System.exit(0);
	}
	
	private static void update(int status){
		getter.updateCalendarItem("OOLU 858002 6", status);
	}
	
	private static void updateSpoed(int nummer){
		if(nummer == 1){
			getter.updateSpoed("OOLU 858002 6");
		}
		
	}
	
	private static void bericht(String bericht){
		getter.sendNotification(bericht);
	}

	private static void nietHerkend() {
		System.out.println("Dit commando wordt niet herkend.");
	}
	
}

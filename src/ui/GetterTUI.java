package ui;

import java.io.*;

import backend.*;

public class GetterTUI {
	private static Getter getter = new Getter();
	private static final boolean PRINTTIME = true;
	
	private static final String MENU = 
			"s ...... Synchronize\n" +
			"r ...... Ruim database op\n" + 
			"x ...... Sluit programma";
	
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
				} else {
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

	private static void nietHerkend() {
		System.out.println("Dit commando wordt niet herkend.");
	}
	
}

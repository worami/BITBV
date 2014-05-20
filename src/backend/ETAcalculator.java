package backend;

public class ETAcalculator {
	public final static int MINUTE = 60;
	public final static int HOUR = 60*MINUTE;
	public final static int DAY = 24*HOUR;
	
	public final static int timeFromGateOut = 20*MINUTE; 				//wanneer de container vertrekt vanuit CTTH
	public final static int timeFromImportGateInPickup = 45*MINUTE; 	//wanneer de container full truck binnenkomt
	public final static int timeFromArrivalPickup = 45*MINUTE; 			//wanneer de container op barge binnenkomt
	public final static int timeFromPickup = 1*DAY; 					//wanneer de container in Rotterdam binnenkomt
	public final static int timeDefault = 0;							//default waarde - hier kunnen we er niks over zeggen
	
	public static void main(String[] args) { //voor testen, boeit verder niet echt iets, mensen lezen dit toch niet.
		System.out.println(eta(0,0,0,1));
		System.out.println(eta(0,0,1,0));
		System.out.println(eta(0,1,0,0));
		System.out.println(eta(1,0,0,0));
		System.out.println(eta(0,0,0,0));
	}
	
	/**
	 * Methode die een verwachte aankomsttijd uitrekent. Deze methode verwacht input in seconden.
	 * LET OP: als alle argumenten 0 zijn, levert deze methode 0 op.
	 * @param pickup
	 * @param arrivalPickup
	 * @param gateOut
	 * @param importGateInPickup
	 * @return
	 */
	public static long eta(long pickup, long arrivalPickup, long gateOut, long importGateInPickup) {
		
		long result = 0;
		if (gateOut != 0) {
			result = gateOut + timeFromGateOut;
		} else if (importGateInPickup != 0) {
			result = importGateInPickup + timeFromImportGateInPickup;
		} else if (arrivalPickup != 0) {
			result = arrivalPickup +timeFromArrivalPickup;
		} else if (pickup != 0) {
			result = pickup + timeFromPickup;
		} else {
			result = timeDefault;
		}
		
		return result;
	}
}

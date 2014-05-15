package calendar;

import backend.httppusher;


public class CalendarItem {
	
	long start; //Pickup  + 4
	//long eind;
	//int duur;
	String containernr; //11 characters ContainerNumber
	String mrn; //18 characters ImportDocNr
	int kartons; //NumberColli
	int units;
	boolean gasmeting;
	char categorie;
	long beschikbaarop; //Pickup + 2
	String opmerkingen;
	int bookingnr; //Booking
	
	final String OPERATORID = "operatorid";
	final String TEMPLATETYPE = "templatetype";
	final String TYPEID = "typeid";
	final String ID = "id";
	final String CALLS = "calls";
	final String ALLDAY = "allDay";
	final String START = "start";
	final String END = "end";
	final String BOOKINGNR = "bookingnr";
	final String CONTAINERNR = "containernr";
	final String MRN = "mrn";
	final String KARTONS = "kartons";
	final String UNITS = "units";
	final String BESCHIKBAAR= "beschikbaarop";
	final String GASMETING = "gasmeting";
	final String CATEGORIE = "categorie";
	final String OPMERKINGEN = "opmerkingen";
	final String TITEL = "title";
	
	final String DBtemptype = "status";
	final int DBoperatorid = 23; 
	
	/**
	 * 
	 * @param bookingnr
	 * @param start
	 * @param containernr
	 * @param mrn
	 * @param kartons
	 */
	public CalendarItem(int bookingnr, long start, String containernr, String mrn, int kartons){
		this.bookingnr = bookingnr;
		this.start = start/1000;
		this.containernr = containernr;
		this.mrn = mrn; 
		this.kartons = kartons;
		
		this.units = 0;
		this.gasmeting = false;
		this.categorie = 'C';
		this.beschikbaarop = this.start;
		this.opmerkingen = "Test";
	}
	
	public int getBookingnr(){
		return bookingnr;
	}
	
	public long getStart(){
		return start;
	}
	
	public long getEind(){
		return start + 900;
	}
	
	public String getContainernr(){
		return containernr;
	}
	
	public String getMRN(){
		return mrn;
	}
	
	public int getKartons(){
		return kartons;
	}

	public int getUnits(){
		return units;
	}
	
	public boolean getGasmeting(){
		return gasmeting;
	}
	
	public char getCategorie(){
		return categorie;
	}
	
	public long getBeschikbaarOp(){
		return beschikbaarop;
	}
	
	public String getOpmerkingen(){
		return opmerkingen;
	}
	
	public String toString(){
		return "Container: "+ containernr + ", Boeking: " + bookingnr;
	}
	
	public String toHTTPString(){
		String result = "{\"" + this.OPERATORID + "\":" + this.DBoperatorid + ',' +
				"\"" + this.TEMPLATETYPE + "\":\"" + this.DBtemptype + "\"," +
				//"\"" + this.TYPEID + "\":" + 4 + ',' +
				"\"" + this.ID + "\":" + this.getBookingnr() + ',' +
				"\"" + this.CALLS + "\":\"" + this.getBookingnr() + "\"," +
				"\"" + this.ALLDAY + "\":" + false + ',' +
				"\"" + this.START + "\":" + this.getStart() + ',' +
				"\"" + this.END + "\":" + this.getEind() + ',' +
				"\"" + this.BOOKINGNR + "\":" + this.getBookingnr() + ',' +
				"\"" + this.CONTAINERNR + "\":\"" + this.getContainernr() + "\"," +
				"\"" + this.MRN + "\":\"" + this.getMRN() + "\"," +
				"\"" + this.KARTONS + "\":" + this.getKartons() + ',' +
				"\"" + this.TITEL + "\":\"" + this.getKartons() + "\"," +
				"\"" + this.UNITS + "\":" + 0 + ',' +
				"\"" + this.BESCHIKBAAR + "\":" + this.getBeschikbaarOp() + ',' +
				"\"" + this.GASMETING + "\":" + false + ',' +
				"\"" + this.CATEGORIE + "\":" + "\"A\"" + ',' +
				"\"" + this.OPMERKINGEN + "\":\"" + "Ik ben een container! Echt!" + "\"}";
		
		//String content = "{\"operatorid\":23,\"templatetype\":\"status\",\"typeid\":2,\"id\":7,\"allDay\":false,\"start\":1399970000,\"end\":1399973000,\"title\":\"HOI b\"}";

		
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		 
		CalendarItem test = new CalendarItem(12, 13, "test", "test", 12);
		System.out.println(test.toHTTPString());
 
	}
	
	
}

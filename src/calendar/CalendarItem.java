package calendar;


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
		this.start = start;
		this.containernr = containernr;
		this.mrn = mrn; 
		this.kartons = kartons;
		
		this.units = 0;
		this.gasmeting = false;
		this.categorie = 'C';
		this.beschikbaarop = this.start - 17280000;
		this.opmerkingen = "Test";
	}
	
	public int getBookingnr(){
		return bookingnr;
	}
	
	public long getStart(){
		return start;
	}
	
	public long getEind(){
		return start + 90000;
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
	
	
}

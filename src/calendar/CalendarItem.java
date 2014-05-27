package calendar;

import java.text.SimpleDateFormat;
import java.util.Date;

import backend.ETAcalculator;
import backend.Hasher;

public class CalendarItem {
	
	private long start; //Pickup  + 4
	private String containernr; //11 characters ContainerNumber
	private String mrn; //18 characters ImportDocNr
	private int kartons; //NumberColli
	private int units;
	private boolean gasmeting;
	private char categorie;
	private long beschikbaarop; //Pickup + 2
	private String opmerkingen;
	private int bookingnr; //Booking
	private int status;
	private boolean spoed;
	private String mongoid;
	
	public static final String OPERATORID = "operatorid";
	public static final String TEMPLATETYPE = "templatetype";
	public static final String TYPEID = "typeid";
	public static final String ID = "id";
	public static final String CALLS = "calls";
	public static final String ALLDAY = "allDay";
	public static final String START = "start";
	public static final String END = "end";
	public static final String BOOKINGNR = "bookingnr";
	public static final String CONTAINERNR = "containernr";
	public static final String MRN = "mrn";
	public static final String KARTONS = "kartons";
	public static final String UNITS = "units";
	public static final String BESCHIKBAAR= "beschikbaarop";
	public static final String GASMETING = "gasmeting";
	public static final String CATEGORIE = "categorie";
	public static final String OPMERKINGEN = "opmerkingen";
	public static final String TITEL = "title";
	public static final String SPOED = "spoed";
	public static final String MONGOID = "_id";
	
	public static final int STATUSLEEG = 0;
	public static final int STATUSACTIEVEREIST = 1;
	public static final int STATUSACTIEVEREISTUPDATE = 2;
	public static final int STATUSVOORSTEL = 3;
	//public static final int STATUSVOORSTELSPOED = 4;
	public static final int STATUSGOEDGEKEURD = 5;
	public static final int STATUSGASMETINGMIS = 6;
	public static final int STATUSVERTRAGING = 7;
	public static final int STATUSBEZORGD = 8;
	
	//public static final String DBtemptype = "statussupplychain";
	//public static final int DBoperatorid = 23; 
	
	/**
	 * 
	 * @param bookingnr
	 * @param start
	 * @param containernr
	 * @param mrn
	 * @param kartons
	 */
	public CalendarItem(int bookingnr, long start, String containernr, String mrn, int kartons, long beschikbaarop){
		this.bookingnr = bookingnr;
		this.start = start;
		this.containernr = containernr;
		this.mrn = mrn; 
		this.kartons = kartons;
		this.beschikbaarop = beschikbaarop;
		
		//initiele waarden
		this.units = 0;
		this.gasmeting = false;
		this.categorie = 'C';
		this.status = CalendarItem.STATUSACTIEVEREIST;
		this.opmerkingen = "";
		this.mongoid = "";
		this.spoed = false;
	}
	
	/**
	 * 
	 * @param bookingnr
	 * @param start
	 * @param containernr
	 * @param mrn
	 * @param kartons
	 * @param units
	 * @param ETA
	 * @param gasmeting
	 * @param categorie
	 * @param status
	 * @param opmerkingen
	 * @param mongoid
	 * @param spoed
	 */
	public CalendarItem(int bookingnr, long start, String containernr, String mrn, int kartons, int units, long ETA, boolean gasmeting, char categorie, int status, String opmerkingen, String mongoid, boolean spoed){
		this.bookingnr = bookingnr;
		this.start = start;
		this.containernr = containernr;
		this.mrn = mrn;
		this.kartons = kartons;
		this.units = units;
		this.beschikbaarop = ETA;
		this.gasmeting = gasmeting;
		this.categorie = categorie;
		this.status = status;
		this.opmerkingen = opmerkingen;
		this.spoed = spoed;
		this.mongoid = mongoid;
	}
	
	public int getBookingnr(){
		return bookingnr;
	}
	
	public long getStart(){
		return start;
	}
	
	public long getEind(){
		return start + 1800;
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
	
	public int getStatus(){
		return status;
	}
	
	public String getMondoID(){
		return this.mongoid;
	}
	
	public boolean getSpoed(){
		return this.spoed;
	}
	
	public void setBookingnr(int bookingnr){
		this.bookingnr = bookingnr;
	}
	
	public void setStart(long start){
		this.start = start;
	}
	
	public void setContainernr(String containernummer){
		this.containernr = containernummer;
	}
	
	public void setMRN(String MRN){
		this.mrn = MRN;
	}
	
	public void setKartons(int kartons){
		this.kartons = kartons;
	}
	
	public void setUnits(int units){
		this.units = units;
	}
	
	public void setGasmeting(boolean gasmeting){
		this.gasmeting = gasmeting;
	}
	
	public void setCategorie(char categorie){
		this.categorie = categorie;
	}
	
	public void setBeschikbaarOp(long beschikbaar){
		this.beschikbaarop = beschikbaar;
	}
	
	public void setOpmerkingen(String opmerkingen){
		this.opmerkingen = opmerkingen;
	}
	
	public void setStatus(int status){//TODO het zou volgens mij mooi zijn om hier met public constantes te werken
		this.status = status;
	}
	
	public void setMondoID(String id){
		this.mongoid = id;
	}
	
	public void setSpoed(boolean spoed){
		this.spoed = spoed;
	}
	
	public String toString(){
		return "Container: "+ this.getContainernr() + '\n'
				+ "Boekingnr: " + this.getBookingnr() + '\n'
				+ CalendarItem.TYPEID + ": " + this.getStatus() + '\n'
				+ CalendarItem.CALLS + ": " + this.getBookingnr() + '\n'
				+ CalendarItem.ALLDAY + ": " + false + '\n'
				+ CalendarItem.START + ": " + this.getStart() + '\n'
				+ CalendarItem.END + ": " + this.getEind() + '\n'
				+ CalendarItem.BOOKINGNR + ": " + this.getBookingnr() + '\n'
				+ CalendarItem.CONTAINERNR + ": " + this.getContainernr() + '\n'
				+ CalendarItem.MRN + ": " + this.getMRN() + '\n'
				+ CalendarItem.KARTONS + ": " + this.getKartons() + '\n'
				+ CalendarItem.TITEL + ": " + this.getKartons() + '\n'
				+ CalendarItem.UNITS + ": " + this.getUnits() + '\n'
				+ CalendarItem.BESCHIKBAAR + ": " + this.beschikbaarop + '\n'
				+ CalendarItem.GASMETING + ": " + this.getGasmeting() + '\n'
				+ CalendarItem.CATEGORIE + ": " + this.getCategorie() + '\n'
				+ CalendarItem.OPMERKINGEN + ": " + this.getOpmerkingen();
	}
	
	public String toMailString() {
		String result = "<table>";
		
		result += tablify("Containernr:",this.getContainernr());
		result += tablify("Boekingnr: ",this.getBookingnr());
		result += tablify(CalendarItem.TYPEID,this.getStatus());
		//result += tablify(CalendarItem.CALLS,this.getBookingnr());
		//result += tablify(CalendarItem.ALLDAY,false);
		result += tablify(CalendarItem.START,this.getStart());
		//result += tablify(CalendarItem.END,this.getEind());
		//result += tablify(CalendarItem.BOOKINGNR,this.getBookingnr());
		//result += tablify(CalendarItem.CONTAINERNR,this.getContainernr());
		result += tablify(CalendarItem.MRN,this.getMRN());
		result += tablify(CalendarItem.KARTONS,this.getKartons());
		//result += tablify(CalendarItem.TITEL,this.getKartons());
		result += tablify(CalendarItem.UNITS,this.getUnits());
		result += tablify(CalendarItem.BESCHIKBAAR,this.beschikbaarop);
		result += tablify(CalendarItem.GASMETING,this.getGasmeting());
		result += tablify(CalendarItem.CATEGORIE,this.getCategorie());
		result += tablify(CalendarItem.OPMERKINGEN,this.getOpmerkingen());
		
		result += "</table>";
		return result;
	}
	
	private String tablify(String td1, String td2) {
		return "<tr><td>" + td1 + "</td><td>" + td2 + "</td></tr>";
	}
	//Overload methods for Tablify
	private String tablify(String td1, int td2) {
		return tablify(td1,""+td2);
	}
	
	private String tablify(String td1, boolean td2) {
		return tablify(td1,""+td2);
	}
	
	private String tablify(String td1, long td2) {
		Date date = new Date(td2*1000);
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm");
		return tablify(td1,""+sdf.format(date));
	}
	
	private String tablify(String td1, char td2) {
		return tablify(td1,""+td2);
	}
	
	/**
	public String toHTTPString(){
		SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy 'om' HH:mm");
		String date = "\"" + sdf.format((this.getBeschikbaarOp() - (ETAcalculator.HOUR * 2))*1000) + "\"";
		
		//Resulteerd in een string zoals: "{\"operatorid\":23,\"templatetype\":\"status\",\"typeid\":2,
		//\"id\":7,\"allDay\":false,\"start\":1399970000,\"end\":1399973000,\"title\":\"HOI b\"}";
		String result = "{\"" + CalendarItem.OPERATORID + "\":" + CalendarItem.DBoperatorid + ',' +
				"\"" + CalendarItem.TEMPLATETYPE + "\":\"" + CalendarItem.DBtemptype + "\"," +
				"\"" + CalendarItem.TYPEID + "\":" + this.getStatus() + ',' +
				"\"" + CalendarItem.ID + "\":" + Hasher.hash(this.getBookingnr(), this.getContainernr()) + ',' +
				"\"" + CalendarItem.CALLS + "\":\"" + this.getBookingnr() + "\"," +
				"\"" + CalendarItem.ALLDAY + "\":" + false + ',' +
				"\"" + CalendarItem.START + "\":" + this.getStart() + ',' +
				"\"" + CalendarItem.END + "\":" + this.getEind() + ',' +
				"\"" + CalendarItem.BOOKINGNR + "\":" + this.getBookingnr() + ',' +
				"\"" + CalendarItem.CONTAINERNR + "\":\"" + this.getContainernr() + "\"," +
				"\"" + CalendarItem.MRN + "\":\"" + this.getMRN() + "\"," +
				"\"" + CalendarItem.KARTONS + "\":" + this.getKartons() + ',' +
				"\"" + CalendarItem.TITEL + "\":\"" + this.getKartons() + "\"," +
				"\"" + CalendarItem.UNITS + "\":" + this.getUnits() + ',' +
				"\"" + CalendarItem.BESCHIKBAAR + "\":" + date + ',' +
				"\"" + CalendarItem.GASMETING + "\":" + this.getGasmeting() + ',' +
				"\"" + CalendarItem.CATEGORIE + "\":\"" + this.getCategorie() + "\"," +
				"\"" + CalendarItem.OPMERKINGEN + "\":\"" + this.getOpmerkingen() + "\"}";
		return result;
	}**/
	
	public boolean equals(CalendarItem e){
		return (Hasher.hash(this.getBookingnr(), this.getContainernr()) == Hasher.hash(e.getBookingnr(), e.getContainernr()));
	}
	
	public static void main(String[] args) throws Exception {
		 
		CalendarItem test = new CalendarItem(12, 13, "test", "test", 12, 12);
 
	}
	
	
}

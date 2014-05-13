package backend;

public class Hasher {
	
	/**
	 * Constructor; doet verder niks
	 */
	public Hasher() {
		
	}

	/**
	 * Levert een unieke long op op basis van een bookingnummer en een containernummer.
	 * BELANGRIJK: bookingnummer moet kleiner zijn dan 524288, containernummer moet beginnen met 4 hoofdletters gevolgd door 7 nummers. 
	 * Containernummer mag spaties bevatten
	 * @param bookingnr Het bookingnummer (bijvoorbeeld 123456)
	 * @param containernr Het containernummer (bijvoorbeeld "CMPU 194670 5")
	 * @return unieke hash
	 */
	public long hash(int bookingnr, String containernr) {
	
		String cleanContainerNr = clean(containernr);
		
		String cnr1 = cleanContainerNr.substring(0,4);
		String cnr2 = cleanContainerNr.substring(4);
		
		String bin_bookingnr = 	"0000000000000000000"; //19
		String bin_cnr1 = 	"0000000000000000000"; //19
		String bin_cnr2 = 	"000000000000000000000000"; //24
		
		bin_bookingnr += Integer.toBinaryString(bookingnr);
		bin_bookingnr = bin_bookingnr.substring(bin_bookingnr.length()-19); //levert een binary substring van het bookingnr op met leading nullen
		
		bin_cnr1 += Integer.toBinaryString(nr(cnr1));
		bin_cnr1 = bin_cnr1.substring(bin_cnr1.length()-19); //levert een binary substring van de letters van het containernr
		
		long l_cnr2 = Long.parseLong(cnr2);
		bin_cnr2 += Long.toBinaryString(l_cnr2);
		bin_cnr2 = bin_cnr2.substring(bin_cnr2.length()-24);

		System.out.println(bin_bookingnr);
		System.out.println(bin_cnr1);
		System.out.println(bin_cnr2);
		
		String hashString = bin_bookingnr + bin_cnr1 + bin_cnr2;
		return Long.parseLong(hashString,2);
	}
	
	private String clean(String str) { //even apart gemaakt ivm testing
		return str.replaceAll(" ","");
	}
	
	//requires length 4, allemaal hoofdletters
	private int nr(String str) {
		int result = 0;
		for (int i = 0; i < 4; i++) {
			result += (str.charAt(i) - 'A') * Math.pow(26,3-i);
		}
		return result;
	}
}

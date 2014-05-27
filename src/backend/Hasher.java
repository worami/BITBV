package backend;

public class Hasher {
	public static final int NRRUNS = 100000; //aantal runs bij testen, moet kleiner blijven dan 300000
	
	public static void main(String[] args) {
		long timeStart = System.currentTimeMillis();
		for (int i = 0; i < NRRUNS; i++) {
			long x = hash(123456+i,"CMDP 123456 7");
		}
		long time = System.currentTimeMillis() - timeStart;
		System.out.println("Time taken for " + NRRUNS + " is: " + time + " ms.");
		
		Object[] reverse = reverse(hash(214365,"CMDP 123456 7"));

		System.out.println(reverse[0]);
		System.out.println(reverse[1]);
		
	}
	
	/**
	 * Levert een unieke long op op basis van een bookingnummer en een containernummer.
	 * BELANGRIJK: bookingnummer moet kleiner zijn dan 524288, containernummer moet beginnen met 4 hoofdletters gevolgd door 7 nummers. 
	 * Containernummer mag spaties bevatten
	 * @param bookingnr Het bookingnummer (bijvoorbeeld 123456)
	 * @param containernr Het containernummer (bijvoorbeeld "CMPU 194670 5")
	 * @return unieke hash
	 */
	public static long hash(int bookingnr, String containernr) {
	
		String cleanContainerNr = clean(containernr);
		
		String cnr1 = cleanContainerNr.substring(0,4);
		String cnr2 = cleanContainerNr.substring(4);
		
		String bin_bookingnr = 	"0000000000000000000"; //19
		String bin_cnr1 = 		"0000000000000000000"; //19
		String bin_cnr2 = 		"000000000000000000000000"; //24
		
		bin_bookingnr += Integer.toBinaryString(bookingnr);
		bin_bookingnr = bin_bookingnr.substring(bin_bookingnr.length()-19); //levert een binary substring van het bookingnr op met leading nullen
		
		bin_cnr1 += Integer.toBinaryString(nr(cnr1));
		bin_cnr1 = bin_cnr1.substring(bin_cnr1.length()-19); //levert een binary substring van de letters van het containernr
		
		long l_cnr2 = Long.parseLong(cnr2);
		bin_cnr2 += Long.toBinaryString(l_cnr2);
		bin_cnr2 = bin_cnr2.substring(bin_cnr2.length()-24);

		/* Code voor debuggen
		System.out.println(bin_bookingnr);
		System.out.println(bin_cnr1);
		System.out.println(bin_cnr2);*/
		
		String hashString = bin_bookingnr + bin_cnr1 + bin_cnr2;
		return Long.parseLong(hashString,2);
	}
	
	private static String clean(String str) { //even apart gemaakt ivm testing
		return str.replaceAll(" ","");
	}
	
	//requires length 4, allemaal hoofdletters
	private static int nr(String str) {
		int result = 0;
		for (int i = 0; i < 4; i++) {
			result += (str.charAt(i) - 'A') * Math.pow(26,3-i);
		}
		return result;
	}
	
	//levert bookingnr en containernr op.
	public static final Object[] reverse(long hash) {
		Object[] result = new Object[2];
		
		String binaryString = Long.toBinaryString(hash);
		String binary_bookingnr = binaryString.substring(0, binaryString.length()-24-19);
		String binary_cnr1 = binaryString.substring(binaryString.length()-24-19, binaryString.length()-24);
		String binary_cnr2 = binaryString.substring(binaryString.length()-24, binaryString.length());
		result[0] = Integer.parseInt(binary_bookingnr, 2);
		
		
		int cnr1 = Integer.parseInt(binary_cnr1, 2);
		char cnr1_4 = (char) (cnr1%26+'A');
		cnr1/=26;
		char cnr1_3 = (char) (cnr1%26+'A');
		cnr1/=26;
		char cnr1_2 = (char) (cnr1%26+'A');
		cnr1/=26;
		char cnr1_1 = (char) (cnr1%26+'A');
		
		String cnr2_temp = Long.parseLong(binary_cnr2,2)+"";
		String cnr2 = cnr2_temp.substring(0,6)+" "+ cnr2_temp.substring(6,7);
		
		result[1] = ""+cnr1_1 + cnr1_2 + cnr1_3 + cnr1_4 + " " + cnr2;
		return result;
	}
}

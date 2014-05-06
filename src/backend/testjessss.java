package backend;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class testjessss {
	
	public static void main(String args[]){
		try {
			System.out.println("testejsss whoehoe ip: " + Inet4Address.getLocalHost().getHostAddress().toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Connector nice = new Connector();
		//System.out.println(nice.sql("SELECT * FROM modalitycontainerstatisticsinfocumm;"));
		Getter g = new Getter();
		for(String[] e : g.getContainers()){
			for(String s : e){
				System.out.print(s);
			}
			System.out.println();
		}
	}

}

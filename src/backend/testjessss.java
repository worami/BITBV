package backend;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import calendar.CalendarItem;

public class testjessss {
	
	public static void main(String args[]){
		
		//Connector nice = new Connector();
		//System.out.println(nice.sql("SELECT * FROM modalitycontainerstatisticsinfocumm;"));
		/**Getter g = new Getter();
		for(String[] e : g.getContainers()){
			for(String s : e){
				System.out.print(s);
			}
			System.out.println();
		}**/
		
		Getter get = new Getter();
		get.synchronize();
		
		//get.getMongoConnecotr().deleteObjects();
		get.getMongoConnecotr().getData();

		while(true){
			//long millis = System.currentTimeMillis();
			
			try {
				get.synchronize();
				System.out.println("update!");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//more.getData();
		//536b79d785ca09d7cf491ffb
		
		//more.putCalendarItem(7777);
	}

}

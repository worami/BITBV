package backend;

import java.util.List;

import calendar.CalendarItem;

public class Getter {
	
	Connector connection;
	MongoConnector mongo;
	
	public Getter(){
		connection = new Connector();
		mongo = new MongoConnector();
	}
	
	public void synchronize(){
		for(CalendarItem c : connection.getCalendarList()){
			System.out.println(c.toString());
			mongo.putCalendarItem(c);
		}
	}
	
	public List<String[]> getContainers(){
		List<String[]> containers = connection.sql("SELECT  PickupDate, ContainerNumber FROM modalitycontainerstatisticsinfocumm;");
		return containers;
	}

}

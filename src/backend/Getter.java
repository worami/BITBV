package backend;

import java.util.List;

import calendar.CalendarItem;

public class Getter {
	
	Connector connection;
	MongoConnector mongo;
	httppusher http;
	
	public Getter(){
		connection = new Connector();
		mongo = new MongoConnector();
		http = new httppusher();
	}
	
	public void synchronize(){
		for(CalendarItem c : connection.getCalendarList()){
			System.out.println(c.toString());
			//mongo.putCalendarItem(c);
			http.sendPost(c);
		}
	}
	
	public List<String[]> getContainers(){
		List<String[]> containers = connection.sql("SELECT  PickupDate, ContainerNumber FROM modalitycontainerstatisticsinfocumm;");
		return containers;
	}
	
	public MongoConnector getMongoConnecotr(){
		return mongo;
	}

}
